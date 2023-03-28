package com.example.slide.ui.video.video_preview;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

import com.example.slide.videolib.VideoConfig;

import java.lang.reflect.Array;

/* renamed from: com.slideshow.with.music.d.a */
/* compiled from: Slide_FinalMaskBitmap3D */
public final class MaskProvider {

    /* renamed from: a */
    static final Paint paint = new Paint();

    /* renamed from: b */
    public static float ANIMATED_FRAME = 22.0f;

    /* renamed from: c */
    public static int ANIMATED_FRAME_CAL = 21;

    /* renamed from: d */
    public static Camera camera = new Camera();

    /* renamed from: e */
    public static int orientation = 0;

    /* renamed from: f */
    public static Matrix matrix = new Matrix();

    /* renamed from: g */
    public static int config_length = 8;

    /* renamed from: h */
    public static Effect currentEffect;

    /* renamed from: i */
    private static int dy;

    /* renamed from: j */
    private static int dx;

    /* renamed from: k */
    private static float translateX;

    /* renamed from: l */
    private static float translateY;

    /* renamed from: m */
    private static Bitmap[][] bitmapArrays;

    /* renamed from: n */
    private static float ratio;

    /* renamed from: com.slideshow.with.music.d.a$a */
    /* compiled from: Slide_FinalMaskBitmap3D */
    public enum Effect {
        None("r2") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap1(bitmap2, bitmap, new Canvas(createBitmap), true);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                orientation = 0;
                currentEffect = this;
                camera = new Camera();
                matrix = new Matrix();
            }
        },
        Roll2D_TB("r3") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap1(bitmap, bitmap2, new Canvas(createBitmap), true);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                orientation = 1;
                currentEffect = this;
                camera = new Camera();
                matrix = new Matrix();
            }
        },
        Roll2D_BT("r4") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap1(bitmap2, bitmap, new Canvas(createBitmap), true);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                orientation = 1;
                currentEffect = this;
                camera = new Camera();
                matrix = new Matrix();
            }
        },
        Roll2D_LR("r5") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap1(bitmap, bitmap2, new Canvas(createBitmap), true);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                orientation = 0;
                currentEffect = this;
                camera = new Camera();
                matrix = new Matrix();
            }
        },
        Roll2D_RL("r6") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap1(bitmap2, bitmap, new Canvas(createBitmap), true);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                orientation = 0;
                currentEffect = this;
                camera = new Camera();
                matrix = new Matrix();
            }
        },
        Whole3D_TB("r7") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                currentEffect = this;
                combineBitmap1(bitmap, bitmap2, canvas, false);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                currentEffect = this;
            }
        },
        Whole3D_BT("r8") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                currentEffect = this;
                combineBitmap1(bitmap2, bitmap, canvas, false);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                orientation = 1;
                config_length = 8;
                currentEffect = this;
                camera = new Camera();
                matrix = new Matrix();
            }
        },
        Whole3D_LR("r8") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                currentEffect = this;
                combineBitmap1(bitmap, bitmap2, canvas, false);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                orientation = 0;
                currentEffect = this;
                camera = new Camera();
                matrix = new Matrix();
            }
        },
        Whole3D_RL("Whole3D_RL") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                currentEffect = this;
                combineBitmap1(bitmap2, bitmap, canvas, false);
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                currentEffect = this;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
            }
        },
        SepartConbine_TB("SepartConbine_TB") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap2(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 4;
                currentEffect = this;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap, bitmap2, this);
            }
        },
        SepartConbine_BT("SepartConbine_BT") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap2(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 4;
                currentEffect = this;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap2, bitmap, this);
            }
        },
        SepartConbine_LR("SepartConbine_LR") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap2(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 4;
                currentEffect = this;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap, bitmap2, this);
            }
        },
        SepartConbine_RL("SepartConbine_RL") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap2(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 4;
                currentEffect = this;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap2, bitmap, this);
            }
        },
        RollInTurn_TB("RollInTurn_TB") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap3(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                config_length = 8;
                currentEffect = this;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap, bitmap2, this);
            }
        },
        RollInTurn_BT("RollInTurn_BT") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap3(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                currentEffect = this;
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap2, bitmap, this);
            }
        },
        RollInTurn_LR("RollInTurn_LR") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap3(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                currentEffect = this;
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap, bitmap2, this);
            }
        },
        RollInTurn_RL("RollInTurn_RL") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap3(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                currentEffect = this;
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap2, bitmap, this);
            }
        },
        Jalousie_BT("Jalousie_BT") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(ANIMATED_FRAME_CAL - i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap4(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                currentEffect = this;
                config_length = 8;
                orientation = 1;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap2, bitmap, this);
            }
        },
        Jalousie_LR("Jalousie_LR") {
            public final Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i) {
                currentEffect = this;
                setupVariable(i);
                Bitmap createBitmap = Bitmap.createBitmap(VideoConfig.INSTANCE.getVideoWidth(), VideoConfig.INSTANCE.getVideoHeight(), Config.ARGB_8888);
                combineBitmap4(new Canvas(createBitmap));
                return createBitmap;
            }

            public final void initBitmaps(Bitmap bitmap, Bitmap bitmap2) {
                currentEffect = this;
                config_length = 8;
                orientation = 0;
                camera = new Camera();
                matrix = new Matrix();
                initBitmap1(bitmap, bitmap2, this);
            }
        };
        
        String name;

        public abstract Bitmap getMask(Bitmap bitmap, Bitmap bitmap2, int i);

        public abstract void initBitmaps(Bitmap bitmap, Bitmap bitmap2);

        private Effect(String str) {
            this.name = "";
            this.name = str;
        }
    }

    static {
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL_AND_STROKE);
    }

    /* renamed from: a */
    public static void setupVariable(int i) {
        if (currentEffect == Effect.RollInTurn_BT || currentEffect == Effect.RollInTurn_LR || currentEffect == Effect.RollInTurn_RL || currentEffect == Effect.RollInTurn_TB) {
            ratio = (((((float) (config_length - 1)) * 30.0f) + 90.0f) * ((float) i)) / ((float) ANIMATED_FRAME_CAL);
        } else if (currentEffect == Effect.Jalousie_BT || currentEffect == Effect.Jalousie_LR) {
            ratio = (((float) i) * 180.0f) / ((float) ANIMATED_FRAME_CAL);
        } else {
            ratio = (((float) i) * 90.0f) / ((float) ANIMATED_FRAME_CAL);
        }
        int i2 = 180;
        if (orientation == 1) {
            float f = ratio;
            if (!(currentEffect == Effect.Jalousie_BT || currentEffect == Effect.Jalousie_LR)) {
                i2 = 90;
            }
            translateY = (f / ((float) i2)) * ((float) VideoConfig.INSTANCE.getVideoHeight());
            return;
        }
        float f2 = ratio;
        if (!(currentEffect == Effect.Jalousie_BT || currentEffect == Effect.Jalousie_LR)) {
            i2 = 90;
        }
        translateX = (f2 / ((float) i2)) * ((float) VideoConfig.INSTANCE.getVideoWidth());
    }

    /* renamed from: a */
    public static void initBitmap1(Bitmap bitmap, Bitmap bitmap2, Effect aVar) {
        Bitmap bitmap3;
        currentEffect = aVar;
        if (VideoConfig.INSTANCE.getVideoHeight() > 0 || VideoConfig.INSTANCE.getVideoWidth() > 0) {
            bitmapArrays = (Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{2, config_length});
            dx = VideoConfig.INSTANCE.getVideoWidth() / config_length;
            dy = VideoConfig.INSTANCE.getVideoHeight() / config_length;
            int i = 0;
            while (i < 2) {
                for (int i2 = 0; i2 < config_length; i2++) {
                    if (currentEffect == Effect.Jalousie_BT || currentEffect == Effect.Jalousie_LR) {
                        if (orientation == 1) {
                            bitmap3 = copyBitmap(i == 0 ? bitmap : bitmap2, 0, dy * i2, new Rect(0, dy * i2, VideoConfig.INSTANCE.getVideoWidth(), (i2 + 1) * dy));
                        } else {
                            int i3 = dx;
                            bitmap3 = copyBitmap(i == 0 ? bitmap : bitmap2, dx * i2, 0, new Rect(i3 * i2, 0, (i2 + 1) * i3, VideoConfig.INSTANCE.getVideoHeight()));
                        }
                    } else if (orientation == 1) {
                        int i4 = dx;
                        bitmap3 = copyBitmap(i == 0 ? bitmap : bitmap2, dx * i2, 0, new Rect(i4 * i2, 0, (i2 + 1) * i4, VideoConfig.INSTANCE.getVideoHeight()));
                    } else {
                        Bitmap bitmap4 = i == 0 ? bitmap : bitmap2;
                        int i5 = dy;
                        bitmap3 = copyBitmap(bitmap4, 0, i5 * i2, new Rect(0, i5 * i2, VideoConfig.INSTANCE.getVideoWidth(), (i2 + 1) * dy));
                    }
                    bitmapArrays[i][i2] = bitmap3;
                }
                i++;
            }
        }
    }

    /* renamed from: a */
    private static Bitmap copyBitmap(Bitmap bitmap, int i, int i2, Rect rect) {
        return Bitmap.createBitmap(bitmap, i, i2, rect.width(), rect.height());
    }

    /* renamed from: a */
    public static void combineBitmap1(Bitmap bitmap, Bitmap bitmap2, Canvas canvas, boolean z) {
        canvas.save();
        if (orientation == 1) {
            camera.save();
            if (z) {
                camera.rotateX(0.0f);
            } else {
                camera.rotateX(-ratio);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) ((-VideoConfig.INSTANCE.getVideoWidth()) / 2), 0.0f);
            matrix.postTranslate((float) (VideoConfig.INSTANCE.getVideoWidth() / 2), translateY);
            canvas.drawBitmap(bitmap, matrix, paint);
            camera.save();
            if (z) {
                camera.rotateX(0.0f);
            } else {
                camera.rotateX(90.0f - ratio);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) ((-VideoConfig.INSTANCE.getVideoWidth()) / 2), (float) (-VideoConfig.INSTANCE.getVideoHeight()));
            matrix.postTranslate((float) (VideoConfig.INSTANCE.getVideoWidth() / 2), translateY);
            canvas.drawBitmap(bitmap2, matrix, paint);
        } else {
            camera.save();
            if (z) {
                camera.rotateY(0.0f);
            } else {
                camera.rotateY(ratio);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(0.0f, (float) ((-VideoConfig.INSTANCE.getVideoHeight()) / 2));
            matrix.postTranslate(translateX, (float) (VideoConfig.INSTANCE.getVideoHeight() / 2));
            canvas.drawBitmap(bitmap, matrix, paint);
            camera.save();
            if (z) {
                camera.rotateY(0.0f);
            } else {
                camera.rotateY(ratio - 90.0f);
            }
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate((float) (-VideoConfig.INSTANCE.getVideoWidth()), (float) ((-VideoConfig.INSTANCE.getVideoHeight()) / 2));
            matrix.postTranslate(translateX, (float) (VideoConfig.INSTANCE.getVideoHeight() / 2));
            canvas.drawBitmap(bitmap2, matrix, paint);
        }
        canvas.restore();
    }

    /* renamed from: a */
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
                    matrix.preTranslate((float) ((-bitmap.getWidth()) / 2), 0.0f);
                    matrix.postTranslate((float) ((bitmap.getWidth() / 2) + (dx * i)), translateY);
                    canvas.drawBitmap(bitmap, matrix, paint);
                    camera.save();
                    camera.rotateX(90.0f - ratio);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((float) ((-bitmap2.getWidth()) / 2), (float) (-bitmap2.getHeight()));
                    matrix.postTranslate((float) ((bitmap2.getWidth() / 2) + (dx * i)), translateY);
                    canvas.drawBitmap(bitmap2, matrix, paint);
                } else {
                    camera.save();
                    camera.rotateY(ratio);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate(0.0f, (float) ((-bitmap.getHeight()) / 2));
                    matrix.postTranslate(translateX, (float) ((bitmap.getHeight() / 2) + (dy * i)));
                    canvas.drawBitmap(bitmap, matrix, paint);
                    camera.save();
                    camera.rotateY(ratio - 90.0f);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((float) (-bitmap2.getWidth()), (float) ((-bitmap2.getHeight()) / 2));
                    matrix.postTranslate(translateX, (float) ((bitmap2.getHeight() / 2) + (dy * i)));
                    canvas.drawBitmap(bitmap2, matrix, paint);
                }
                canvas.restore();
            } catch (Exception unused) {
            }
        }
    }

    /* renamed from: b */
    public static void combineBitmap3(Canvas canvas) {
        for (int i = 0; i < config_length; i++) {
            Bitmap[][] bitmapArr = bitmapArrays;
            Bitmap bitmap = bitmapArr[0][i];
            Bitmap bitmap2 = bitmapArr[1][i];
            float f = ratio - ((float) (i * 30));
            if (f < 0.0f) {
                f = 0.0f;
            }
            if (f > 90.0f) {
                f = 90.0f;
            }
            canvas.save();
            if (orientation == 1) {
                float f2 = (f / 90.0f) * ((float) VideoConfig.INSTANCE.getVideoHeight());
                if (f2 > ((float) VideoConfig.INSTANCE.getVideoHeight())) {
                    f2 = (float) VideoConfig.INSTANCE.getVideoHeight();
                }
                if (f2 < 0.0f) {
                    f2 = 0.0f;
                }
                camera.save();
                camera.rotateX(-f);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-bitmap.getWidth()), 0.0f);
                matrix.postTranslate((float) (bitmap.getWidth() + (dx * i)), f2);
                canvas.drawBitmap(bitmap, matrix, paint);
                camera.save();
                camera.rotateX(90.0f - f);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-bitmap2.getWidth()), (float) (-bitmap2.getHeight()));
                matrix.postTranslate((float) (bitmap2.getWidth() + (dx * i)), f2);
                canvas.drawBitmap(bitmap2, matrix, paint);
            } else {
                float f3 = (f / 90.0f) * ((float) VideoConfig.INSTANCE.getVideoWidth());
                if (f3 > ((float) VideoConfig.INSTANCE.getVideoWidth())) {
                    f3 = (float) VideoConfig.INSTANCE.getVideoWidth();
                }
                if (f3 < 0.0f) {
                    f3 = 0.0f;
                }
                camera.save();
                camera.rotateY(f);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate(0.0f, (float) ((-bitmap.getHeight()) / 2));
                matrix.postTranslate(f3, (float) ((bitmap.getHeight() / 2) + (dy * i)));
                canvas.drawBitmap(bitmap, matrix, paint);
                camera.save();
                camera.rotateY(f - 90.0f);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) (-bitmap2.getWidth()), (float) ((-bitmap2.getHeight()) / 2));
                matrix.postTranslate(f3, (float) ((bitmap2.getHeight() / 2) + (dy * i)));
                canvas.drawBitmap(bitmap2, matrix, paint);
            }
            canvas.restore();
        }
    }

    /* renamed from: c */
    public static void combineBitmap4(Canvas canvas) {
        for (int i = 0; i < config_length; i++) {
            Bitmap[][] bitmapArr = bitmapArrays;
            Bitmap bitmap = bitmapArr[0][i];
            Bitmap bitmap2 = bitmapArr[1][i];
            canvas.save();
            if (orientation == 1) {
                if (ratio < 90.0f) {
                    camera.save();
                    camera.rotateX(ratio);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((float) ((-bitmap.getWidth()) / 2), (float) ((-bitmap.getHeight()) / 2));
                    matrix.postTranslate((float) (bitmap.getWidth() / 2), (float) ((bitmap.getHeight() / 2) + (dy * i)));
                    canvas.drawBitmap(bitmap, matrix, paint);
                } else {
                    camera.save();
                    camera.rotateX(180.0f - ratio);
                    camera.getMatrix(matrix);
                    camera.restore();
                    matrix.preTranslate((float) ((-bitmap2.getWidth()) / 2), (float) ((-bitmap2.getHeight()) / 2));
                    matrix.postTranslate((float) (bitmap2.getWidth() / 2), (float) ((bitmap2.getHeight() / 2) + (dy * i)));
                    canvas.drawBitmap(bitmap2, matrix, paint);
                }
            } else if (ratio < 90.0f) {
                camera.save();
                camera.rotateY(ratio);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-bitmap.getWidth()) / 2), (float) ((-bitmap.getHeight()) / 2));
                matrix.postTranslate((float) ((bitmap.getWidth() / 2) + (dx * i)), (float) (bitmap.getHeight() / 2));
                canvas.drawBitmap(bitmap, matrix, paint);
            } else {
                camera.save();
                camera.rotateY(180.0f - ratio);
                camera.getMatrix(matrix);
                camera.restore();
                matrix.preTranslate((float) ((-bitmap2.getWidth()) / 2), (float) ((-bitmap2.getHeight()) / 2));
                matrix.postTranslate((float) ((bitmap2.getWidth() / 2) + (dx * i)), (float) (bitmap2.getHeight() / 2));
                canvas.drawBitmap(bitmap2, matrix, paint);
            }
            canvas.restore();
        }
    }
}
