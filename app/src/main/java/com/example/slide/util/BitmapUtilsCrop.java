package com.example.slide.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by DS on 30/01/2018.
 */

public class BitmapUtilsCrop {
    public static Bitmap addPadding(Bitmap bmp, int color) {
        if (bmp == null) {
            return null;
        }
        int biggerParam = Math.max(bmp.getWidth(), bmp.getHeight());
        Bitmap bitmap = Bitmap.createBitmap(biggerParam, biggerParam, bmp.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        canvas.drawBitmap(bmp, (float) (bmp.getWidth() > bmp.getHeight() ? 0 : (bmp.getHeight() - bmp.getWidth()) / 2), (float) (bmp.getHeight() > bmp.getWidth() ? 0 : (bmp.getWidth() - bmp.getHeight()) / 2), null);
        return bitmap;
    }

}
