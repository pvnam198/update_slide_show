package com.example.slide.framework.croplib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.slide.R;
import com.example.slide.util.BitmapUtilsCrop;


/**
 * Created by DS on 30/01/2018.
 */

public class CropperImageView extends AppCompatImageView {
    private static final String TAG = "CropperImageView";
    public boolean DEBUG = false;
    private boolean doPreScaling = false;
    private boolean gestureEnabled = true;
    private boolean isAdjusting = false;
    private boolean isMaxZoomSet = false;
    private boolean mAddPaddingToMakeSquare = true;
    private float mBaseZoom;
    private Bitmap mBitmap;
    private boolean mFirstRender = true;
    private float mFocusX;
    private float mFocusY;
    private GestureCallback mGestureCallback;
    protected GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private float[] mMatrixValues = new float[9];
    private float mMaxZoom;
    private float mMinZoom;
    private int mPaintColor = -1;
    private float mPreScale;
    protected ScaleGestureDetector mScaleDetector;
    private ScaleListener mScaleListener;
    private boolean showAnimation = true;
    private float scaleZoom = 1f;

    public CropperImageView(Context paramContext) {
        super(paramContext);
        init(paramContext, null);
    }

    public CropperImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext, paramAttributeSet);
    }

    public CropperImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext, paramAttributeSet);
    }


    private boolean adjustToSides() {
        boolean changeRequired = false;

        Drawable drawable = getDrawable();
        if (drawable == null) {
            return false;
        }

        Matrix matrix = getImageMatrix();

        float tx = getMatrixValue(matrix, Matrix.MTRANS_X);
        float ty = getMatrixValue(matrix, Matrix.MTRANS_Y);
        float scaleX = getMatrixValue(matrix, Matrix.MSCALE_X);
        float scaleY = getMatrixValue(matrix, Matrix.MSCALE_Y);

        if (tx > 0) {
            tx = -tx;
            changeRequired = true;
        } else {

            // check if scrolled to left
            float xDiff = getWidth() - (scaleX) * drawable.getIntrinsicWidth();
            if (tx < xDiff) {
                tx = xDiff - tx;
                changeRequired = true;
            } else {
                tx = 0;
            }
        }

        if (ty > 0) {
            ty = -ty;
            changeRequired = true;
        } else {

            // check if scrolled to top
            float yDiff = getHeight() - (scaleY) * drawable.getIntrinsicHeight();
            if (ty < yDiff) {
                ty = yDiff - ty;
                changeRequired = true;
            } else {
                ty = 0;
            }
        }

        if (changeRequired) {

            if (showAnimation()) {
                animateAdjustment(tx, ty);
            } else {
                matrix.postTranslate(tx, ty);
                setImageMatrix(matrix);
                invalidate();
            }
        }

        return changeRequired;
    }

    private void animateAdjustment(final float xDiff, final float yDiff) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 20);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Matrix matrix = getImageMatrix();
                matrix.postTranslate(xDiff / 20, yDiff / 20);
                setImageMatrix(matrix);
                invalidate();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAdjusting = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAdjusting = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAdjusting = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isAdjusting = true;
            }
        });

        animator.start();
    }

    private void animateAdjustmentWithScale(final float xStart, final float xEnd,
                                            final float yStart, final float yEnd,
                                            final float scaleStart, final float scaleEnd) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 20);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Matrix matrix = getImageMatrix();
                matrix.reset();

                Integer value = (Integer) animation.getAnimatedValue();

                matrix.postScale((scaleEnd - scaleStart) * value / 20f + scaleStart,
                        (scaleEnd - scaleStart) * value / 20f + scaleStart);
                matrix.postTranslate((xEnd - xStart) * value / 20f + xStart,
                        (yEnd - yStart) * value / 20f + yStart);

                setImageMatrix(matrix);
                invalidate();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAdjusting = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAdjusting = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAdjusting = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isAdjusting = true;
            }
        });

        animator.start();
    }

    private void animateOverMaxZoomAdjustment() {

        Matrix matrix = getImageMatrix();
        final float scale = getScale(matrix);

        final ValueAnimator animator = ValueAnimator.ofInt(0, 20);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Matrix matrix = getImageMatrix();

                float currentScale = getScale(matrix);
                if (currentScale <= mMaxZoom) {
//                    animator.cancel();
                    return;
                }

                double expScale = Math.pow(mMaxZoom / scale, 1 / 20f);
                matrix.postScale((float) expScale, (float) expScale, mFocusX, mFocusY);
                setImageMatrix(matrix);
                invalidate();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAdjusting = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAdjusting = false;
                adjustToSides();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAdjusting = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                isAdjusting = true;
            }
        });

        animator.start();
    }

    private void cropToCenter(Drawable drawable, int frameDimen) {

        if (drawable == null) {
            if (DEBUG) {
                Log.e(TAG, "Drawable is null. I can't fit anything");
            }
            return;
        }

        if (frameDimen == 0) {
            if (DEBUG) {
                Log.e(TAG, "Frame Dimension is 0. I'm quite boggled by it.");
            }
            return;
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (DEBUG) {
            Log.i(TAG, "drawable size: (" + width + " ," + height + ")");
        }

        int min_dimen = Math.min(width, height);
        float scaleFactor = (float) min_dimen / (float) frameDimen;

        Matrix matrix = new Matrix();
        matrix.setScale(1f / scaleFactor, 1f / scaleFactor);
        matrix.postTranslate((frameDimen - width / scaleFactor) / 2,
                (frameDimen - height / scaleFactor) / 2);
        setImageMatrix(matrix);
    }

    private void fitToCenter(Drawable drawable, int frameDimen) {

        if (drawable == null) {
            if (DEBUG) {
                Log.e(TAG, "Drawable is null. I can't fit anything");
            }
            return;
        }

        if (frameDimen == 0) {
            if (DEBUG) {
                Log.e(TAG, "Frame Dimension is 0. I'm quite boggled by it.");
            }
            return;
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (DEBUG) {
            Log.i(TAG, "drawable size: (" + width + " ," + height + ")");
        }

        int min_dimen = Math.max(width, height);
        float scaleFactor = (float) min_dimen / (float) frameDimen;

        Matrix matrix = new Matrix();
        matrix.setScale(1f / scaleFactor, 1f / scaleFactor);
        matrix.postTranslate((frameDimen - width / scaleFactor) / 2,
                (frameDimen - height / scaleFactor) / 2);
        setImageMatrix(matrix);

        // If over scrolled, return back to the place.
        float tx = getMatrixValue(matrix, Matrix.MTRANS_X);
        float ty = getMatrixValue(matrix, Matrix.MTRANS_Y);
        float scaleX = getMatrixValue(matrix, Matrix.MSCALE_X);
        if (scaleX < mMinZoom) {
            float xx = getWidth() / 2 - mMinZoom * drawable.getIntrinsicWidth() / 2;
            float yy = getHeight() / 2 - mMinZoom * drawable.getIntrinsicHeight() / 2;
            animateAdjustmentWithScale(tx, xx, ty, yy, scaleX, mMinZoom);
        }
    }

    public void zoomImageCenterCrop() {
//        Matrix matrix = getImageMatrix();
//        matrix.postScale(scaleZoom, scaleZoom, getMeasuredWidth()/2, getMeasuredHeight()/2);
//        setImageMatrix(matrix);

        Drawable drawable = getDrawable();
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        int frameDimen = getMeasuredWidth();

        int min_dimen = Math.max(width, height);
        float scaleFactor = (float) min_dimen / (float) frameDimen;

        Matrix matrix = new Matrix();
        matrix.setScale(1f / scaleFactor, 1f / scaleFactor);
        matrix.postTranslate((frameDimen - width / scaleFactor) / 2,
                (frameDimen - height / scaleFactor) / 2);
        setImageMatrix(matrix);
        float tx = getMatrixValue(matrix, Matrix.MTRANS_X);
        float ty = getMatrixValue(matrix, Matrix.MTRANS_Y);
        float scaleX = getMatrixValue(matrix, Matrix.MSCALE_X);
        animateAdjustmentWithScale(tx, 0, ty, 0, scaleX, 1f);

        invalidate();
    }

    private float getMatrixValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private float getScale(Matrix matrix) {
        return getMatrixValue(matrix, Matrix.MSCALE_X);
    }

    private void init(Context paramContext, AttributeSet paramAttributeSet) {

        if (paramAttributeSet != null) {
            TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CropperView);
            this.mPaintColor = localTypedArray.getColor(R.styleable.CropperView_grid_color, this.mPaintColor);
            this.mAddPaddingToMakeSquare = localTypedArray.getBoolean(R.styleable.CropperView_nocropper__add_padding_to_make_square, true);
        }
        this.mGestureListener = new GestureListener();
        this.mGestureDetector = new GestureDetector(paramContext, this.mGestureListener, null, true);
        this.mScaleListener = new ScaleListener();
        this.mScaleDetector = new ScaleGestureDetector(paramContext, this.mScaleListener);
        setScaleType(ScaleType.CENTER_CROP);
    }

    private boolean onUp(MotionEvent event) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return false;
        }
        Matrix matrix = getImageMatrix();
        float tx = getMatrixValue(matrix, 2);
        float ty = getMatrixValue(matrix, 5);
        float scaleX = getMatrixValue(matrix, 0);
        float scaleY = getMatrixValue(matrix, 4);
        if (this.DEBUG) {
            Log.i(TAG, "onUp: " + tx + " " + ty);
            Log.i(TAG, "scale: " + scaleX);
            Log.i(TAG, "min, max, base zoom: " + this.mMinZoom + ", " + this.mMaxZoom + ", " + this.mBaseZoom);
            Log.i(TAG, "imageview size: " + getWidth() + " " + getHeight());
            Log.i(TAG, "drawable size: " + drawable.getIntrinsicWidth() + " " + drawable.getIntrinsicHeight());
            Log.i(TAG, "scaled drawable size: " + (((float) drawable.getIntrinsicWidth()) * scaleX) + " " + (((float) drawable.getIntrinsicHeight()) * scaleY));
        }
        if (scaleX <= this.mMinZoom) {
            if (this.DEBUG) {
                Log.i(TAG, "set scale: " + this.mMinZoom);
            }
            float xx = ((float) (getWidth() / 2)) - ((this.mMinZoom * ((float) drawable.getIntrinsicWidth())) / 2.0f);
            float yy = ((float) (getHeight() / 2)) - ((this.mMinZoom * ((float) drawable.getIntrinsicHeight())) / 2.0f);
            if (showAnimation()) {
                animateAdjustmentWithScale(tx, xx, ty, yy, scaleX, this.mMinZoom);
            } else {
                matrix.reset();
                matrix.setScale(this.mMinZoom, this.mMinZoom);
                matrix.postTranslate(xx, yy);
                setImageMatrix(matrix);
                invalidate();
                if (this.DEBUG) {
                    Log.i(TAG, "scale after invalidate: " + getScale(matrix));
                }
            }
            return true;
        } else if (scaleX < this.mBaseZoom) {
            float yTranslate;
            float xTranslate;
            int h = drawable.getIntrinsicHeight();
            int w = drawable.getIntrinsicWidth();
            if (h <= w) {
                yTranslate = ((float) (getHeight() / 2)) - ((((float) h) * scaleX) / 2.0f);
                if (tx >= 0.0f) {
                    xTranslate = 0.0f;
                } else {
                    float xDiff = ((float) getWidth()) - (((float) drawable.getIntrinsicWidth()) * scaleX);
                    xTranslate = tx < xDiff ? xDiff : tx;
                }
            } else {
                xTranslate = ((float) (getWidth() / 2)) - ((((float) w) * scaleX) / 2.0f);
                if (ty >= 0.0f) {
                    yTranslate = 0.0f;
                } else {
                    float yDiff = ((float) getHeight()) - (((float) drawable.getIntrinsicHeight()) * scaleY);
                    yTranslate = ty < yDiff ? yDiff : ty;
                }
            }
            if (showAnimation()) {
                matrix.reset();
                matrix.postScale(scaleX, scaleX);
                matrix.postTranslate(tx, ty);
                setImageMatrix(matrix);
                animateAdjustment(xTranslate - tx, yTranslate - ty);
            } else {
                matrix.reset();
                matrix.postScale(scaleX, scaleX);
                matrix.postTranslate(xTranslate, yTranslate);
                setImageMatrix(matrix);
                invalidate();
            }
            return true;
        } else if (!this.isMaxZoomSet || scaleX <= this.mMaxZoom) {
            if (this.DEBUG) {
                Log.i(TAG, "adjust to sides");
            }
            adjustToSides();
            return true;
        } else {
            if (this.DEBUG) {
                Log.i(TAG, "set to max zoom");
                Log.i(TAG, "isMaxZoomSet: " + this.isMaxZoomSet);
            }
            if (showAnimation()) {
                animateOverMaxZoomAdjustment();
            } else {
                matrix.postScale(this.mMaxZoom / scaleX, this.mMaxZoom / scaleX, this.mFocusX, this.mFocusY);
                setImageMatrix(matrix);
                invalidate();
                adjustToSides();
            }
            return true;
        }
    }

    public void cropToCenter() {

        Drawable localDrawable = getDrawable();
        if (localDrawable != null) {
            cropToCenter(localDrawable, getWidth());
        }
    }

    public void fitToCenter() {
        Drawable localDrawable = getDrawable();
        if (localDrawable != null) {
            fitToCenter(localDrawable, getWidth());
        }
    }

    public Bitmap getCroppedBitmap() {
        if (this.mBitmap == null) {
            Log.e(TAG, "original image is not available");
            return null;
        }
        Matrix matrix = getImageMatrix();
        if (this.doPreScaling) {
            matrix.postScale(1f / this.mPreScale, 1f / this.mPreScale);
        }
        float xTrans = getMatrixValue(matrix, 2);
        float yTrans = getMatrixValue(matrix, 5);
        float scale = getMatrixValue(matrix, 0);
        if (this.DEBUG) {
            Log.i(TAG, "xTrans: " + xTrans + ", yTrans: " + yTrans + " , scale: " + scale);
        }
        if (this.DEBUG) {
            Log.i(TAG, "old bitmap: " + this.mBitmap.getWidth() + " " + this.mBitmap.getHeight());
        }
        if (xTrans <= 0.0f || yTrans <= 0.0f || scale > this.mMinZoom) {
            float cropY = (-yTrans) / scale;
            float Y = ((float) getHeight()) / scale;
            float cropX = (-xTrans) / scale;
            float X = ((float) getWidth()) / scale;
            if (this.DEBUG) {
                Log.i(TAG, "cropY: " + cropY);
                Log.i(TAG, "Y: " + Y);
                Log.i(TAG, "cropX: " + cropX);
                Log.i(TAG, "X: " + X);
            }
            if (cropY + Y > ((float) this.mBitmap.getHeight())) {
                cropY = ((float) this.mBitmap.getHeight()) - Y;
                if (this.DEBUG) {
                    Log.i(TAG, "readjust cropY to: " + cropY);
                }
            } else if (cropY < 0.0f) {
                cropY = 0.0f;
                if (this.DEBUG) {
                    Log.i(TAG, "readjust cropY to: " + 0.0f);
                }
            }
            if (cropX + X > ((float) this.mBitmap.getWidth())) {
                cropX = ((float) this.mBitmap.getWidth()) - X;
                if (this.DEBUG) {
                    Log.i(TAG, "readjust cropX to: " + cropX);
                }
            } else if (cropX < 0.0f) {
                cropX = 0.0f;
                if (this.DEBUG) {
                    Log.i(TAG, "readjust cropX to: " + 0.0f);
                }
            }
            Bitmap bitmap;
            if (this.mBitmap.getHeight() <= this.mBitmap.getWidth()) {
                if (yTrans >= 0.0f) {
                    bitmap = Bitmap.createBitmap(this.mBitmap, (int) cropX, 0, (int) X, this.mBitmap.getHeight(), null, true);
                    if (this.mAddPaddingToMakeSquare) {
                        bitmap = BitmapUtilsCrop.addPadding(bitmap, this.mPaintColor);
                    }
                } else {
                    bitmap = Bitmap.createBitmap(this.mBitmap, (int) cropX, (int) cropY, (int) X, (int) Y, null, true);
                }
                if (!this.DEBUG) {
                    return bitmap;
                }
                Log.i(TAG, "width should be: " + this.mBitmap.getWidth());
                Log.i(TAG, "crop bitmap: " + bitmap.getWidth() + " " + bitmap.getHeight());
                return bitmap;
            } else if (xTrans < 0.0f) {
                return Bitmap.createBitmap(this.mBitmap, (int) cropX, (int) cropY, (int) X, (int) Y, null, true);
            } else {
                bitmap = Bitmap.createBitmap(this.mBitmap, 0, (int) cropY, this.mBitmap.getWidth(), (int) Y, null, true);
                if (this.mAddPaddingToMakeSquare) {
                    return BitmapUtilsCrop.addPadding(bitmap, this.mPaintColor);
                }
                return bitmap;
            }
        } else if (this.mAddPaddingToMakeSquare) {
            return BitmapUtilsCrop.addPadding(this.mBitmap, this.mPaintColor);
        } else {
            return this.mBitmap;
        }
    }

    public float getMaxZoom() {
        return this.mMaxZoom;
    }

    public float getMinZoom() {
        return this.mMinZoom;
    }

    public int getPaddingColor() {
        return this.mPaintColor;
    }

    public boolean isDoPreScaling() {
        return this.doPreScaling;
    }

    public boolean isGestureEnabled() {
        return this.gestureEnabled;
    }

    public boolean isMakeSquare() {
        return this.mAddPaddingToMakeSquare;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.DEBUG) {
            Log.i(TAG, "onLayout: " + changed + " [" + left + ", " + top + ", " + right + ", " + bottom + "]");
        }
        if ((changed || this.mFirstRender) && this.mFirstRender) {
            Drawable drawable = getDrawable();
            if (drawable != null) {
                this.mMinZoom = ((float) (bottom - top)) / ((float) Math.max(drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth()));
                this.mBaseZoom = ((float) (bottom - top)) / ((float) Math.min(drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth()));
                fitToCenter(drawable, bottom - top);
                this.mFirstRender = false;
            } else if (this.DEBUG) {
                Log.e(TAG, "drawable is null");
            }
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Matrix matrix = getImageMatrix();
        matrix.postTranslate(-distanceX, -distanceY);
        setImageMatrix(matrix);
        invalidate();
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.isAdjusting) {
            return true;
        }
        if (event.getActionMasked() == 0 && this.mGestureCallback != null) {
            this.mGestureCallback.onGestureStarted();
        }
        this.mScaleDetector.onTouchEvent(event);
        if (!this.mScaleDetector.isInProgress()) {
            this.mGestureDetector.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case 1:
                if (this.mGestureCallback != null) {
                    this.mGestureCallback.onGestureCompleted();
                }
                return onUp(event);
            default:
                return true;
        }
    }

    public void release() {
        setImageBitmap(null);
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
        }
    }

    public void replaceBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Can not replace with null bitmap");
        } else if (this.mBitmap == null) {
            setImageBitmap(bitmap);
        } else if (this.mBitmap.getWidth() == bitmap.getWidth() && this.mBitmap.getHeight() == bitmap.getHeight()) {
            super.setImageBitmap(bitmap);
            this.mBitmap = bitmap;
        } else {
            Log.e(TAG, "Bitmap is of different size. Not replacing");
        }
    }

    public void setDEBUG(boolean paramBoolean) {
        this.DEBUG = paramBoolean;
    }

    public void setDoPreScaling(boolean paramBoolean) {
        this.doPreScaling = paramBoolean;
    }

    public void setGestureCallback(GestureCallback paramGestureCallback) {
        this.mGestureCallback = paramGestureCallback;
    }

    public void setGestureEnabled(boolean paramBoolean) {
        this.gestureEnabled = paramBoolean;
    }

    public void setImageBitmap(Bitmap bm) {
        this.mFirstRender = true;
        if (bm == null) {
            this.mBitmap = null;
            super.setImageBitmap(null);
            return;
        }
        if (bm.getHeight() > 1280 || bm.getWidth() > 1280) {
            Log.w(TAG, "Bitmap size greater than 1280. This might cause memory issues");
        }
        this.mBitmap = bm;
        if (this.doPreScaling) {
            this.mPreScale = ((float) Math.max(bm.getWidth(), bm.getHeight())) / ((float) getWidth());
            super.setImageBitmap(Bitmap.createScaledBitmap(bm, (int) (((float) bm.getWidth()) / this.mPreScale), (int) (((float) bm.getHeight()) / this.mPreScale), false));
        } else {
            this.mPreScale = 1f;
            super.setImageBitmap(bm);
        }

//        this.mPreScale = 1f;
//        super.setImageBitmap(bm);
        requestLayout();
    }

    public void setMakeSquare(boolean paramBoolean) {
        this.mAddPaddingToMakeSquare = paramBoolean;
    }

    public void setMaxZoom(float mMaxZoom) {
        if (mMaxZoom <= 0.0f) {
            Log.e(TAG, "Max zoom must be greater than 0");
            return;
        }
        this.mMaxZoom = mMaxZoom;
        this.isMaxZoomSet = true;
    }

    public void setMinZoom(float paramFloat) {
        if (paramFloat <= 0.0F) {
            Log.e("CropperImageView", "Min zoom must be greater than 0");
            return;
        }
        this.mMinZoom = paramFloat;
    }

    public void setPaddingColor(int paramInt) {
        this.mPaintColor = paramInt;
    }

    public void setShowAnimation(boolean paramBoolean) {
        this.showAnimation = paramBoolean;
    }

    public boolean showAnimation() {
        return this.showAnimation;
    }

    public void setScaleFitScreen(float scaleZoom) {
        this.scaleZoom = scaleZoom;
    }

    public static abstract interface GestureCallback {
        public abstract void onGestureCompleted();

        public abstract void onGestureStarted();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private GestureListener() {
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (CropperImageView.this.gestureEnabled && e1 != null && e2 != null && e1.getPointerCount() <= 1 && e2.getPointerCount() <= 1) {
                CropperImageView.this.onScroll(e1, e2, distanceX, distanceY);
            }
            return false;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        protected boolean mScaled;

        private ScaleListener() {
            this.mScaled = false;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            if (!CropperImageView.this.gestureEnabled) {
                return false;
            }
            Matrix matrix = CropperImageView.this.getImageMatrix();
            CropperImageView.this.mFocusX = detector.getFocusX();
            CropperImageView.this.mFocusY = detector.getFocusY();
            matrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            CropperImageView.this.setImageMatrix(matrix);
            CropperImageView.this.invalidate();
            return true;
        }
    }
}
