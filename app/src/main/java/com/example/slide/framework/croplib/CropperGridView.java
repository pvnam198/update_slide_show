package com.example.slide.framework.croplib;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.slide.R;

public class CropperGridView extends View {
    private int myAlpha = 200;
    private int lineColor = getResources().getColor(R.color.colorBlue);
    private Paint linePaint;
    private Path gridPath;
    private int strokeWidth = 3;

    public CropperGridView(Context paramContext) {
        super(paramContext);
        init();
    }

    public CropperGridView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext);
        init();
    }

    public CropperGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext);
        init();
    }

    @TargetApi(21)
    public CropperGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
        super(paramContext);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        strokeWidth = getResources().getDimensionPixelSize(R.dimen.crop_line_stroke);
        linePaint.setStrokeWidth(strokeWidth);
        linePaint.setAlpha(myAlpha);
        gridPath = new Path();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        gridPath.reset();
        gridPath.moveTo(0, 0);
        gridPath.lineTo(width, 0);
        gridPath.lineTo(width, height);
        gridPath.lineTo(0, height);
        gridPath.lineTo(0, 0);
        canvas.drawPath(gridPath, linePaint);
        canvas.drawLine(width / 3, 0, width / 3, height, linePaint);
        canvas.drawLine(width * 2 / 3, 0, width * 2 / 3, height, linePaint);
        canvas.drawLine(0, height / 3, width, height / 3, linePaint);
        canvas.drawLine(0, (height * 2) / 3, width, (height * 2) / 3, linePaint);
    }

}
