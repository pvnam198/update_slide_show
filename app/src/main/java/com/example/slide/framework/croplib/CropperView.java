package com.example.slide.framework.croplib;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by DS on 30/01/2018.
 */

public class CropperView extends FrameLayout {
    private static final String TAG = "CropperView";
    private boolean gestureEnabled = true;
    private CropperGridView mGridView;
    private CropperImageView mImageView;

    public CropperView(Context paramContext) {
        super(paramContext);
        init(paramContext, null);
    }

    public CropperView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext, paramAttributeSet);
    }

    public CropperView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext, paramAttributeSet);
    }

    @TargetApi(21)
    public CropperView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext, paramAttributeSet, paramInt1, paramInt2);
        init(paramContext, paramAttributeSet);
    }

    private void init(Context paramContext, AttributeSet paramAttributeSet) {
        mImageView = new CropperImageView(paramContext, paramAttributeSet);
        mGridView = new CropperGridView(paramContext, paramAttributeSet);
        LayoutParams localLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mImageView, 0, localLayoutParams);
        addView(mGridView, 1, localLayoutParams);
        mImageView.setGestureCallback(new TouchGestureCallback());
    }

    public void cropToCenter() {
        mImageView.cropToCenter();
    }

    public void zoomImageCenterCrop() {
        mImageView.zoomImageCenterCrop();
    }

        public void fitToCenter() {
        mImageView.fitToCenter();
    }

    public Bitmap getCroppedBitmap() {
        return mImageView.getCroppedBitmap();
    }

    public int getCropperWidth() {
        if (mImageView != null) {
            return mImageView.getWidth();
        }
        return 0;
    }

    public float getMaxZoom() {
        return mImageView.getMaxZoom();
    }

    public float getMinZoom() {
        return mImageView.getMinZoom();
    }

    public int getPaddingColor() {
        return mImageView.getPaddingColor();
    }

    public CropperGridView getmGridView() {
        return mGridView;
    }

    public CropperImageView getmImageView() {
        return mImageView;
    }

    public boolean isGestureEnabled() {
        return gestureEnabled;
    }

    public boolean isMakeSquare() {
        return mImageView.isMakeSquare();
    }

    public boolean isPreScaling() {
        return mImageView.isDoPreScaling();
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
        int i = getContext().getResources().getConfiguration().orientation;
        if ((i == 1) || (i == 0)) {
            int j = MeasureSpec.getSize(paramInt1);
            setMeasuredDimension(j, MeasureSpec.makeMeasureSpec(j, MeasureSpec.EXACTLY));
            return;
        }
        int k = MeasureSpec.getSize(paramInt2);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(k, MeasureSpec.EXACTLY), k);
    }

    public void release() {
        mImageView.release();
    }

    public void replaceBitmap(Bitmap paramBitmap) {
        mImageView.replaceBitmap(paramBitmap);
    }

    public void setDebug(boolean paramBoolean) {
        mImageView.setDEBUG(paramBoolean);
    }

    public void setGestureEnabled(boolean paramBoolean) {
        gestureEnabled = paramBoolean;
        mImageView.setGestureEnabled(paramBoolean);
    }

    public void setImageBitmap(Bitmap paramBitmap) {
        mImageView.setImageBitmap(paramBitmap);
    }

    public void setMakeSquare(boolean paramBoolean) {
        mImageView.setMakeSquare(paramBoolean);
    }

    public void setMaxZoom(float paramFloat) {
        mImageView.setMaxZoom(paramFloat);
    }

    public void setMinZoom(float paramFloat) {
        mImageView.setMinZoom(paramFloat);
    }

    public void setPaddingColor(int paramInt) {
        mImageView.setPaddingColor(paramInt);
    }

    public void setPreScaling(boolean paramBoolean) {
        mImageView.setDoPreScaling(paramBoolean);
    }

    public void hideGridLine() {
        mGridView.setVisibility(GONE);
    }

    public void showGridline() {
        mGridView.setVisibility(VISIBLE);
    }

    public void setScaleFitScreen(float scaleZoom) {
        mImageView.setScaleFitScreen(scaleZoom);
    }

    private static class TouchGestureCallback
            implements CropperImageView.GestureCallback {
        private TouchGestureCallback() {
        }

        public void onGestureCompleted() {
        }

        public void onGestureStarted() {
        }
    }

}
