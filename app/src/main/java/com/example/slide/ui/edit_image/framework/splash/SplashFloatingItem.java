package com.example.slide.ui.edit_image.framework.splash;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.example.slide.ui.edit_image.framework.FloatingItem;

public class SplashFloatingItem extends FloatingItem {

    private Bitmap bitmapXor;
    private Bitmap bitmapOver;
    private Paint xor;
    private Paint over;

    public SplashFloatingItem(Bitmap drawableXor, Bitmap drawableOver) {
        xor = new Paint();
        xor.setDither(true);
        xor.setAntiAlias(true);
        xor.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

        over = new Paint();
        over.setDither(true);
        over.setAntiAlias(true);
        over.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        this.bitmapXor = drawableXor;
        this.bitmapOver = drawableOver;

    }

    private Bitmap getBitmapOver() {
        return this.bitmapOver;
    }

    private Bitmap getBitmapXor() {
        return this.bitmapXor;
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return null;//drawableOver;
    }

    @Override
    public SplashFloatingItem setDrawable(@NonNull Drawable drawable) {
        //this.drawableOver = drawable;
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawBitmap(getBitmapXor(), getMatrix(), xor);
        canvas.drawBitmap(getBitmapOver(), getMatrix(), over);
    }

    @NonNull
    @Override
    public SplashFloatingItem setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        return this;
    }

    @Override
    public int getAlpha() {
        return 1;
    }

    @Override
    public int getWidth() {
        return bitmapXor.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmapOver.getHeight();
    }

    @Override
    public void release() {
        super.release();
        xor = null;
        over = null;
        if (bitmapXor != null)
            bitmapXor.recycle();
        bitmapXor = null;
        if (bitmapOver != null)
            bitmapOver.recycle();
        bitmapOver = null;
    }
}
