package com.example.slide.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import androidx.exifinterface.media.ExifInterface;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ScalingUtilities {

//    public static Bitmap loadFullBitmapForVideo(String url, int mDisplayWidth, int mDisplayHeight, float x, float y, Context context) {
//        Bitmap cs = bgBitmap.copy(bgBitmap.getConfig(), true);
//        try {
//            Bitmap blurBitmap = Glide.with(context).asBitmap().load(cs).centerCrop().apply(RequestOptions.bitmapTransform(new BlurTransformation(25)).override(mDisplayWidth, mDisplayHeight)).submit().get();
//            new Canvas(blurBitmap)
//                    .drawBitmap(newscaleBitmap(originalImage, mDisplayWidth, mDisplayHeight, x, y), 0.0f, 0.0f, new Paint());
//            return blurBitmap;
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        return originalImage;
//    }

    public static Bitmap ConvertSameSize(Bitmap originalImage, Bitmap bgBitmap, int mDisplayWidth, int mDisplayHeight, float x, float y, Context context) {
        Bitmap cs = bgBitmap.copy(bgBitmap.getConfig(), true);
        try {
            Bitmap blurBitmap = Glide.with(context).asBitmap().load(cs).apply(new RequestOptions().centerCrop().bitmapTransform(new BlurTransformation(25)).override(mDisplayWidth, mDisplayHeight)).submit().get();
            new Canvas(blurBitmap)
                    .drawBitmap(newscaleBitmap(originalImage, mDisplayWidth, mDisplayHeight, x, y), 0.0f, 0.0f, new Paint());
            return blurBitmap;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return originalImage;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        if (sourceWidth == newWidth && sourceHeight == newHeight) {
            return source;
        }
        float scale = Math.max(((float) newWidth) / ((float) sourceWidth), ((float) newHeight) / ((float) sourceHeight));
        float scaledWidth = scale * ((float) sourceWidth);
        float scaledHeight = scale * ((float) sourceHeight);
        float left = (((float) newWidth) - scaledWidth) / 2.0f;
        float top = (((float) newHeight) - scaledHeight) / 2.0f;
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        new Canvas(dest).drawBitmap(source, null, targetRect, null);
        return dest;
    }

    private static Bitmap newscaleBitmap(Bitmap originalImage, int width, int height, float x, float y) {
        Bitmap background = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        float originalWidth = (float) originalImage.getWidth();
        float originalHeight = (float) originalImage.getHeight();
        Canvas canvas = new Canvas(background);
        float scale = ((float) width) / originalWidth;
        float scaleY = ((float) height) / originalHeight;
        float xTranslation = 0.0f;
        float yTranslation = (((float) height) - (originalHeight * scale)) / 2.0f;
        if (yTranslation < 0.0f) {
            yTranslation = 0.0f;
            scale = ((float) height) / originalHeight;
            xTranslation = (((float) width) - (originalWidth * scaleY)) / 2.0f;
        }
        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation * x, yTranslation + y);
        Log.d("translation", "xTranslation :" + xTranslation + " yTranslation :" + yTranslation);
        transformation.preScale(scale, scale);
        canvas.drawBitmap(originalImage, transformation, new Paint());
        return background;
    }

    public static Bitmap checkBitmap(String path) {
        int orientation = 1;
        Options bounds = new Options();
//        bounds.outWidth = VideoConfig.VIDEO_WIDTH;
//        bounds.outHeight = VideoConfig.VIDEO_HEIGHT;
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        Bitmap bm = BitmapFactory.decodeFile(path, new Options());
//        return bm;
        try {
            String orientString = new ExifInterface(path).getAttribute("Orientation");
            if (orientString != null) {
                orientation = Integer.parseInt(orientString);
            }
            int rotationAngle = 0;
            if (orientation == 6) {
                rotationAngle = 90;
            }
            if (orientation == 3) {
                rotationAngle = 180;
            }
            if (orientation == 8) {
                rotationAngle = 270;
            }
            Matrix matrix = new Matrix();
            matrix.setRotate((float) rotationAngle, ((float) bm.getWidth()) / 2.0f, ((float) bm.getHeight()) / 2.0f);
            return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
