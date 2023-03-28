package com.example.slide.ui.edit_image.framework;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.slide.R;
import com.example.slide.ui.edit_image.interfaces.OnSaveBitmap;

import org.wysaid.view.ImageGLSurfaceView;

import static org.wysaid.view.ImageGLSurfaceView.DisplayMode.DISPLAY_ASPECT_FIT;

public class PhotoEditorView extends StickerView {

    private static final String TAG = "PhotoEditorView";

    private Bitmap currentBitmap;
    private FilterImageView mImgSource;
    private ImageGLSurfaceView mGLSurfaceView;
    private static final int imgSrcId = 1, brushSrcId = 2, glFilterId = 3;
    //private Bitmap currentImage;

    public PhotoEditorView(Context context) {
        super(context);
        init(null);
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressLint({"Recycle", "ResourceAsColor"})
    private void init(@Nullable AttributeSet attrs) {
        //Setup image attributes
        mImgSource = new FilterImageView(getContext());
        mImgSource.setId(imgSrcId);
        mImgSource.setAdjustViewBounds(true);
        mImgSource.setBackgroundColor(getResources().getColor(R.color.black));
        LayoutParams imgSrcParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Align brush to the size of image view
        LayoutParams brushParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brushParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        brushParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        brushParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        //Setup GLSurface attributes
        mGLSurfaceView = new ImageGLSurfaceView(getContext(), attrs);
        mGLSurfaceView.setId(glFilterId);
        mGLSurfaceView.setVisibility(VISIBLE);
        mGLSurfaceView.setAlpha(1);
        mGLSurfaceView.setDisplayMode(DISPLAY_ASPECT_FIT);


        //Align brush to the size of image view
        LayoutParams imgFilterParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgFilterParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imgFilterParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        imgFilterParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        addView(mImgSource, imgSrcParam);
        //Add Gl FilterView
        addView(mGLSurfaceView, imgFilterParam);

        setDrawingCacheEnabled(true);
    }

    public void setImageSource(final Bitmap bitmap) {
        mImgSource.setImageBitmap(bitmap);
        if (mGLSurfaceView.getImageHandler() != null) {
            mGLSurfaceView.setImageBitmap(bitmap);
        } else {
            mGLSurfaceView.setSurfaceCreatedCallback(new ImageGLSurfaceView.OnSurfaceCreatedCallback() {
                @Override
                public void surfaceCreated() {
                    mGLSurfaceView.setImageBitmap(bitmap);
                }
            });
        }
        currentBitmap = bitmap;
    }


    public void initialBitmap(Bitmap bitmap) {
        setImageSource(bitmap);
    }

    public ImageGLSurfaceView getGLSurfaceView() {
        return mGLSurfaceView;
    }


    public void saveGLSurfaceViewAsBitmap(@NonNull final OnSaveBitmap onSaveBitmap) {
        if (mGLSurfaceView.getVisibility() == VISIBLE) {
            mGLSurfaceView.getResultBitmap(onSaveBitmap::onBitmapReady);
        }
    }


    public void setFilterEffect(String config) {
        mGLSurfaceView.setFilterWithConfig(config);
    }

    public void setFilterIntensity(float intensity) {
        mGLSurfaceView.setFilterIntensity(intensity);
    }

}
