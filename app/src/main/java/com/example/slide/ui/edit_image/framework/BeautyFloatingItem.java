package com.example.slide.ui.edit_image.framework;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.example.slide.ui.edit_image.utils.SystemUtil;

public class BeautyFloatingItem extends FloatingItem {

    private Drawable drawable;
    private Rect realBounds;
    private int drawableSizeBoobs, drawableSizeHip1_Width, drawableSizeHip1_Height, drawableSizeFace_Width, drawableSizeFace_Height, height_Width;
    private int type;
    public final static int BUST_0 = 0, BUST_1 = 1, BRUST = 7, HIP_1 = 2, HIP_2 = 8, HIP = 9, WAIST = 3, FACE = 4, TALL = 5, TALL_1 = 10, TALL_2 = 11, MAGIC = 6;
    private int radius;
    private PointF mappedCenterPoint;

    public BeautyFloatingItem(Context context, int type, Drawable drawable) {
        drawableSizeBoobs = SystemUtil.dpToPx(context, 50);
        drawableSizeHip1_Width = SystemUtil.dpToPx(context, 150);
        drawableSizeHip1_Height = SystemUtil.dpToPx(context, 75);
        drawableSizeFace_Height = SystemUtil.dpToPx(context, 50);
        drawableSizeFace_Width = SystemUtil.dpToPx(context, 80);
        this.type = type;
        this.drawable = drawable;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    @NonNull
    public PointF getMappedCenterPoint2() {
        return mappedCenterPoint;
    }

    public BeautyFloatingItem(Context context, int type, Drawable drawable, int size) {
        this.height_Width = size;
        this.type = type;
        this.drawable = drawable;
        realBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void updateRadius() {
        RectF bound = getBound();
        if (type == BUST_0 || type == BUST_1 || type == FACE)
            this.radius = (int) (bound.left + bound.right);
        else if (type == HIP_1)
            this.radius = (int) ((bound.top + bound.bottom));
        mappedCenterPoint = super.getMappedCenterPoint();
    }

    public int getType() {
        return type;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return null;
    }

    @Override
    public BeautyFloatingItem setDrawable(@NonNull Drawable drawable) {
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
    public BeautyFloatingItem setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        drawable.setAlpha(alpha);
        return this;
    }

    @Override
    public int getAlpha() {
        return drawable.getAlpha();
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }

    @Override
    public int getWidth() {
        if (type == BUST_1 || type == BUST_0)
            return drawableSizeBoobs;
        if (type == HIP_1)
            return drawableSizeHip1_Width;
        if (type == FACE)
            return drawableSizeFace_Width;
        if (type == TALL_1 || type == TALL_2)
            return height_Width;
        return 0;
    }

    @Override
    public int getHeight() {
        if (type == BUST_1 || type == BUST_0)
            return drawableSizeBoobs;
        if (type == HIP_1)
            return drawableSizeHip1_Height;
        if (type == FACE)
            return drawableSizeFace_Height;
        if (type == TALL_1 || type == TALL_2)
            return drawable.getIntrinsicHeight();
        return 0;
    }

}
