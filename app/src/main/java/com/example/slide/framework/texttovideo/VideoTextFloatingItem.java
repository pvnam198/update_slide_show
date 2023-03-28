package com.example.slide.framework.texttovideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.slide.ui.edit_image.framework.AddTextProperties;
import com.example.slide.ui.edit_image.utils.SystemUtil;
import com.example.slide.util.FileUtils;
import com.example.slide.videolib.VideoConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

public class VideoTextFloatingItem extends FloatingItemAdded implements Serializable {

    private final Context context;
    private final TextPaint textPaint;
    private Drawable drawable;
    private StaticLayout staticLayout;
    private int textWidth;
    private int textHeight;
    private int textAlpha;
    private int textColor;
    private int paddingWidth;
    private int paddingHeight;
    private int backgroundColor;
    private int backgroundAlpha;
    private int backgroundBorder;
    private boolean isShowBackground;
    private BitmapDrawable backgroundDrawable;
    private AddTextProperties.TextShadow textShadow;
    private String text;
    private Layout.Alignment textAlign;
    private AddTextProperties addTextProperties;
    private long id;
    private boolean isHandling = false;
    private float startX;
    private float startY;

    public int getTextWidth() {
        return textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getPaddingWidth() {
        return paddingWidth;
    }

    public int getPaddingHeight() {
        return paddingHeight;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public int getBackgroundBorder() {
        return backgroundBorder;
    }

    public boolean isShowBackground() {
        return isShowBackground;
    }

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public boolean isHandling() {
        return isHandling;
    }

    public void setHandling(boolean handling) {
        isHandling = handling;
    }

    public boolean isInTime(int currentTime) {
        if (isFullTime) return true;
        return currentTime >= startTime && currentTime <= endTime;
    }

/*    public void setId(long id) {
        this.id = id;
    }*/

    public long getId() {
        return id;
    }

    public void setFullTime(boolean fullTime) {
        isFullTime = fullTime;
    }

    public boolean isFullTime() {
        return isFullTime;
    }

    /**
     * Upper bounds for text size.
     * This acts as a starting point for resizing.
     */
    private float maxTextSizePixels;

    /**
     * Lower bounds for text size.
     */
    private float minTextSizePixels;

    /**
     * Line spacing multiplier.
     */
    private float lineSpacingMultiplier = 1.0f;

    /**
     * Additional line spacing.
     */
    private float lineSpacingExtra = 0.0f;

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getTotalTime() {
        return endTime - startTime;
    }

    public VideoTextFloatingItem(@NonNull Context context) {
        this.context = context;
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(25);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public VideoTextFloatingItem(@NonNull Context context, AddTextProperties addTextProperties) {

        id = System.currentTimeMillis();
        this.context = context;
        this.addTextProperties = addTextProperties;
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        setTextSize(addTextProperties.getTextSize())
                .setTextWidth(addTextProperties.getTextWidth())
                .setTextHeight(addTextProperties.getTextHeight())
                .setText(addTextProperties.getText())
                .setPaddingWidth(SystemUtil.dpToPx(context, addTextProperties.getPaddingWidth()))
                .setBackgroundBorder(SystemUtil.dpToPx(context, addTextProperties.getBackgroundBorder()))
                .setTextShadow(addTextProperties.getTextShadow())
                .setTextColor(addTextProperties.getTextColor())
                .setTextAlpha(addTextProperties.getTextAlpha())
                .setBackgroundColor(addTextProperties.getBackgroundColor())
                .setBackgroundAlpha(addTextProperties.getBackgroundAlpha())
                .setShowBackground(addTextProperties.isShowBackground())
                .setTextColor(addTextProperties.getTextColor())
                .setTypeface(addTextProperties.getTypeface(context))
                .setTextAlign(addTextProperties.getTextAlign())
                .setTextShare(addTextProperties.getTextShader())
                .resizeText();
    }

    public void updateConfig(String newText, int width, int height) {
        addTextProperties.setText(newText);
        addTextProperties.setTextWidth(width);
        addTextProperties.setTextHeight(height);
        setTextSize(addTextProperties.getTextSize())
                .setTextWidth(addTextProperties.getTextWidth())
                .setTextHeight(addTextProperties.getTextHeight())
                .setText(addTextProperties.getText())
                .setPaddingWidth(SystemUtil.dpToPx(context, addTextProperties.getPaddingWidth()))
                .setBackgroundBorder(SystemUtil.dpToPx(context, addTextProperties.getBackgroundBorder()))
                .setTextShadow(addTextProperties.getTextShadow())
                .setTextColor(addTextProperties.getTextColor())
                .setTextAlpha(addTextProperties.getTextAlpha())
                .setBackgroundColor(addTextProperties.getBackgroundColor())
                .setBackgroundAlpha(addTextProperties.getBackgroundAlpha())
                .setShowBackground(addTextProperties.isShowBackground())
                .setTextColor(addTextProperties.getTextColor())
                .setTypeface(addTextProperties.getTypeface(context))
                .setTextAlign(addTextProperties.getTextAlign())
                .setTextShare(addTextProperties.getTextShader())
                .resizeText();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Matrix matrix = getMatrix();
        canvas.save();
        canvas.concat(matrix);
        if (isShowBackground) {
            Paint paint = new Paint();
            if (backgroundDrawable != null) {
                Shader shader = new BitmapShader(backgroundDrawable.getBitmap(), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
                paint.setShader(shader);
                paint.setAlpha(backgroundAlpha);
            } else {
                paint.setARGB(backgroundAlpha, Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
            }
            canvas.drawRoundRect(0, 0, textWidth, textHeight, backgroundBorder, backgroundBorder, paint);
            canvas.restore();

            canvas.save();
            canvas.concat(matrix);
        }
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        int dx = paddingWidth;
        int dy = textHeight / 2 - staticLayout.getHeight() / 2;
        // center vertical
        canvas.translate(dx, dy);
        staticLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        canvas.restore();
    }

    public AddTextProperties getAddTextProperties() {
        return addTextProperties;
    }

    public VideoTextFloatingItem setTextShadow(AddTextProperties.TextShadow textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public VideoTextFloatingItem setBackgroundBorder(int backgroundBorder) {
        this.backgroundBorder = backgroundBorder;
        return this;
    }

    public VideoTextFloatingItem setShowBackground(boolean showBackground) {
        this.isShowBackground = showBackground;
        return this;
    }

    public VideoTextFloatingItem setBackgroundDrawable(BitmapDrawable backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
        return this;
    }

    @Override
    public int getWidth() {
        return textWidth;
    }

    @Override
    public int getHeight() {
        return textHeight;
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }

    public int getTextAlpha() {
        return textAlpha;
    }

    public VideoTextFloatingItem setTextAlpha(int textAlpha) {
        this.textAlpha = textAlpha;
        return this;
    }

    @NonNull
    @Override
    public VideoTextFloatingItem setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        textPaint.setAlpha(alpha);
        return this;
    }

    @Override
    public int getAlpha() {
        return textPaint.getAlpha();
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public VideoTextFloatingItem setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public VideoTextFloatingItem setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public VideoTextFloatingItem setBackgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        return this;
    }

    public VideoTextFloatingItem setTextWidth(int width) {
        this.textWidth = width;
        return this;
    }

    public VideoTextFloatingItem setTextHeight(int height) {
        this.textHeight = height;
        return this;
    }

    @NonNull
    public VideoTextFloatingItem setDrawable(@NonNull Drawable drawable, @Nullable Rect region) {
        this.drawable = drawable;
        //realBounds.set(0, 0, getWidth(), getHeight());
        if (region == null) {
            //textRect.set(0, 0, getWidth(), getHeight());
        } else {
            //textRect.set(region.left, region.top, region.right, region.bottom);
        }
        return this;
    }

    @NonNull
    public VideoTextFloatingItem setTypeface(@Nullable Typeface typeface) {
        textPaint.setTypeface(typeface);
        return this;
    }

    @NonNull
    public VideoTextFloatingItem setTextSize(int textSize) {
        this.textPaint.setTextSize(convertSpToPx(textSize));
        return this;
    }

    @NonNull
    public VideoTextFloatingItem setTextShare(@Nullable Shader share) {
        textPaint.setShader(share);
        return this;
    }

    @NonNull
    public VideoTextFloatingItem setTextColor(@ColorInt int color) {
        this.textColor = color;
        return this;
    }

    @NonNull
    public VideoTextFloatingItem setTextAlign(@NonNull int alignment) {
        switch (alignment) {
            case View.TEXT_ALIGNMENT_CENTER:
                textAlign = Layout.Alignment.ALIGN_CENTER;
                break;
            case View.TEXT_ALIGNMENT_TEXT_START:
                textAlign = Layout.Alignment.ALIGN_NORMAL;
                break;
            case View.TEXT_ALIGNMENT_TEXT_END:
                textAlign = Layout.Alignment.ALIGN_OPPOSITE;
                break;
        }
        return this;
    }

    public VideoTextFloatingItem setPaddingWidth(int paddingWidth) {
        this.paddingWidth = paddingWidth;
        return this;
    }

    @NonNull
    public VideoTextFloatingItem setText(@Nullable String text) {
        this.text = text;
        return this;
    }

    @Nullable
    public String getText() {
        return text;
    }

    private void resizeText() {
        final CharSequence text = getText();

        if (text == null
                || text.length() <= 0
        ) {
            return;
        }

        if (textShadow != null)
            textPaint.setShadowLayer(textShadow.getRadius(), textShadow.getDx(), textShadow.getDy(), textShadow.getColorShadow());
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setARGB(textAlpha, Color.red(textColor), Color.green(textColor), Color.blue(textColor));

        int width = textWidth - paddingWidth * 2;
        if (width <= 0)
            width = 100;
        staticLayout =
                new StaticLayout(this.text, textPaint, width, textAlign, lineSpacingMultiplier,
                        lineSpacingExtra, true);
    }

    @Override
    VideoTextExport convertToExport(Context context, int quality, int width, int height) {
        Log.d("kimkaka", "matrix: " + getMatrix());

        float[] values = new float[9];
        getMatrix().getValues(values);
        int x = (int) values[Matrix.MTRANS_X];
        int y = (int) values[Matrix.MTRANS_Y];
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas saveCanvas = new Canvas(bitmap);
        Matrix matrix = getMatrix();
//        matrix.setTranslate(0,0);
        saveCanvas.save();
        saveCanvas.concat(matrix);
        staticLayout.draw(saveCanvas);
        saveCanvas.restore();
        saveCanvas.save();
        File out = new File(FileUtils.INSTANCE.getFramesTempDir(context), "" + System.currentTimeMillis() + ".PNG");
        Bitmap outbitmap = Bitmap.createScaledBitmap(bitmap, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), false);
        try {
            outbitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(out));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new VideoTextExport(out.getAbsolutePath(), 0, 0, isFullTime, startTime, endTime);
    }

    private float convertSpToPx(float scaledPixels) {
        return scaledPixels * context.getResources().getDisplayMetrics().scaledDensity;
    }
}