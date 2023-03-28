package com.example.slide.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.internal.FastBlur;


;

/**
 * Created by PingPingStudio on 11/30/2015.
 */

public class BitmapUtil {
    public final static String TAG = BitmapUtil.class.getSimpleName();

    private static final float BLUR_RADIUS = 25f;


    public static Bitmap loadFullBitmapForVideo(String url, int videoWidth, int videoHeight, Context context) {
        Bitmap front;
        Bitmap bg;
        Timber.d("loadFullBitmapForVideo width: %s", videoWidth);
        Timber.d("loadFullBitmapForVideo height: %s", videoHeight);
        try {
            front = Glide.with(context).asBitmap().fitCenter().load(url).submit(videoWidth, videoHeight).get();
        } catch (Exception e) {
            front = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
        }
        try {
            bg = Glide.with(context).asBitmap().load(url).centerCrop().submit(videoWidth, videoHeight).get();
        } catch (Exception e) {
            bg = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
        }
        bg = FastBlur.blur(bg, 25, true);
        Bitmap out = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();
        matrix.postTranslate((bg.getWidth() - front.getWidth()) / 2f, (bg.getHeight() - front.getHeight()) / 2f);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(out);
        canvas.drawBitmap(bg, 0f, 0f, paint);
        canvas.drawBitmap(front, matrix, paint);
        return out;
    }

    public static Bitmap alPhaBitmap(Bitmap bitmap, int cons) {
        Bitmap transBitmap = Bitmap.createBitmap(bitmap);
        Canvas canvas = new Canvas(transBitmap);
        canvas.drawARGB(0, 0, 0, 0);

        Paint transparentpainthack = new Paint();

        transparentpainthack.setAlpha(cons);

        canvas.drawBitmap(transBitmap, 0, 0, transparentpainthack);
        return transBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
// RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    private static int getClosestResampleSize(int cx, int cy, int maxDim) {
        int max = Math.max(cx, cy);
        int resample;
        for (resample = 1; resample < Integer.MAX_VALUE; resample++) {
            if (resample * maxDim > max) {
                resample--;
                break;
            }
        }

        if (resample > 0) {
            return resample;
        }
        return 1;
    }

    private static BitmapFactory.Options getResampling(int cx, int cy, int max) {
        float scaleVal;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        if (cx > cy) {
            scaleVal = (float) max / (float) cx;
        } else if (cy > cx) {
            scaleVal = (float) max / (float) cy;
        } else {
            scaleVal = (float) max / (float) cx;
        }
        bfo.outWidth = (int) (cx * scaleVal + 0.5f);
        bfo.outHeight = (int) (cy * scaleVal + 0.5f);
        return bfo;
    }

    public static Bitmap resampleImage(String path, int maxDim) {

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);

        BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
        optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth, bfo.outHeight, maxDim);

        Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);

        Matrix m = new Matrix();

        if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
            BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(), bmpt.getHeight(), maxDim);
            m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
                    (float) optsScale.outHeight / (float) bmpt.getHeight());
        }

        int sdk = Build.VERSION.SDK_INT;
        if (sdk > 4) {
            int rotation = getExifRotation(path);
            if (rotation != 0) {
                m.postRotate(rotation);
            }
        }

        return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
    }

    private static int getExifRotation(String imgPath) {
        try {
            ExifInterface exif = new ExifInterface(imgPath);
            String rotationAmount = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (!TextUtils.isEmpty(rotationAmount)) {
                int rotationParam = Integer.parseInt(rotationAmount);
                switch (rotationParam) {
                    case ExifInterface.ORIENTATION_NORMAL:
                        return 0;
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return 90;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return 180;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return 270;
                    default:
                        return 0;
                }
            } else {
                return 0;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String saveBitmapToLocal(String path, Bitmap bm) {
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }

    public static String saveBitmapToLocalPNG(String path, Bitmap bm) {
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }


    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap flipVBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap flipHBitmap(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap blur(Bitmap bitmap, Context context) {
        if (null == bitmap) return null;

        try {
            return Glide.with(context).asBitmap().load(bitmap)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25))).submit().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static float geScaleFitScreenFromBitmap(Bitmap originalBitmap) {
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        if (originalWidth < 9f / 6f * originalHeight) {
            return (originalHeight * 9 / 6f) / originalWidth;
        } else {
            return (originalWidth * 6 / 9f) / originalHeight;
        }
    }

    public static Bitmap bitmapOneOverBackground(Bitmap originalBitmap, Bitmap blurBitmap) {
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        int width;
        int height;
        int cutLeft, cutTop, cutRight, cutBottom;
        if (originalWidth < 9f / 6f * originalHeight) {
            height = originalHeight;
            width = originalHeight * 9 / 6;
            int srcHeight = originalWidth * 6 / 9;
            cutLeft = 0;
            cutTop = (originalHeight - srcHeight) / 2;
            cutRight = originalWidth;
            cutBottom = srcHeight + cutTop;
        } else {
            width = originalWidth;
            height = originalWidth * 6 / 9;
            int srcWidth = originalHeight * 9 / 6;

            cutLeft = (originalWidth - srcWidth) / 2;
            cutTop = 0;
            cutRight = cutLeft + srcWidth;
            cutBottom = originalHeight;
        }

        float marginLeft = (float) (width * 0.5 - originalWidth * 0.5);
        float marginTop = (float) (height * 0.5 - originalHeight * 0.5);

        Bitmap finalBitmap = Bitmap.createBitmap(width, height, originalBitmap.getConfig());
        Canvas canvas = new Canvas(finalBitmap);
        canvas.drawBitmap(blurBitmap, new Rect(cutLeft, cutTop, cutRight, cutBottom), new Rect(0, 0, width, height), null);
        canvas.drawBitmap(originalBitmap, marginLeft, marginTop, null);
        return finalBitmap;
    }

    public static Drawable getDrawableFromAsset(Context context, String fileAsset) {
        Drawable d = null;
        try {
            InputStream ims = context.getAssets().open(fileAsset);
            d = Drawable.createFromStream(ims, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return d;
    }

    public static Bitmap getBitmapFromAsset(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            inputStream = assetManager.open(path);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }

    public static Bitmap createBitmapFromView(View view) {
        Bitmap createBitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(createBitmap);
        view.draw(canvas);
        return createBitmap;
    }
}
