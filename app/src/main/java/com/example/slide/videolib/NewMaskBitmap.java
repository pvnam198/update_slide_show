package com.example.slide.videolib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.example.slide.R;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class NewMaskBitmap implements Serializable {

    private static int[][] randRect = new int[21][21];

    private static Random random = new Random();

    public static Matrix matrix = new Matrix();

    private static final Paint paint = new Paint();

    private static final Paint markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    static float translateY = 0;

    static float translateX = 0;

    static float ratio = 0;

    public static int config_length = 8;

    public static int orientation = 0;

    private static Bitmap[][] bitmapArrays;

    private static float dx, dy;

    static {
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    public enum EFFECT {
        NONE("NONE") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd, int videoWidth, int videoHeight, int factor) {
                float fx = factor / ((float) VideoConfig.INSTANCE.getAnimatedFrameSub());
                if (fx < 0.5) {
                    return bitmapStart;
                } else {
                    return bitmapEnd;
                }
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth, int videoHeight) {

            }
        },
        ZOOM_IN_CROSS_FADE("ZOOM_IN_CROSS_FADE") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd, int videoWidth, int videoHeight, int factor) {
                float fx = factor / ((float) VideoConfig.INSTANCE.getAnimatedFrameSub());
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvasStart = new Canvas(createBitmap);
                canvasStart.save();
                if (fx < 0.6) {
                    canvasStart.scale(1 + fx, 1 + fx, VideoConfig.INSTANCE.getVideoWidth() / 2f, VideoConfig.INSTANCE.getVideoHeight() / 2f);
                    canvasStart.drawBitmap(bitmapStart, 0f, 0f, null);
                    canvasStart.restore();
                } else {
                    int alpha = (int) ((fx - 0.6) * 255 / 0.4f);
                    paint.setAlpha(alpha - 1);
                    canvasStart.drawBitmap(bitmapEnd, 0f, 0f, paint);
                    canvasStart.scale(1.6f, 1.6f, VideoConfig.INSTANCE.getVideoWidth() / 2f, VideoConfig.INSTANCE.getVideoHeight() / 2f);
                    paint.setAlpha(255 - alpha);
                    canvasStart.drawBitmap(bitmapStart, 0f, 0f, paint);
                    paint.setAlpha(255);
                    canvasStart.restore();
                }
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth, int videoHeight) {

            }
        },
        OPEN_WINDOW("OPEN_WINDOW") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd, int videoWidth, int videoHeight, int factor) {
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(),
                        VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                try {
                    float percent = (float) factor / VideoConfig.INSTANCE.getAnimatedFrameSub();

                    FutureTarget<Bitmap> futureTarget = Glide.with(context).asBitmap().load("file:///android_asset/toptop2.png").submit();
                    Bitmap bitmapTop = Bitmap.createScaledBitmap(futureTarget.get(), VideoConfig.INSTANCE.getVideoWidth(), (int) dy, false);

                    Canvas canvas = new Canvas(createBitmap);
                    canvas.drawBitmap(bitmapStart, 0f, 0f, null);

                    Bitmap bitmap1 = bitmapArrays[0][0];
                    Bitmap bitmap2 = bitmapArrays[0][1];
                    Bitmap bitmap3 = bitmapArrays[0][2];
                    Bitmap bitmap4 = bitmapArrays[0][3];

                    float ratio1 = -90f + percent * 360;
                    float ratio2 = -180f + percent * 360;
                    float ratio3 = -270f + percent * 360;
                    float ratio4 = -360f + percent * 360;

                    boolean shouldReturn = false;

                    if (ratio1 > 0)
                        ratio1 = 0;
                    else
                        shouldReturn = true;

                    canvas.save();
                    matrix.reset();
                    camera.save();
                    camera.rotateX(ratio1);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(-bitmap1.getWidth() / 2f, 0f);
                    matrix.postTranslate(bitmap1.getWidth() / 2f, 0);
                    canvas.drawBitmap(bitmap1, matrix, paint);
                    if (shouldReturn && factor != 0) {
                        paint.setAlpha((int) (255 *(-ratio1)/90));
                        canvas.drawBitmap(bitmapTop, matrix, paint);
                        paint.setAlpha(255);
                    }
                    canvas.restore();

                    if (shouldReturn)
                        return createBitmap;

                    if (ratio2 > 0)
                        ratio2 = 0;
                    else
                        shouldReturn = true;

                    canvas.save();
                    matrix.reset();
                    camera.save();
                    camera.rotateX(ratio2);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(-bitmap2.getWidth() / 2f, 0f);
                    matrix.postTranslate(bitmap2.getWidth() / 2f, dy);
                    canvas.drawBitmap(bitmap2, matrix, paint);
                    if (shouldReturn && ratio2 < 90) {
                        paint.setAlpha((int) (255 *(-ratio2)/90));
                        canvas.drawBitmap(bitmapTop, matrix, paint);
                        paint.setAlpha(255);
                    }

                    canvas.restore();

                    if (shouldReturn)
                        return createBitmap;

                    if (ratio3 > 0)
                        ratio3 = 0;
                    else
                        shouldReturn = true;

                    canvas.save();
                    matrix.reset();
                    camera.save();
                    camera.rotateX(ratio3);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(-bitmap3.getWidth() / 2f, 0f);
                    matrix.postTranslate(bitmap3.getWidth() / 2f, dy * 2);
                    canvas.drawBitmap(bitmap3, matrix, paint);
                    if (shouldReturn && ratio3 < 90) {
                        paint.setAlpha((int) (255 *(-ratio3)/90));
                        canvas.drawBitmap(bitmapTop, matrix, paint);
                        paint.setAlpha(255);
                    }

                    canvas.restore();

                    if (shouldReturn)
                        return createBitmap;

                    if (ratio4 > 0)
                        ratio4 = 0;

                    canvas.save();
                    matrix.reset();
                    camera.save();
                    camera.rotateX(ratio4);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(-bitmap4.getWidth() / 2f, 0f);
                    matrix.postTranslate(bitmap4.getWidth() / 2f, dy * 3);
                    canvas.drawBitmap(bitmap4, matrix, paint);
                    if (factor < VideoConfig.INSTANCE.getAnimatedFrameSub() - 1) {
                        paint.setAlpha((int) (255 *(-ratio4)/90));
                        canvas.drawBitmap(bitmapTop, matrix, paint);
                        paint.setAlpha(255);
                    }
                    canvas.restore();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                Bitmap bitmap3;
                if (VideoConfig.INSTANCE.getVideoHeight() > 0 || VideoConfig.INSTANCE.getVideoWidth() > 0) {
                    bitmapArrays = new Bitmap[2][4];
                    dx = videoWidth / 4f;
                    dy = videoHeight / 4f;
                    for (int j = 0; j < 4; j++) {
                        int yTop = (int) (dy * j);
                        int yBottom = (int) (yTop + dy);
                        if (yBottom > videoHeight)
                            yBottom = videoHeight;
                        bitmap3 = copyBitmap(bitmap2, 0, yTop, new Rect(0, yTop, videoWidth, yBottom));
                        bitmapArrays[0][j] = bitmap3;
                    }
                }
            }
        },

        OPEN_DOOR("OPEN_DOOR") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        RECT_RANDOM("rect_random") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        PIN_WHEL("Pinwhel") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        FOUR_TRIANGLE("FOUR_TRIANGLE") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        DIAMOND_ZOOM_IN("Diamond_zoom_in") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        DIAMOND_ZOOM_OUT("Diamond_zoom_out") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapEnd, bitmapStart, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        SKEW_LEFT_MERGE("SKEW_LEFT_MERGE") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        SKEW_RIGHT_MERGE("SKEW_RIGHT_MERGE") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        SKEW_LEFT_SPLIT("SKEW_LEFT_SPILT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        SKEW_RIGHT_SPLIT("SKEW_RIGHT_SPLIT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        ECLIPSE_IN("ECLIPSE_IN") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CIRCLE_LEFT_TOP("CIRCLE_LEFT_TOP") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CIRCLE_RIGHT_TOP("CIRCLE_RIGHT_TOP") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CIRCLE_LEFT_BOTTOM("CIRCLE_LEFT_BOTTOM") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CIRCLE_RIGHT_BOTTOM("CIRCLE_RIGHT_BOTTOM") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CIRCLE_IN("CIRCLE_IN") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapEnd, bitmapStart, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CIRCLE_OUT("CIRCLE_OUT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CLOCK("CLOCK") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CROSS_SHUTTER_1("Cross_Shutter_1") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        SQUARE_IN("SQUARE_IN") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        SQUARE_OUT("SQUARE_OUT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        LEAF("LEAF") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        HORIZONTAL_COLUMN_DOWNMASK("HORIZONTAL_COLUMN_DOWNMASK") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        VERTICAL_RECT("VERTICAL_RECT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        HORIZONTAL_RECT("HORIZONTAL_RECT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CROSS_IN("CROSS_IN") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        CROSS_OUT("CROSS_OUT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {

            }
        },

        ROW_SPLIT("ROW_SPLIT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
            }
        },

        COL_SPLIT("Col_Split") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                return createBitmapWithMask(bitmapStart, bitmapEnd, factor, this);
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
            }
        },

        Erase_Slide("Erase_Slide") { //30

            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                Bitmap createBitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
                combineErase(this, bitmapStart, bitmapEnd, new Canvas(createBitmap), videoWidth, videoHeight, factor);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Erase("Erase") {//31

            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                Bitmap createBitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
                combineErase(this, bitmapStart, bitmapEnd, new Canvas(createBitmap), videoWidth, videoHeight, factor);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Pixel_effect("Pixel_effect") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combinePixel(bitmapStart, bitmapEnd, new Canvas(createBitmap), factor);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Flip_Page_Right("Flip_Page_Right") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineFlip(context, bitmapStart, bitmapEnd, new Canvas(createBitmap), factor);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Crossfade("Crossfade") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineCrossFade(bitmapStart, bitmapEnd, new Canvas(createBitmap), factor);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Tilt_Drift("Tilt_Drift") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineDrift(bitmapStart, bitmapEnd, new Canvas(createBitmap), false);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        ROLL_2D_TB("ROLL_2D_TB") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap1(bitmapStart, bitmapEnd, new Canvas(createBitmap), true);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmapStart, Bitmap bitmapEnd, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Roll2D_BT("Roll2D_BT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap1(bitmapEnd, bitmapStart, new Canvas(createBitmap), true);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Roll2D_LR("Roll2D_LR") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap1(bitmapStart, bitmapEnd, new Canvas(createBitmap), true);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Roll2D_RL("Roll2D_RL") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap1(bitmapEnd, bitmapStart, new Canvas(createBitmap), true);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Whole3D_TB("Whole3D_TB") { //40

            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                combineBitmap1(bitmapStart, bitmapEnd, canvas, false);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Whole3D_BT("Whole3D_BT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                combineBitmap1(bitmapEnd, bitmapStart, canvas, false);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Whole3D_LR("Whole3D_LR") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                combineBitmap1(bitmapStart, bitmapEnd, canvas, false);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        Whole3D_RL("Whole3D_RL") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                combineBitmap1(bitmapEnd, bitmapStart, canvas, false);
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
            }
        },

        SepartConbine_TB("SepartConbine_TB") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap2(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 4;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap, bitmap2, videoWidth, videoHeight);
            }
        },

        SepartConbine_BT("SepartConbine_BT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap2(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 4;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap2, bitmap, videoWidth, videoHeight);
            }
        },

        SepartConbine_LR("SepartConbine_LR") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap2(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 4;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap, bitmap2, videoWidth, videoHeight);
            }
        },

        SepartConbine_RL("SepartConbine_RL") { //47

            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap2(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 4;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap2, bitmap, videoWidth, videoHeight);
            }
        },

        RollInTurn_TB("RollInTurn_TB") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap3(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap, bitmap2, videoWidth, videoHeight);
            }
        },

        RollInTurn_BT("RollInTurn_BT") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap3(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap2, bitmap, videoWidth, videoHeight);
            }
        },

        RollInTurn_LR("RollInTurn_LR") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {

                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap3(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap, bitmap2, videoWidth, videoHeight);
            }
        },

        RollInTurn_RL("RollInTurn_RL") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap3(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap2, bitmap, videoWidth, videoHeight);
            }
        },

        Jalousie_BT("Jalousie_BT") {//52

            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, VideoConfig.INSTANCE.getAnimatedFrameSub() - factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap4(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap2, bitmap, videoWidth, videoHeight);
            }
        },

        Jalousie_LR("Jalousie_LR") {
            @Override
            public Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd,
                                        int videoWidth, int videoHeight, int factor) {
                setupVariable(this, factor);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
                combineBitmap4(new Canvas(createBitmap));
                return createBitmap;
            }

            @Override
            public void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth,
                                   int videoHeight) {
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(this, bitmap, bitmap2, videoWidth, videoHeight);
            }
        };

        private static Bitmap createBitmapFromRec1(Bitmap bitmap, int x, int y, Rect rect) {
            return Bitmap.createBitmap(bitmap, x, y, rect.width(), rect.height());
        }

        private static Bitmap createBitmapFromRec(Bitmap bitmap, int x, int y, Rect rect) {
            return Bitmap.createBitmap(bitmap, x, y, rect.width(), rect.height());
        }

        private static Bitmap createBitmapFromRec2(Bitmap bitmap, int x, int y, Rect rect) {
            return Bitmap.createBitmap(bitmap, x, y, rect.width(), rect.height());
        }

        public static Matrix matrix = new Matrix();

        public static Camera camera = new Camera();

        String name;

        EFFECT(String name) {
            this.name = name;
        }

        public abstract Bitmap combineBitmap(Context context, Bitmap bitmapStart, Bitmap bitmapEnd, int videoWidth, int videoHeight, int factor);

        public abstract void initConfig(Bitmap bitmap, Bitmap bitmap2, int videoWidth, int videoHeight);

        public static void setupVariable(EFFECT currentEffect, int i) {

            if (currentEffect == RollInTurn_BT || currentEffect == RollInTurn_LR || currentEffect == RollInTurn_RL || currentEffect == RollInTurn_TB) {
                ratio = ((((config_length - 1) * 30f) + 90f) * i) / VideoConfig.INSTANCE.getAnimatedFrameSub();
            } else if (currentEffect == Jalousie_BT || currentEffect == Jalousie_LR) {
                ratio = ((i) * 180f) / VideoConfig.INSTANCE.getAnimatedFrameSub();
            } else {
                ratio = ((i) * 90f) / VideoConfig.INSTANCE.getAnimatedFrameSub();
            }
            int i2 = 180;
            if (orientation == 1) {
                float f = ratio;
                if (!(currentEffect == Jalousie_BT || currentEffect == Jalousie_LR)) {
                    i2 = 90;
                }
                translateY = (f / i2) * (VideoConfig.INSTANCE.getVideoHeight());
                return;
            }
            float f2 = ratio;
            if (!(currentEffect == Jalousie_BT || currentEffect == Jalousie_LR)) {
                i2 = 90;
            }
            translateX = (f2 / i2) * (VideoConfig.INSTANCE.getVideoWidth());
        }

        public static void initBitmap1(EFFECT currentEffect, Bitmap bitmap, Bitmap bitmap2, int videoWidth, int videoHeight) {
            Bitmap bitmap3;
            if (VideoConfig.INSTANCE.getVideoHeight() > 0 || VideoConfig.INSTANCE.getVideoWidth() > 0) {
                bitmapArrays = (Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{2, config_length});
                dx = (float) videoWidth / config_length;
                dy = (float) videoHeight / config_length;
                int i = 0;
                while (i < 2) {
                    for (int i2 = 0; i2 < config_length; i2++) {
                        if (currentEffect == Jalousie_BT || currentEffect == Jalousie_LR) {
                            if (orientation == 1) {
                                int y = (int) (dy * i2);
                                int bottom = y + (int) dy;
                                if (bottom > videoHeight)
                                    bottom = videoHeight;
                                bitmap3 = copyBitmap(i == 0 ? bitmap : bitmap2, 0, y, new Rect(0, y, videoWidth, bottom));
                            } else {
                                int x = (int) (dx * i2);
                                int right = x + (int) dx;
                                if (right > videoWidth)
                                    right = videoWidth;
                                bitmap3 = copyBitmap(i == 0 ? bitmap : bitmap2, x, 0, new Rect(x, 0, right, videoHeight));
                            }
                        } else if (orientation == 1) {
                            int x = (int) (dx * i2);
                            int right = x + (int) dx;
                            if (right > videoWidth)
                                right = videoWidth;
                            bitmap3 = copyBitmap(i == 0 ? bitmap : bitmap2, x, 0, new Rect(x, 0, right, videoHeight));
                        } else {
                            int y = (int) (dy * i2);
                            int bottom = y + (int) dy;
                            if (bottom > videoHeight)
                                bottom = videoHeight;
                            bitmap3 = copyBitmap(i == 0 ? bitmap : bitmap2, 0, y, new Rect(0, y, videoWidth, bottom));
                        }
                        bitmapArrays[i][i2] = bitmap3;
                    }
                    i++;
                }
            }
        }

        private static Bitmap copyBitmap(Bitmap bitmap, int x, int y, Rect rect) {
            int width = rect.width();
            int height = rect.height();
            if (x + width > VideoConfig.INSTANCE.getVideoWidth())
                width = VideoConfig.INSTANCE.getVideoWidth() - x;
            if (y + height > VideoConfig.INSTANCE.getVideoHeight())
                height = VideoConfig.INSTANCE.getVideoHeight() - y;
            return Bitmap.createBitmap(bitmap, x, y, width, height);
        }

        public static void combineBitmap1(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, boolean z) {
            canvas.save();
            if (orientation == 1) {
                camera.save();
                if (z) {
                    camera.rotateX(0f);
                } else {
                    camera.rotateX(-ratio);
                }
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(-VideoConfig.INSTANCE.getVideoWidth() / 2f, 0f);
                matrix.postTranslate(VideoConfig.INSTANCE.getVideoWidth() / 2f, translateY);
                canvas.drawBitmap(bitmap, matrix, paint);
                camera.save();
                if (z) {
                    camera.rotateX(0f);
                } else {
                    camera.rotateX(90f - ratio);
                }
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(-VideoConfig.INSTANCE.getVideoWidth() / 2f, -VideoConfig.INSTANCE.getVideoHeight());
                matrix.postTranslate(VideoConfig.INSTANCE.getVideoWidth() / 2f, translateY);
                canvas.drawBitmap(bitmap2, matrix, paint);
            } else {
                camera.save();
                if (z) {
                    camera.rotateY(0f);
                } else {
                    camera.rotateY(ratio);
                }
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(0f, ((-VideoConfig.INSTANCE.getVideoHeight()) / 2));
                matrix.postTranslate(translateX, (VideoConfig.INSTANCE.getVideoHeight() / 2));
                canvas.drawBitmap(bitmap, matrix, paint);
                camera.save();
                if (z) {
                    camera.rotateY(0f);
                } else {
                    camera.rotateY(ratio - 90f);
                }
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((-VideoConfig.INSTANCE.getVideoWidth()), ((-VideoConfig.INSTANCE.getVideoHeight()) / 2));
                matrix.postTranslate(translateX, (VideoConfig.INSTANCE.getVideoHeight() / 2));
                canvas.drawBitmap(bitmap2, matrix, paint);
            }
            canvas.restore();
        }

        public static void combineBitmap2(Canvas canvas) {
            for (int i = 0; i < config_length; i++) {
                try {
                    Bitmap bitmap = bitmapArrays[0][i];
                    Bitmap bitmap2 = bitmapArrays[1][i];
                    canvas.save();
                    if (orientation == 1) {
                        camera.save();
                        camera.rotateX(-ratio);
                        camera.getMatrix(matrix);
                        camera.restore();
                        matrix.preTranslate(((-bitmap.getWidth()) / 2), 0f);
                        matrix.postTranslate(((bitmap.getWidth() / 2) + (dx * i)), translateY);
                        canvas.drawBitmap(bitmap, matrix, paint);
                        camera.save();
                        camera.rotateX(90f - ratio);
                        camera.getMatrix(matrix);
                        camera.restore();
                        matrix.preTranslate(((-bitmap2.getWidth()) / 2), (-bitmap2.getHeight()));
                        matrix.postTranslate(((bitmap2.getWidth() / 2) + (dx * i)), translateY);
                        canvas.drawBitmap(bitmap2, matrix, paint);
                    } else {
                        camera.save();
                        camera.rotateY(ratio);
                        camera.getMatrix(matrix);
                        camera.restore();
                        matrix.preTranslate(0f, ((-bitmap.getHeight()) / 2));
                        matrix.postTranslate(translateX, ((bitmap.getHeight() / 2) + (dy * i)));
                        canvas.drawBitmap(bitmap, matrix, paint);
                        camera.save();
                        camera.rotateY(ratio - 90f);
                        camera.getMatrix(matrix);
                        camera.restore();
                        matrix.preTranslate((-bitmap2.getWidth()), ((-bitmap2.getHeight()) / 2));
                        matrix.postTranslate(translateX, ((bitmap2.getHeight() / 2) + (dy * i)));
                        canvas.drawBitmap(bitmap2, matrix, paint);
                    }
                    canvas.restore();
                } catch (Exception unused) {
                }
            }
        }

        public static void combineBitmap3(Canvas canvas) {
            for (int i = 0; i < config_length; i++) {
                Bitmap[][] bitmapArr = bitmapArrays;
                Bitmap bitmap = bitmapArr[0][i];
                Bitmap bitmap2 = bitmapArr[1][i];
                float f = ratio - ((i * 30));
                if (f < 0f) {
                    f = 0f;
                }
                if (f > 90f) {
                    f = 90f;
                }
                canvas.save();
                if (orientation == 1) {
                    float f2 = (f / 90f) * (VideoConfig.INSTANCE.getVideoHeight());
                    if (f2 > (VideoConfig.INSTANCE.getVideoHeight())) {
                        f2 = VideoConfig.INSTANCE.getVideoHeight();
                    }
                    if (f2 < 0f) {
                        f2 = 0f;
                    }
                    camera.save();
                    camera.rotateX(-f);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((-bitmap.getWidth()), 0f);
                    matrix.postTranslate((bitmap.getWidth() + (dx * i)), f2);
                    canvas.drawBitmap(bitmap, matrix, paint);
                    camera.save();
                    camera.rotateX(90f - f);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((-bitmap2.getWidth()), (-bitmap2.getHeight()));
                    matrix.postTranslate((bitmap2.getWidth() + (dx * i)), f2);
                    canvas.drawBitmap(bitmap2, matrix, paint);
                } else {
                    float f3 = (f / 90f) * (VideoConfig.INSTANCE.getVideoWidth());
                    if (f3 > (VideoConfig.INSTANCE.getVideoWidth())) {
                        f3 = VideoConfig.INSTANCE.getVideoWidth();
                    }
                    if (f3 < 0f) {
                        f3 = 0f;
                    }
                    camera.save();
                    camera.rotateY(f);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(0f, ((-bitmap.getHeight()) / 2));
                    matrix.postTranslate(f3, ((bitmap.getHeight() / 2) + (dy * i)));
                    canvas.drawBitmap(bitmap, matrix, paint);
                    camera.save();
                    camera.rotateY(f - 90f);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((-bitmap2.getWidth()), ((-bitmap2.getHeight()) / 2));
                    matrix.postTranslate(f3, ((bitmap2.getHeight() / 2) + (dy * i)));
                    canvas.drawBitmap(bitmap2, matrix, paint);
                }
                canvas.restore();
            }
        }

        public static void combineBitmap4(Canvas canvas) {
            for (int i = 0; i < config_length; i++) {
                Bitmap[][] bitmapArr = bitmapArrays;
                Bitmap bitmap = bitmapArr[0][i];
                Bitmap bitmap2 = bitmapArr[1][i];
                canvas.save();
                if (orientation == 1) {
                    if (ratio < 90f) {
                        camera.save();
                        camera.rotateX(ratio);
                        camera.getMatrix(matrix);
                        camera.restore();
                        matrix.preTranslate(((-bitmap.getWidth()) / 2), ((-bitmap.getHeight()) / 2));
                        matrix.postTranslate((bitmap.getWidth() / 2), ((bitmap.getHeight() / 2) + (dy * i)));
                        canvas.drawBitmap(bitmap, matrix, paint);
                    } else {
                        camera.save();
                        camera.rotateX(180f - ratio);
                        camera.getMatrix(matrix);
                        camera.restore();
                        matrix.preTranslate(((-bitmap2.getWidth()) / 2), ((-bitmap2.getHeight()) / 2));
                        matrix.postTranslate((bitmap2.getWidth() / 2), ((bitmap2.getHeight() / 2) + (dy * i)));
                        canvas.drawBitmap(bitmap2, matrix, paint);
                    }
                } else if (ratio < 90f) {
                    camera.save();
                    camera.rotateY(ratio);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(((-bitmap.getWidth()) / 2), ((-bitmap.getHeight()) / 2));
                    matrix.postTranslate(((bitmap.getWidth() / 2) + (dx * i)), (bitmap.getHeight() / 2));
                    canvas.drawBitmap(bitmap, matrix, paint);
                } else {
                    camera.save();
                    camera.rotateY(180f - ratio);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(((-bitmap2.getWidth()) / 2), ((-bitmap2.getHeight()) / 2));
                    matrix.postTranslate(((bitmap2.getWidth() / 2) + (dx * i)), (bitmap2.getHeight() / 2));
                    canvas.drawBitmap(bitmap2, matrix, paint);
                }
                canvas.restore();
            }
        }

        public static void combineRow(EFFECT effect, Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
            Rect rect;
            canvas.save();
            camera.save();
            camera.getMatrix(matrix);
            camera.restore();
            if (i == 0) {
                canvas.drawBitmap(bitmap, matrix, paint);
            } else if (i == VideoConfig.INSTANCE.getAnimatedFrameSub()) {
                canvas.drawBitmap(bitmap2, matrix, paint);
            } else {
                canvas.drawBitmap(bitmap2, matrix, paint);
                if (effect == COL_SPLIT) {
                    double d2 = (0.0238d * i) + 0.5d;
                    canvas.drawBitmap(createBitmapFromRec(bitmap, (int) ((VideoConfig.INSTANCE.getVideoWidth()) * d2), 0, new Rect((int) ((VideoConfig.INSTANCE.getVideoWidth()) * d2), 0, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight())), ((int) ((VideoConfig.INSTANCE.getVideoWidth()) * d2)), 0f, paint);
                    rect = new Rect(0, 0, (int) ((VideoConfig.INSTANCE.getVideoWidth()) * ((i * -0.0238d) + 0.5d)), VideoConfig.INSTANCE.getVideoHeight());
                } else if (effect == ROW_SPLIT) {
                    double d4 = (0.0238d * i) + 0.5d;
                    canvas.drawBitmap(createBitmapFromRec(bitmap, 0, (int) ((VideoConfig.INSTANCE.getVideoHeight()) * d4), new Rect(0, (int) ((VideoConfig.INSTANCE.getVideoHeight()) * d4), VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight())), 0f, ((int) ((VideoConfig.INSTANCE.getVideoHeight()) * d4)), paint);
                    rect = new Rect(0, 0, VideoConfig.INSTANCE.getVideoWidth(), (int) ((VideoConfig.INSTANCE.getVideoHeight()) * ((i * -0.0238d) + 0.5d)));
                } else {
                    return;
                }
                canvas.drawBitmap(createBitmapFromRec(bitmap, 0, 0, rect), 0f, 0f, paint);
            }
        }

        public static void combineErase(EFFECT effect, Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int videoWidth, int videoHeight, int factor) {
            canvas.save();
            camera.save();
            camera.getMatrix(matrix);
            camera.restore();
            Paint newPaint = new Paint();
            newPaint.setColor(Color.BLACK);
            newPaint.setAntiAlias(true);
            newPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            if (factor == 0) {
                canvas.drawBitmap(bitmap, matrix, paint);
            } else if (factor == VideoConfig.INSTANCE.getAnimatedFrameSub()) {
                canvas.drawBitmap(bitmap2, matrix, paint);
            } else {
                float fRatioX = ((float) videoWidth) / VideoConfig.INSTANCE.getAnimatedFrameSub() * factor;
                int ratioX = (int) fRatioX;
                if (ratioX >= videoWidth) {
                    canvas.drawBitmap(bitmap2, matrix, paint);
                } else {
                    float blurDistance = videoWidth / 8f;
                    canvas.drawBitmap(bitmap2, matrix, paint);
                    canvas.drawBitmap(createBitmapFromRec1(bitmap, ratioX, 0, new Rect(ratioX, 0, videoWidth, videoHeight)), ratioX, 0f, newPaint);
                    if (effect == Erase) {
                        if (ratioX - blurDistance < 0) {
                            blurDistance = ratioX;
                            Bitmap a2 = createBitmapFromRec(bitmap, (int) (ratioX - blurDistance), 0, new Rect((int) (ratioX - blurDistance), 0, (int) ratioX, videoHeight));
                            LinearGradient linearGradient2 = new LinearGradient(0f, 0f, blurDistance + 2, 0f, Color.TRANSPARENT, Color.WHITE, Shader.TileMode.CLAMP);
                            newPaint.setShader(new ComposeShader(linearGradient2, new BitmapShader(a2, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_IN));
                            Bitmap createBitmap = Bitmap.createBitmap((int) blurDistance, videoHeight, Bitmap.Config.ARGB_8888);
                            new Canvas(createBitmap).drawRect(0f, 0f, a2.getWidth(), a2.getHeight(), newPaint);
                            newPaint.setShader(null);
                            canvas.drawBitmap(createBitmap, ratioX - blurDistance, 0f, newPaint);
                        } else {
                            Bitmap a2 = createBitmapFromRec2(bitmap, (int) (ratioX - blurDistance), 0, new Rect((int) (ratioX - blurDistance), 0, (int) ratioX, videoHeight));
                            LinearGradient linearGradient2 = new LinearGradient(0f, 0f, blurDistance, 0f, Color.TRANSPARENT, Color.WHITE, Shader.TileMode.CLAMP);
                            newPaint.setShader(new ComposeShader(linearGradient2, new BitmapShader(a2, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_IN));
                            Bitmap createBitmap = Bitmap.createBitmap((int) blurDistance, videoHeight, Bitmap.Config.ARGB_8888);
                            new Canvas(createBitmap).drawRect(0f, 0f, a2.getWidth(), a2.getHeight(), newPaint);
                            newPaint.setShader(null);
                            canvas.drawBitmap(createBitmap, ratioX - blurDistance, 0f, newPaint);
                        }
                    }
                }
            }
            canvas.restore();
        }

        public static void combinePixel(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
            canvas.save();
            camera.save();
            camera.getMatrix(matrix);
            camera.restore();
            if (i != 0) {
                if (i == VideoConfig.INSTANCE.getAnimatedFrameSub()) {
                    canvas.drawBitmap(bitmap2, matrix, paint);
                    canvas.restore();
                    return;
                }
                int mid = VideoConfig.INSTANCE.getAnimatedFrame() / 2;
                if (i > mid) {
                    int i2 = (VideoConfig.INSTANCE.getAnimatedFrame() - i);
                    float factor = ((float) i2 / mid) * 25f + 1f;
                    Bitmap bitmapEnd = Bitmap.createScaledBitmap(bitmap2, (int) (VideoConfig.INSTANCE.getVideoWidth() / factor), (int) (VideoConfig.INSTANCE.getVideoHeight() / factor), false);
                    bitmap = Bitmap.createScaledBitmap(bitmapEnd, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), false);
                    canvas.drawBitmap(bitmap, matrix, paint);
                    if (factor > 20) {
                        float alpha = (factor - 20) * 255 / 6;
                        paint.setAlpha((int) alpha);
                        Bitmap bitmapStart = Bitmap.createScaledBitmap(bitmap, (int) (VideoConfig.INSTANCE.getVideoWidth() / factor), (int) (VideoConfig.INSTANCE.getVideoHeight() / factor), false);
                        bitmap2 = Bitmap.createScaledBitmap(bitmapStart, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), false);
                        canvas.drawBitmap(bitmap2, matrix, paint);
                        paint.setAlpha(255);
                    }
                } else {
                    float factor = ((float) i / mid) * 25f + 1f;
                    Bitmap bitmapStart = Bitmap.createScaledBitmap(bitmap, (int) (VideoConfig.INSTANCE.getVideoWidth() / factor), (int) (VideoConfig.INSTANCE.getVideoHeight() / factor), false);
                    bitmap = Bitmap.createScaledBitmap(bitmapStart, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), false);
                    canvas.drawBitmap(bitmap, matrix, paint);
                    if (factor > 20) {
                        float alpha = (factor - 20) * 255 / 6;
                        paint.setAlpha((int) alpha);
                        Bitmap bitmapEnd = Bitmap.createScaledBitmap(bitmap2, (int) (VideoConfig.INSTANCE.getVideoWidth() / factor), (int) (VideoConfig.INSTANCE.getVideoHeight() / factor), false);
                        bitmap2 = Bitmap.createScaledBitmap(bitmapEnd, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), false);
                        canvas.drawBitmap(bitmap2, matrix, paint);
                        paint.setAlpha(255);
                    }
                }
            } else {
                canvas.drawBitmap(bitmap, matrix, paint);
            }
            canvas.restore();
        }

        public static void combineFlip(Context context, Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
            try {
                canvas.save();
                camera.save();
                camera.getMatrix(matrix);
                camera.restore();
                paint.setAlpha(255);

                FutureTarget<Bitmap> futureTarget = Glide.with(context).asBitmap().load("file:///android_asset/left.png").submit();
                Bitmap bitmapLeft = futureTarget.get();
                futureTarget = Glide.with(context).asBitmap().load("file:///android_asset/right.png").submit();
                Bitmap bitmapRight = futureTarget.get();
                paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.filter_0), PorterDuff.Mode.LIGHTEN));
                if (i == 0) {
                    canvas.drawBitmap(bitmap, matrix, paint);
                } else if (i == VideoConfig.INSTANCE.getAnimatedFrameSub()) {
                    canvas.drawBitmap(bitmap2, matrix, paint);
                } else if (i == 1) {
                    canvas.drawBitmap(bitmap, matrix, paint);
                    canvas.drawBitmap(Bitmap.createScaledBitmap(bitmapLeft, 30, VideoConfig.INSTANCE.getVideoHeight(), false), 0f, 0f, paint);
                } else {
                    float ratioX = ((float) VideoConfig.INSTANCE.getVideoWidth()) / VideoConfig.INSTANCE.getAnimatedFrameSub() * (i - 1);
                    int otherX = (int) (VideoConfig.INSTANCE.getVideoWidth() - ratioX);

                    Matrix matrix = new Matrix();
                    matrix.postScale(-1f, 1f, VideoConfig.INSTANCE.getVideoWidth() / 2f, 0f);
                    Rect rect = new Rect(0, 0, (int) ratioX, VideoConfig.INSTANCE.getVideoHeight());
                    Rect rect2 = new Rect((int) ratioX, 0, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight());
                    Bitmap a = createBitmapFromRec(Bitmap.createBitmap(bitmap, 0, 0, VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), matrix, false),
                            otherX, 0, rect);
                    Bitmap a2 = createBitmapFromRec(bitmap, (int) ratioX, 0, rect2);
                    canvas.drawBitmap(bitmap2, NewMaskBitmap.matrix, paint);
                    canvas.drawBitmap(a2, ((int) ratioX), 0f, paint);
                    canvas.drawBitmap(Bitmap.createScaledBitmap(bitmapRight, 30, VideoConfig.INSTANCE.getVideoHeight(), false),
                            (((int) ratioX) - 30), 0f, paint);
                    canvas.drawBitmap(Bitmap.createScaledBitmap(bitmapLeft, 30, VideoConfig.INSTANCE.getVideoHeight(), false),
                            (((int) ratioX) * 2), 0f, paint);
                    paint.setAlpha(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                    paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.filter_8), PorterDuff.Mode.LIGHTEN));
                    canvas.drawBitmap(a, (int) ratioX, 0f, paint);
                    paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.filter_0), PorterDuff.Mode.LIGHTEN));
                }
                canvas.restore();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static void combineCrossFade(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, int i) {
            canvas.save();
            camera.save();
            camera.getMatrix(matrix);
            camera.restore();
            if (i == VideoConfig.INSTANCE.getAnimatedFrameSub()) {
                paint.setAlpha(255);
                canvas.drawBitmap(bitmap2, matrix, paint);
            } else {
                int alpha = i * 255 / VideoConfig.INSTANCE.getAnimatedFrameSub();
                paint.setAlpha(255 - alpha);
                canvas.drawBitmap(bitmap, matrix, paint);
                paint.setAlpha(alpha);
                canvas.drawBitmap(bitmap2, matrix, paint);
                paint.setAlpha(255);
            }
            canvas.restore();
        }

        public static void combineDrift(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, boolean z) {
            canvas.save();
            camera.save();
            if (z) {
                camera.rotateX(0f);
            } else {
                camera.rotateX(-ratio);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(((-VideoConfig.INSTANCE.getVideoWidth()) / 2), 0f);
            matrix.postTranslate(VideoConfig.INSTANCE.getVideoWidth() / 2f, translateY);
            if (translateY < (VideoConfig.INSTANCE.getVideoHeight())) {
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) ((VideoConfig.INSTANCE.getVideoWidth()) * (((VideoConfig.INSTANCE.getVideoHeight()) - translateY) / (VideoConfig.INSTANCE.getVideoHeight()))), (int) ((VideoConfig.INSTANCE.getVideoHeight()) - translateY), false);
            }
            canvas.drawBitmap(bitmap, matrix, paint);
            camera.save();
            if (z) {
                camera.rotateX(0f);
            } else {
                camera.rotateX(90f - ratio);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(((-VideoConfig.INSTANCE.getVideoWidth()) / 2), (-VideoConfig.INSTANCE.getVideoHeight()));
            matrix.postTranslate(VideoConfig.INSTANCE.getVideoWidth() / 2f, translateY);
            if (translateY > 0f) {
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap2, (int) ((VideoConfig.INSTANCE.getVideoWidth()) * (translateY / (VideoConfig.INSTANCE.getVideoHeight()))), (int) translateY, false), matrix, paint);
                canvas.restore();
                return;
            }
            canvas.drawBitmap(bitmap2, matrix, paint);
            canvas.restore();
        }

    }

    public static void reInitRect() {
        randRect = new int[21][21];
        for (int i = 0; i < 21; i++) {
            for (int j = 0; j < 21; j++) {
                randRect[i][j] = 55;
            }
        }

        for (int index = 0; index < 21; index++) {
            for (int i = 0; i < 21; i++) {
                int rand = random.nextInt(21);
                while (randRect[i][rand] <= index) {
                    rand = random.nextInt(21);
                }
                randRect[i][rand] = index;
            }
        }
    }

    private static Bitmap createBitmapWithMask(Bitmap bitmapStart, Bitmap bitmapEnd, int factor, EFFECT effect) {
        int w = VideoConfig.INSTANCE.getVideoWidth();
        int h = VideoConfig.INSTANCE.getVideoHeight();
        Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasStart = new Canvas(createBitmap);
        canvasStart.drawBitmap(bitmapStart, 0f, 0f, null);

        Bitmap mask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mask);
        Path path = new Path();
        float fx, fy, ratioX, ratioY, hf, wf, r, f;

        switch (effect) {
            case OPEN_DOOR:
                Log.d("kimkakasevice", "VideoConfig.INSTANCE.getAnimatedFrameSub()" + VideoConfig.INSTANCE.getAnimatedFrameSub());
                fx = ((w / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * factor);
                path.moveTo((w / 2f), 0f);
                path.lineTo(w / 2f - fx, 0f);
                path.lineTo(w / 2f - fx / 2f, h / 6f);
                path.lineTo(w / 2f - fx / 2f, (h - h / 6f));
                path.lineTo(w / 2f - fx, h);
                path.lineTo(w / 2f + fx, h);
                path.lineTo(w / 2f + (fx / 2f), (h - (h / 6f)));
                path.lineTo(w / 2f + (fx / 2f), (h / 6f));
                path.lineTo(w / 2f + fx, 0f);
                path.lineTo(w / 2f - fx, 0f);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case RECT_RANDOM:
                wf = w / 21f;
                hf = h / 21f;
                int max = factor * 21 / VideoConfig.INSTANCE.getAnimatedFrameSub();
                for (int i = 0; i < randRect.length; i++) {
                    for (int j = 0; j < randRect[i].length; j++) {
                        if (randRect[i][j] < max) {
                            canvas.drawRect(i * wf - 1f, j * hf - 1f, (i + 1) * wf + 1f, (j + 1) * hf + 1f, paint);
                        }
                    }
                }
                break;
            case PIN_WHEL:
                float rationX = (w / (float) (VideoConfig.INSTANCE.getAnimatedFrameSub())) * (factor);
                float rationY = (h / (float) (VideoConfig.INSTANCE.getAnimatedFrameSub())) * (factor);
                path.moveTo(w / 2f, h / 2f);
                path.lineTo(0f, h);
                path.lineTo(rationX, h);
                path.close();
                path.moveTo(w / 2f, h / 2f);
                path.lineTo(w, h);
                path.lineTo(w, h - rationY);
                path.close();
                path.moveTo(w / 2f, h / 2f);
                path.lineTo(w, 0f);
                path.lineTo(w - rationX, 0f);
                path.close();
                path.moveTo(w / 2f, h / 2f);
                path.lineTo(0f, 0f);
                path.lineTo(0f, rationY);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case FOUR_TRIANGLE:
                ratioX = (w / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                ratioY = (h / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                path.moveTo(0f, ratioY);
                path.lineTo(0f, 0f);
                path.lineTo(ratioX, 0f);
                path.lineTo(w, h - ratioY);
                path.lineTo(w, h);
                path.lineTo(w - ratioX, h);
                path.lineTo(0f, ratioY);
                path.close();
                path.moveTo(w - ratioX, 0f);
                path.lineTo(w, 0f);
                path.lineTo(w, ratioY);
                path.lineTo(ratioX, h);
                path.lineTo(0f, h);
                path.lineTo(0f, h - ratioY);
                path.lineTo(w - ratioX, 0f);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case DIAMOND_ZOOM_IN:
                fx = (w / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * factor;
                fy = (h / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * factor;
                path.moveTo(w / 2f, (h / 2f) - fy);
                path.lineTo((w / 2f) + fx, h / 2f);
                path.lineTo(w / 2f, (h / 2f) + fy);
                path.lineTo((w / 2f) - fx, h / 2f);
                path.lineTo(w / 2f, (h / 2f) - fy);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case DIAMOND_ZOOM_OUT:
                fx = w - (w / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * factor;
                fy = h - (h / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * factor;
                path.moveTo(w / 2f, (h / 2f) - fy);
                path.lineTo((w / 2f) + fx, h / 2f);
                path.lineTo(w / 2f, (h / 2f) + fy);
                path.lineTo((w / 2f) - fx, h / 2f);
                path.lineTo(w / 2f, (h / 2f) - fy);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case SKEW_LEFT_MERGE:
                ratioX = (w / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                ratioY = (h / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                path.moveTo(0f, ratioY);
                path.lineTo(ratioX, 0f);
                path.lineTo(0f, 0f);
                path.close();
                path.moveTo(w - ratioX, h);
                path.lineTo(w, h - ratioY);
                path.lineTo(w, h);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case SKEW_RIGHT_MERGE:
                ratioX = (w / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                ratioY = (h / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                path.moveTo(0f, h - ratioY);
                path.lineTo(ratioX, h);
                path.lineTo(0f, h);
                path.close();
                path.moveTo(w - ratioX, 0f);
                path.lineTo(w, ratioY);
                path.lineTo(w, 0f);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case SKEW_LEFT_SPLIT:
                ratioX = (w / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                ratioY = (h / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                path.moveTo(0f, ratioY);
                path.lineTo(0f, 0f);
                path.lineTo(ratioX, 0f);
                path.lineTo(w, h - ratioY);
                path.lineTo(w, h);
                path.lineTo(w - ratioX, h);
                path.lineTo(0f, ratioY);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case SKEW_RIGHT_SPLIT:
                ratioX = (w / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                ratioY = (h / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                path.moveTo(w - ratioX, 0f);
                path.lineTo(w, 0f);
                path.lineTo(w, ratioY);
                path.lineTo(ratioX, h);
                path.lineTo(0f, h);
                path.lineTo(0f, h - ratioY);
                path.lineTo(w - ratioX, 0f);
                path.close();
                canvas.drawPath(path, paint);
                break;
            case ECLIPSE_IN:
                hf = (h / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                wf = (w / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                RectF rectFL = new RectF(-wf, 0f, wf, h);
                RectF rectFT = new RectF(0f, -hf, w, hf);
                RectF rectFR = new RectF(w - wf, 0f, w + wf, h);
                RectF rectFB = new RectF(0f, h - hf, w, h + hf);
                canvas.drawOval(rectFL, paint);
                canvas.drawOval(rectFT, paint);
                canvas.drawOval(rectFR, paint);
                canvas.drawOval(rectFB, paint);
                break;
            case CIRCLE_LEFT_TOP:
                canvas.drawCircle(0f, 0f, (float) (((Math.sqrt((w * w + h * h))) / (VideoConfig.INSTANCE.getAnimatedFrameSub())) * factor), paint);
                break;
            case CIRCLE_RIGHT_TOP:
                canvas.drawCircle(w, 0f, (float) (((Math.sqrt(((w * w) + (h * h)))) / VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor)), paint);
                break;
            case CIRCLE_LEFT_BOTTOM:
                canvas.drawCircle(0f, h, (float) (((Math.sqrt(((w * w) + (h * h)))) / VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor)), paint);
                break;
            case CIRCLE_RIGHT_BOTTOM:
                canvas.drawCircle(w, h, (float) (((Math.sqrt(((w * w) + (h * h)))) / VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor)), paint);
                break;
            case CIRCLE_IN:
                r = getRad(w * 2, h * 2);
                f = (r / VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                canvas.drawCircle(w / 2f, h / 2f, r - f, paint);
                break;
            case CIRCLE_OUT:
                canvas.drawCircle(w / 2f, h / 2f, (getRad(w * 2, h * 2) / VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor), paint);
                break;
            case VERTICAL_RECT:
                hf = h / 10f;
                float rectH = ((factor) * hf) / VideoConfig.INSTANCE.getAnimatedFrameSub();
                for (int i = 0; i < 10; i++) {
                    canvas.drawRect(new Rect(0, (int) ((i) * hf), w, (int) (((i) * hf) + rectH)), paint);
                }
                break;
            case CLOCK:
                canvas.drawArc(-w, -h, w * 2, h * 2, 270, 360 * factor / (float) VideoConfig.INSTANCE.getAnimatedFrameSub(), true, paint);
                break;
            case CROSS_SHUTTER_1:
                wf = w / 12f;
                hf = h / 12f;
                ratioX = wf / VideoConfig.INSTANCE.getAnimatedFrameSub() * factor;
                ratioY = hf / VideoConfig.INSTANCE.getAnimatedFrameSub() * factor;
                for (int i = 0; i < 12; i++) {
                    canvas.drawRect(i * wf, 0, i * wf + ratioX, h, paint);
                    canvas.drawRect(0, i * hf, w, i * hf + ratioY, paint);
                }
                break;
            case SQUARE_IN:
                ratioX = (w / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                ratioY = (h / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                path.moveTo(0f, 0f);
                path.lineTo(0f, h);
                path.lineTo(ratioX, h);
                path.lineTo(ratioX, 0f);
                path.moveTo(w, h);
                path.lineTo(w, 0f);
                path.lineTo(w - ratioX, 0f);
                path.lineTo(w - ratioX, h);
                path.moveTo(ratioX, ratioY);
                path.lineTo(ratioX, 0f);
                path.lineTo(w - ratioX, 0f);
                path.lineTo(w - ratioX, ratioY);
                path.moveTo(ratioX, h - ratioY);
                path.lineTo(ratioX, h);
                path.lineTo(w - ratioX, h);
                path.lineTo(w - ratioX, h - ratioY);
                canvas.drawPath(path, paint);
                break;

            case SQUARE_OUT:
                ratioX = (w / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                ratioY = (h / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                canvas.drawRect(new RectF((w / 2f) - ratioX, (h / 2f) - ratioY, (w / 2f) + ratioX, (h / 2f) + ratioY), paint);
                break;

            case ROW_SPLIT:
                ratioY = (h / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                canvas.drawRect(new RectF(0, (h / 2f) - ratioY, w, (h / 2f) + ratioY), paint);
                break;

            case COL_SPLIT:
                ratioX = (w / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                canvas.drawRect(new RectF((w / 2f) - ratioX, 0, (w / 2f) + ratioX, h), paint);
                break;

            case LEAF:
                fx = ((w / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * factor * 1.15f);
                fy = ((h / (float) VideoConfig.INSTANCE.getAnimatedFrameSub()) * factor * 1.15f);
                path.moveTo(0f, h);
                path.cubicTo(0f, h, (w / 2f) - fx, (h / 2f) - fy, w, 0f);
                path.cubicTo(w, 0f, (w / 2f) + fx, (h / 2f) + fy, 0f, h);
                path.close();
                canvas.drawPath(path, paint);
                break;

            case HORIZONTAL_COLUMN_DOWNMASK:
                float factorX = VideoConfig.INSTANCE.getAnimatedFrameSub() / 2f;
                canvas.drawRoundRect(new RectF(0f, 0f, (w / (VideoConfig.INSTANCE.getAnimatedFrameSub() / 2f)) * (factor), h / 2f), 0f, 0f, paint);
                if ((factor) >= 0.5f + factorX) {
                    canvas.drawRoundRect(new RectF(w - ((w / (((VideoConfig.INSTANCE.getAnimatedFrameSub() - 1)) / 2f)) * (((int) ((factor) - factorX)))), h / 2f, w, h), 0f, 0f, paint);
                }
                break;

            case HORIZONTAL_RECT:
                wf = w / 10f;
                float rectW = (wf / VideoConfig.INSTANCE.getAnimatedFrameSub()) * (factor);
                for (int i = 0; i < 10; i++) {
                    canvas.drawRect(new Rect((int) ((i) * wf), 0, (int) (((i) * wf) + rectW), h), paint);
                }
                break;

            case CROSS_IN:
                fx = (w / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                fy = (h / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                path.moveTo(0f, 0f);
                path.lineTo(fx, 0f);
                path.lineTo(fx, fy);
                path.lineTo(0f, fy);
                path.lineTo(0f, 0f);
                path.close();
                path.moveTo(w, 0f);
                path.lineTo(w - fx, 0f);
                path.lineTo(w - fx, fy);
                path.lineTo(w, fy);
                path.lineTo(w, 0f);
                path.close();
                path.moveTo(w, h);
                path.lineTo(w - fx, h);
                path.lineTo(w - fx, h - fy);
                path.lineTo(w, h - fy);
                path.lineTo(w, h);
                path.close();
                path.moveTo(0f, h);
                path.lineTo(fx, h);
                path.lineTo(fx, h - fy);
                path.lineTo(0f, h - fy);
                path.lineTo(0f, 0f);
                path.close();
                canvas.drawPath(path, paint);
                break;

            case CROSS_OUT:
                fx = (w / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                fy = (h / (VideoConfig.INSTANCE.getAnimatedFrameSub() * 2f)) * (factor);
                path.moveTo((w / 2f) + fx, 0f);
                path.lineTo((w / 2f) + fx, (h / 2f) - fy);
                path.lineTo(w, (h / 2f) - fy);
                path.lineTo(w, (h / 2f) + fy);
                path.lineTo((w / 2f) + fx, (h / 2f) + fy);
                path.lineTo((w / 2f) + fx, h);
                path.lineTo((w / 2f) - fx, h);
                path.lineTo((w / 2f) - fx, (h / 2f) - fy);
                path.lineTo(0f, (h / 2f) - fy);
                path.lineTo(0f, (h / 2f) + fy);
                path.lineTo((w / 2f) - fx, (h / 2f) + fy);
                path.lineTo((w / 2f) - fx, 0f);
                path.close();
                canvas.drawPath(path, paint);
                break;

        }

        canvasStart.drawBitmap(mask, 0f, 0f, markPaint);

        Bitmap combineBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas3 = new Canvas(combineBitmap);
        canvas3.drawBitmap(bitmapEnd, 0f, 0f, null);
        canvas3.drawBitmap(createBitmap, 0f, 0f, paint);
        return combineBitmap;
    }

    static float getRad(int w, int h) {
        return (float) Math.sqrt((((w * w) + (h * h)) / 4));
    }

}
