package com.example.slide.ui.edit_image.framework.splash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;

import com.example.slide.R;
import com.example.slide.ui.edit_image.framework.FloatingItem;
import com.example.slide.ui.edit_image.framework.StickerView;
import com.example.slide.ui.edit_image.utils.SystemUtil;

import java.util.Random;

public class SplashView extends AppCompatImageView {

    private PointF midPoint = new PointF();
    private FloatingItem floatingItem;
    @StickerView.ActionMode
    private int currentMode = StickerView.ActionMode.NONE;
    private float oldDistance = 0f;
    private float oldRotation = 0f;
    private final float[] tmp = new float[2];
    private final float[] point = new float[2];
    private final Matrix downMatrix = new Matrix();
    private final Matrix moveMatrix = new Matrix();
    private final PointF currentCenterPoint = new PointF();
    public static final int SHAPE = 0;
    public static final int DRAW = 1;
    public int currentSplashMode = SHAPE;
    private Paint mDrawPaint, paintCircle;
    private Path mPath;
    private int brushBitmapSize = 100;
    private float mTouchX, mTouchY;
    private float currentX;
    private float currentY;
    private boolean showTouchIcon = false;
    private Bitmap bitmap;

    public SplashView(Context context) {
        super(context);
        init();
    }

    public SplashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public SplashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setBitmap(bm);
    }

    public void setBitmap(Bitmap bm) {
        bitmap = bm;
    }

    private void init() {
        mDrawPaint = new Paint();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setDither(true);
        mDrawPaint.setStyle(Paint.Style.FILL);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStrokeWidth(brushBitmapSize);
        mDrawPaint.setMaskFilter(new BlurMaskFilter(20F, BlurMaskFilter.Blur.NORMAL));
        mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mDrawPaint.setStyle(Paint.Style.STROKE);

        paintCircle = new Paint();
        paintCircle.setAntiAlias(true);
        paintCircle.setDither(true);
        paintCircle.setColor(getResources().getColor(R.color.colorAccent));
        paintCircle.setStrokeWidth(SystemUtil.dpToPx(getContext(), 2));
        paintCircle.setStyle(Paint.Style.STROKE);
        mPath = new Path();

    }

    public void updateBrush() {
        mPath = new Path();
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setDither(true);
        mDrawPaint.setStyle(Paint.Style.FILL);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStrokeWidth(brushBitmapSize);
        mDrawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        mDrawPaint.setStyle(Paint.Style.STROKE);
        showTouchIcon = false;
        invalidate();
    }

    @NonNull
    public void addSticker(@NonNull FloatingItem floatingItem) {
        addSticker(floatingItem, FloatingItem.Position.CENTER);
    }

    public void addSticker(@NonNull final FloatingItem floatingItem,
                           final @FloatingItem.Position int position) {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(floatingItem, position);
        } else {
            post(() -> addStickerImmediately(floatingItem, position));
        }
    }

    protected void addStickerImmediately(@NonNull FloatingItem floatingItem, @FloatingItem.Position int position) {
        this.floatingItem = floatingItem;
        setStickerPosition(floatingItem, position);
        invalidate();
    }

    protected void setStickerPosition(@NonNull FloatingItem floatingItem, @FloatingItem.Position int position) {
        float width = getWidth();
        float height = getHeight();

        float scale;
        if (width > height)
            scale = (height * 4 / 5) / floatingItem.getHeight();
        else
            scale = (width * 4 / 5) / floatingItem.getWidth();

        midPoint.set(0, 0);
        downMatrix.reset();
        moveMatrix.set(downMatrix);
        moveMatrix.postScale(scale, scale);
        int degree = new Random().nextInt(20) - 10;
        moveMatrix.postRotate(degree, midPoint.x, midPoint.y);

        float offsetX = width - (int) (floatingItem.getWidth() * scale);
        float offsetY = height - (int) (floatingItem.getHeight() * scale);
        if ((position & FloatingItem.Position.TOP) > 0) {
            offsetY /= 4f;
        } else if ((position & FloatingItem.Position.BOTTOM) > 0) {
            offsetY *= 3f / 4f;
        } else {
            offsetY /= 2f;
        }
        if ((position & FloatingItem.Position.LEFT) > 0) {
            offsetX /= 4f;
        } else if ((position & FloatingItem.Position.RIGHT) > 0) {
            offsetX *= 3f / 4f;
        } else {
            offsetX /= 2f;
        }
        moveMatrix.postTranslate(offsetX, offsetY);
        floatingItem.setMatrix(moveMatrix);
    }

    @SuppressLint("CanvasSize")
    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled())
            return;
        super.onDraw(canvas);
        if (currentSplashMode == SHAPE) {
            drawStickers(canvas);
        } else {
            canvas.drawPath(mPath, mDrawPaint);
            if (showTouchIcon) {
                canvas.drawCircle(currentX, currentY, brushBitmapSize / 2, paintCircle);
            }

        }
    }

    protected void drawStickers(Canvas canvas) {

        if (floatingItem != null && floatingItem.isShow()) {
            floatingItem.draw(canvas);
        }

        invalidate();
    }

    protected float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    protected float calculateDistance(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateRotation(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    @NonNull
    protected PointF calculateMidPoint(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            midPoint.set(0, 0);
            return midPoint;
        }
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        midPoint.set(x, y);
        return midPoint;
    }

    @NonNull
    protected PointF calculateMidPoint() {
        floatingItem.getMappedCenterPoint(midPoint, point, tmp);
        return midPoint;
    }

    protected boolean isInStickerArea(@NonNull FloatingItem floatingItem, float downX, float downY) {
        tmp[0] = downX;
        tmp[1] = downY;
        return floatingItem.contains(tmp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);
        float x = event.getX();
        float y = event.getY();
        currentX = x;
        currentY = y;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!onTouchDown(x, y)) {
                    invalidate();
                    return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (floatingItem != null && isInStickerArea(floatingItem, event.getX(1),
                        event.getY(1))) {
                    currentMode = StickerView.ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(x, y, event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                currentMode = StickerView.ActionMode.NONE;
                break;
        }

        return true;
    }

    protected void constrainSticker(@NonNull FloatingItem floatingItem) {
        float moveX = 0;
        float moveY = 0;
        int width = getWidth();
        int height = getHeight();
        floatingItem.getMappedCenterPoint(currentCenterPoint, point, tmp);
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x;
        }

        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x;
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y;
        }

        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y;
        }

        floatingItem.getMatrix().postTranslate(moveX, moveY);
    }


    protected synchronized void handleCurrentMode(float x, float y, MotionEvent event) {
        if (currentSplashMode == SHAPE) {
            switch (currentMode) {
                case StickerView.ActionMode.NONE:
                case StickerView.ActionMode.CLICK:
                    break;
                case StickerView.ActionMode.DRAG:
                    if (floatingItem != null) {
                        moveMatrix.set(downMatrix);
                        moveMatrix.postTranslate(event.getX() - mTouchX, event.getY() - mTouchY);
                        floatingItem.setMatrix(moveMatrix);
                    }
                    break;
                case StickerView.ActionMode.ZOOM_WITH_TWO_FINGER:
                    if (floatingItem != null) {
                        float newDistance = calculateDistance(event);
                        float newRotation = calculateRotation(event);

                        moveMatrix.set(downMatrix);
                        moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                                midPoint.y);
                        moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
                        floatingItem.setMatrix(moveMatrix);
                    }

                    break;

            }
        } else {
            mPath.quadTo(mTouchX, mTouchY, (x + mTouchX) / 2, (y + mTouchY) / 2);
            mTouchX = x;
            mTouchY = y;
        }
    }

    @Nullable
    protected FloatingItem findHandlingSticker() {
        if (isInStickerArea(floatingItem, mTouchX, mTouchY)) {
            return floatingItem;
        }
        return null;
    }

    protected boolean onTouchDown(float x, float y) {
        currentMode = StickerView.ActionMode.DRAG;
        mTouchX = x;
        mTouchY = y;
        currentX = x;
        currentY = y;
        if (currentSplashMode == SHAPE && floatingItem != null) {
            midPoint = calculateMidPoint();
            oldDistance = calculateDistance(midPoint.x, midPoint.y, mTouchX, mTouchY);
            oldRotation = calculateRotation(midPoint.x, midPoint.y, mTouchX, mTouchY);
            FloatingItem handlingFloatingItem = findHandlingSticker();
            if (handlingFloatingItem != null) {
                downMatrix.set(floatingItem.getMatrix());
            }

            if (handlingFloatingItem == null) {
                return false;
            }
        } else {
            showTouchIcon = true;
            mPath.reset();
            mPath.moveTo(x, y);
        }
        invalidate();
        return true;
    }

    protected void onTouchUp(@NonNull MotionEvent event) {
        showTouchIcon = false;
        if (currentSplashMode == SHAPE) {
            currentMode = StickerView.ActionMode.NONE;
        }
        invalidate();
    }

    public Bitmap getBitmap(Bitmap originalBitmap) {

        int width = getWidth();
        int height = getHeight();
        Bitmap bmpMonochrome = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        canvas.drawBitmap(bitmap, null, new RectF(0, 0, width, height), null);
        if (currentSplashMode == SHAPE) {
            drawStickers(canvas);
        }

        Bitmap bmpMonochrome1 = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bmpMonochrome1);
        canvas1.drawBitmap(originalBitmap, null, new RectF(0, 0, originalBitmap.getWidth(), originalBitmap.getHeight()), null);
        canvas1.drawBitmap(bmpMonochrome, null, new RectF(0, 0, originalBitmap.getWidth(), originalBitmap.getHeight()), null);

        return bmpMonochrome1;

    }

}
