package com.example.slide.framework.texttovideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.example.slide.util.FileUtils;
import com.example.slide.videolib.VideoConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

import timber.log.Timber;

public class DrawableVideoFloatingItem extends FloatingItemAdded implements Serializable {

    private Drawable drawable;
    private Rect realBounds;

    private long id;
    private int startTime;
    private int endTime;
    private boolean isFullTime = true;

    private String iconPath;

    public boolean isInTime(int currentTime) {
        if (isFullTime) return true;
        return currentTime >= startTime && currentTime <= endTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFullTime() {
        return isFullTime;
    }

    public void setFullTime(boolean fullTime) {
        isFullTime = fullTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public DrawableVideoFloatingItem(Drawable drawable, String iconPath, int startTime, int endTime) {
        id = System.currentTimeMillis();
        this.drawable = drawable;
        this.iconPath = iconPath;
        this.startTime = startTime;
        this.endTime = endTime;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public DrawableVideoFloatingItem setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        drawable.setBounds(realBounds);
        drawable.draw(canvas);
        canvas.restore();
    }

    @NonNull
    @Override
    public DrawableVideoFloatingItem setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        drawable.setAlpha(alpha);
        return this;
    }

    @Override
    public int getAlpha() {
        return drawable.getAlpha();
    }

    @Override
    public int getWidth() {
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getIntrinsicHeight();
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }

    @Override
    VideoTextExport convertToExport(Context context, int quality, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Timber.d("log_wd width 0: "+width);
        Timber.d("log_wd height 0: "+height);
        Canvas saveCanvas = new Canvas(bitmap);
        saveCanvas.save();
        saveCanvas.concat(getMatrix());
        drawable.setBounds(realBounds);
        drawable.draw(saveCanvas);
        saveCanvas.restore();
        Timber.d("log_wd width 1: "+VideoConfig.INSTANCE.getVideoWidth());
        Timber.d("log_wd height 1: "+ VideoConfig.INSTANCE.getVideoHeight());
        File out = new File(FileUtils.INSTANCE.getFramesTempDir(context), "" + System.currentTimeMillis() + ".PNG");
        Bitmap outbitmap = Bitmap.createScaledBitmap(bitmap, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), false);
        try {
            outbitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(out));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new VideoTextExport(out.getAbsolutePath(), 0, 0, isFullTime, startTime, endTime);
    }
}
