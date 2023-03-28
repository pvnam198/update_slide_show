package com.example.slide.framework.cutter.myrangeseekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.slide.R;
import com.example.slide.framework.cutter.myrangeseekbar.libs.MoveGestureDetector;
import com.example.slide.util.DimenUtils;

import java.util.Locale;

public class AudioCutterView extends View {

    public static final int TOP = 0;
    public static final int BOTTOM = 180;

    public static final int SEEK_NONE = -1;
    public static final int SEEK_END = 0;
    public static final int SEEK_START = 1;
    public static final int SEEK_PROGRESS = 2;

    public static final int STATE_NONE = -1;
    public static final int STATE_PROGRESS_PRESS = 0;
    public static final int STATE_PROGRESS_SEEK = 1;
    public static final int STATE_SEEK_START = 2;
    public static final int STATE_SEEK_END = 3;

    private float mOffset = 0f;
    private float mMinOffset, mMaxOffset;

    private int topHeight, bottomHeight, progressHeight;
    private int labelHeight;
    private Paint labelPaint;

    private int dp1, dp4, dp8, dp12;
    private RectF mRectF = new RectF();

    private int mValueLabelTextColor;
    private int mCount;
    private int mProgress = 0;
    private int mOldProgress = 0;
    private int mMinProgress = 0, mMaxProgress = -1;

    private MoveGestureDetector mMoveDetector;
    private float mValueLabelTextSize;
    private ValueLabelFormatter mValueLabelFormatter;
    private Rect mBounds = new Rect();

    private boolean mSkipMove;
    private int moveState = STATE_NONE;
    private int mPressedPosition;

    private OnValueChangedListener mListener;

    private Bitmap topSeekThumb;
    private Bitmap bottomSeekThumb;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mBitmapPaint;
    private Paint backgroundLinePaint, activeLinePaint;

    private Boolean isHideActiveLinePaint = false;
    private Boolean isHideLabel = true;

    public void setIsHideActiveLinePaint(boolean isHideActiveLinePaint) {
        this.isHideActiveLinePaint = isHideActiveLinePaint;
    }

    public void setIsHideLabel(boolean isHideLabel) {
        this.isHideLabel = isHideLabel;
    }

    public AudioCutterView(Context context) {
        super(context);
        init(context, null);
    }

    public AudioCutterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AudioCutterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyAudioCutting);

            mValueLabelTextColor =
                    a.getColor(R.styleable.MyAudioCutting_valueLabelTextColor, Color.WHITE);
            mValueLabelTextSize = a.getDimension(R.styleable.MyAudioCutting_valueLabelTextSize,
                    DimenUtils.convertSpToPixel(12, context));

            mCount = a.getInt(R.styleable.MyAudioCutting_count, 11);
            mCount = Math.max(mCount, 2);

            mProgress = a.getInt(R.styleable.MyAudioCutting_progress1, 0);
            mMinProgress = a.getInt(R.styleable.MyAudioCutting_minProgress, 0);
            mMaxProgress = a.getInt(R.styleable.MyAudioCutting_maxProgress, mCount - 1);

            a.recycle();
        } else {
            mValueLabelTextSize = DimenUtils.convertSpToPixel(16, context);
            mValueLabelTextColor = Color.WHITE;
            mCount = 11;
        }

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(false);

        backgroundLinePaint = new Paint();
        backgroundLinePaint.setAntiAlias(false);
        backgroundLinePaint.setStrokeWidth(DimenUtils.convertSpToPixel(1, context));
        backgroundLinePaint.setColor(getResources().getColor(R.color.white));
        activeLinePaint = new Paint();
        activeLinePaint.setAntiAlias(false);
        activeLinePaint.setStrokeWidth(DimenUtils.convertSpToPixel(2, context));
        activeLinePaint.setColor(getResources().getColor(R.color.colorViolet));
        activeLinePaint.setStrokeCap(Paint.Cap.ROUND);

        labelPaint = new Paint();
        labelPaint.setColor(Color.WHITE);
        labelPaint.setTextSize(mValueLabelTextSize);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mValueLabelTextSize);
        mPaint.setColor(Color.WHITE);

        topHeight = DimenUtils.convertDpToPixel(18, getContext());
        bottomHeight = DimenUtils.convertDpToPixel(18, getContext());
        progressHeight = DimenUtils.convertDpToPixel(2, getContext());
        labelHeight = DimenUtils.convertDpToPixel(14, getContext());
        dp1 = DimenUtils.convertDpToPixel(1, getContext());
        dp4 = DimenUtils.convertDpToPixel(4, getContext());
        dp8 = DimenUtils.convertDpToPixel(8, getContext());
        dp12 = DimenUtils.convertDpToPixel(12, getContext());

        setValueLabelFormatter(new ValueLabelFormatter() {

            @Override
            public String getLabel(int input) {
                return Integer.toString(input);
            }
        });

        topSeekThumb =
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_cut_music_start_node);
        bottomSeekThumb =
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_cut_music_end_node);


        mMoveDetector = new MoveGestureDetector(context, new MoveListener());
    }

    @Override
    protected void onDetachedFromWindow() {
        if (topSeekThumb != null) {
            topSeekThumb.recycle();
        }
        super.onDetachedFromWindow();
    }

    public void setValueLabelTextColor(@ColorInt int valueLabelTextColor) {
        mValueLabelTextColor = valueLabelTextColor;
        invalidate();
    }

    @ColorInt
    public int getValueLabelTextColor() {
        return mValueLabelTextColor;
    }

    public void setValueLabelTextSize(
            @FloatRange(from = Float.MIN_VALUE) float valueLabelTextSize) {
        if (valueLabelTextSize <= 0) {
            throw new IllegalArgumentException("Value label text size must be a positive number.");
        }
        mValueLabelTextSize = valueLabelTextSize;
        invalidate();
    }

    public float getValueLabelTextSize() {
        return mValueLabelTextSize;
    }

    public void setCount(@IntRange(from = 1) int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Count must larger than 1.");
        }
        mCount = count + 1;
        checkProgressBound();
        invalidate();
    }

    public int getCount() {
        return mCount;
    }

    public void setValueLabelFormatter(@NonNull ValueLabelFormatter formatter) {
        mValueLabelFormatter = formatter;
    }

    public ValueLabelFormatter getValueLabelFormatter() {
        return mValueLabelFormatter;
    }

    public void setProgress(int progress) {
        if (moveState == STATE_PROGRESS_PRESS)
            return;
        int _progress = mProgress;
        mProgress = progress;
        if (mProgress < 0) {
            mProgress = 0;
        }
        if (mProgress >= mCount) {
            mProgress = mCount;
        }

        if (_progress != mProgress && mListener != null) {
            mListener.onValueChanged(mProgress, false);
        }

        invalidate();
    }

    public void setMinProgress(int progress) {
        int _progress = mMinProgress;
        mMinProgress = progress;
        checkProgressBound();
        if (_progress != mMinProgress && mListener != null) {
            mListener.onStartChanged(mMinProgress, false);
        }
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public int getMinProgress() {
        return mMinProgress;
    }

    public void setMaxProgress(int progress) {
        int _progress = mMaxProgress;
        mMaxProgress = progress;
        checkProgressBound();
        if (_progress != mMaxProgress && mListener != null) {
            mListener.onEndChanged(mMaxProgress, false);
        }
        invalidate();
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    private void checkProgressBound() {
        if (mMaxProgress == -1) {
            mMaxProgress = mCount - 1;
        } else if (mMaxProgress > mCount - 1) {
            mMaxProgress = mCount - 1;
        }
        if (mMinProgress >= mMaxProgress) {
            mMinProgress = mMaxProgress - 1;
        }
    }

    public void setOnValueChangedListener(@Nullable OnValueChangedListener listener) {
        mListener = listener;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return isEnabled() && handleTouchEvent(event);
    }

    private void requestDisallowInterceptTouchEvent(ViewParent parent, boolean isDragging) {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(isDragging);
            requestDisallowInterceptTouchEvent(parent.getParent(), isDragging);
        }
    }


    private boolean handleTouchEvent(MotionEvent event) {
        if (mCount < 2) {
            mMoveDetector.onTouchEvent(event);
            return true;
        }
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        float length = measuredWidth - getPaddingStart() - getPaddingEnd();

        int top = 0;
        int bottom = measuredHeight - getPaddingBottom() - bottomHeight;
        int start = getPaddingStart();
        int end = measuredWidth - getPaddingEnd();

        float minPosition = getPosition(SEEK_NONE, mMinProgress);
        float maxPosition = getPosition(SEEK_NONE, mMaxProgress);
        float progressPosition = getPosition(SEEK_NONE, mProgress);

        int halfCutterButtonWidth = topSeekThumb.getWidth() / 2 + dp8;
        int halfProgressButtonWidth = topSeekThumb.getWidth() / 2 + dp8;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("kimkakatB", "handleTouchEvent:ACTION_DOWN" + event.getAction());
            mOffset = 0;
            mSkipMove = false;

            float xTouch = event.getX();
            float yTouch = event.getY();

            if (minPosition - halfCutterButtonWidth <= xTouch &&
                    xTouch <= minPosition + halfCutterButtonWidth && yTouch > top &&
                    yTouch < measuredHeight / 2f) {
                moveState = STATE_SEEK_START;
                mMinOffset = getPosition(SEEK_NONE, 0) - minPosition;
                mMaxOffset = getPosition(SEEK_NONE, mMaxProgress - 1) - minPosition;
            } else if (maxPosition - halfCutterButtonWidth <= xTouch &&
                    xTouch <= maxPosition + halfCutterButtonWidth &&
                    yTouch > measuredHeight / 2f) {
                moveState = STATE_SEEK_END;
                mMinOffset = getPosition(SEEK_NONE, mMinProgress + 1) - maxPosition;
                mMaxOffset = getPosition(SEEK_NONE, mCount - 1) - maxPosition;
            } else if (progressPosition - halfProgressButtonWidth <= xTouch &&
                    xTouch <= halfProgressButtonWidth + progressPosition) {
                mOldProgress = mProgress;
                moveState = STATE_PROGRESS_SEEK;
                mMinOffset = getPosition(SEEK_NONE, 0) - progressPosition;
                mMaxOffset = getPosition(SEEK_NONE, mCount - 1) - progressPosition;
            } else if (xTouch < getPaddingStart() || xTouch > end) {
                moveState = STATE_NONE;
            } else {
                moveState = STATE_PROGRESS_PRESS;
                mPressedPosition = (int) getClosestPosition(xTouch)[0];
            }
            Log.d("kimkakatt",
                    "ontouch ACTION_DOWN:" + xTouch + "state:" + moveState + "mPressedPosition:" +
                            mPressedPosition);

            if (moveState != STATE_NONE && moveState != STATE_PROGRESS_PRESS) {
                requestDisallowInterceptTouchEvent(getParent(), true);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("kimkakatt", "ontouch ACTION_UP mPaddingPosition state:" + moveState);
            if (moveState == STATE_NONE) {
                mOffset = 0;
                mMoveDetector.onTouchEvent(event);
                return true;
            }
            if (moveState == STATE_PROGRESS_PRESS) {
                Log.d("kimkakatt", "ontouch ACTION_UP1:");
                float p = event.getX();
                final int position = (int) getClosestPosition(p)[0];
                if (!mSkipMove) {
                    if (mListener != null) {
                        mListener.onValueChanged(position, true);
                    }

                    setEnabled(false);

                    mOffset = 0;
                    float dis = length / (mCount - 1) * (position - mProgress);
                    Log.d("kimkakatt", "ontouch ACTION_UP1dis:" + dis + "   position" + position + "   mProgress" + mProgress);
                    ValueAnimator animator = ValueAnimator.ofFloat(mOffset, mOffset + dis);
                    animator.setDuration(250);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mOffset = (float) animation.getAnimatedValue();
                            invalidate();
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mOffset = 0;
                            mProgress = position;
                            moveState = STATE_NONE;
                            setEnabled(true);
                            invalidate();
                        }
                    });
                    animator.start();
                }
            } else {

                float p;
                if (moveState == STATE_SEEK_START) {
                    p = getPosition(SEEK_START, mMinProgress);
                } else if (moveState == STATE_SEEK_END) {
                    p = getPosition(SEEK_END, mMaxProgress);
                } else {
                    p = getPosition(SEEK_PROGRESS, mOldProgress);
                }
                Log.d("kimkakatt", "ontouch ACTION_UP2 p:" + p);
                float[] closestPosition = getClosestPosition(p);
                float dis = closestPosition[1];
                int position = (int) closestPosition[0];

                if (mListener != null) {
                    if (moveState == STATE_SEEK_START) {
                        mListener.onStartChanged(position, true);
                    } else if (moveState == STATE_SEEK_END) {
                        mListener.onEndChanged(position, true);
                    } else {
                        mListener.onValueChanged(position, true);
                    }
                }

                if (moveState == STATE_PROGRESS_SEEK) {
                    mProgress = position;
                    moveState = STATE_NONE;
                } else {
                    setEnabled(false);
                    final int _position = position;
                    ValueAnimator animator = ValueAnimator.ofFloat(mOffset, mOffset + dis);
                    animator.setInterpolator(new DecelerateInterpolator(2.5f));
                    animator.setDuration(250);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mOffset = (float) animation.getAnimatedValue();
                            invalidate();
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mOffset = 0;
                            if (moveState == STATE_SEEK_START) {
                                mMinProgress = _position;
                            } else {
                                mMaxProgress = _position;
                            }
                            moveState = STATE_NONE;
                            setEnabled(true);
                            invalidate();
                        }
                    });
                    animator.start();
                }

            }
            mPressedPosition = -1;
            requestDisallowInterceptTouchEvent(getParent(), false);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            Log.d("kimkakatt", "ontouch ACTION_CANCEL:");
            //			if (mPaddingPosition == mMinProgress || mPaddingPosition == mMaxProgress) {
            //				setEnabled(false);
            //
            //				ValueAnimator animator = ValueAnimator.ofFloat(mOffset, 0);
            //				animator.setInterpolator(new DecelerateInterpolator(2.5f));
            //				animator.setDuration(250);
            //				animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            //
            //					@Override
            //					public void onAnimationUpdate(ValueAnimator animation) {
            //						mOffset = (float) animation.getAnimatedValue();
            //						invalidate();
            //					}
            //				});
            //				animator.addListener(new AnimatorListenerAdapter() {
            //
            //					@Override
            //					public void onAnimationEnd(Animator animation) {
            //						super.onAnimationEnd(animation);
            //						mOffset = 0;
            //						mPaddingPosition = -1;
            //						setEnabled(true);
            //						invalidate();
            //					}
            //				});
            //				animator.start();
            //			} else {
            //				mOffset = 0;
            //			}
            //
            //			mPaddingPosition = -1;
            //			mPressedPosition = -1;
            //			requestDisallowInterceptTouchEvent(getParent(), false);
        }
        mMoveDetector.onTouchEvent(event);
        invalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getSize() + getPaddingTop() + getPaddingBottom());

    }

    public int getSize() {
        return (int) (topHeight + progressHeight + bottomHeight + labelHeight * 2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        float startX = getPosition(SEEK_START, mMinProgress);
        float endX = getPosition(SEEK_END, mMaxProgress);
        drawProgress(canvas, measuredWidth, measuredHeight, startX, endX);
        drawAllThumb(canvas, measuredHeight, startX, endX);
        /*if (isHideLabel)
            drawAllLabel(canvas, measuredWidth, measuredHeight, startX, endX);*/
    }

    private void drawAllThumb(Canvas canvas, int measuredHeight, float startX, float endX) {
        canvas.drawBitmap(topSeekThumb, startX - topSeekThumb.getWidth() / 2f, measuredHeight / 2f - topSeekThumb.getHeight(), mBitmapPaint);
        canvas.drawBitmap(bottomSeekThumb, endX - bottomSeekThumb.getWidth() / 2f, measuredHeight / 2f, mBitmapPaint);
    }

    private void drawProgress(Canvas canvas, int measuredWidth, int measuredHeight, float startX, float endX) {

        //background trang
        //progress hong
        int top = topHeight + getPaddingTop();
        float progressX = getPosition(SEEK_PROGRESS, (moveState == STATE_PROGRESS_SEEK) ? mOldProgress : mProgress);

        canvas.drawLine(getPaddingStart(), measuredHeight / 2f, measuredWidth - getPaddingEnd(), measuredHeight / 2f, backgroundLinePaint);
        activeLinePaint.setColor(getResources().getColor(R.color.colorViolet));
        canvas.drawLine(startX, measuredHeight / 2f, endX, measuredHeight / 2f, activeLinePaint);
        if (!isHideActiveLinePaint) {
            activeLinePaint.setColor(getResources().getColor(R.color.white));
            canvas.drawLine(progressX, measuredHeight / 2f - dp4, progressX, measuredHeight / 2f + dp4, activeLinePaint);
        }
    }

    private void drawAllLabel(Canvas canvas, int measuredWidth, int measuredHeight, float startX, float endX) {
        float progressX = getPosition(SEEK_PROGRESS, mProgress);
        drawValueLabel(canvas, mMinProgress, startX, labelHeight, true);
        //drawValueLabel(canvas, mMaxProgress, endX, measuredHeight - labelHeight, false);
        if (moveState == STATE_PROGRESS_SEEK) {
            int progress = (int) getClosestPosition(progressX)[0];
            drawValueLabel(canvas, progress, progressX, measuredHeight / 2 - dp4, true);
        }
    }

    private void drawValueLabel(Canvas canvas, int second, float _cx, float _by, boolean isTop) {
        String label = getStringFromSecond(second);
        mPaint.getTextBounds(label, 0, label.length(), mBounds);
        canvas.drawText(label, _cx - mBounds.width() / 2f - mBounds.left, isTop ? _by : _by + mBounds.height(), mPaint);
    }

    private String getStringFromSecond(int second) {
        int realSecond = second % 60;
        int minute = second / 60;
        if (minute < 10) {
            return String.format(Locale.US,"%02d:%02d", minute, realSecond);
        }
        return String.format(Locale.US,"%d:%02d", minute, realSecond);
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {

        @Override
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();

            float offset = mOffset;
            offset += d.x;

            if (moveState == STATE_SEEK_START || moveState == STATE_SEEK_END ||
                    moveState == STATE_PROGRESS_SEEK) {
                mOffset = Math.min(Math.max(offset, mMinOffset), mMaxOffset);
            } else if (Math.abs(mOffset) >= topSeekThumb.getWidth()) {
                mSkipMove = true;
                mOffset = offset;
            }
            return true;
        }
    }

    private float[] getClosestPosition(float p) {
        float dis = Float.MAX_VALUE;
        int position = -1;
        for (int i = 0; i < mCount; i++) {
            float _dis = getPosition(SEEK_NONE, i) - p;
            if (Math.abs(_dis) < Math.abs(dis)) {
                dis = _dis;
                position = i;
            }
        }
        return new float[]{position, dis};
    }

    private int getPosition(int where, int progress) {
        float length = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();
        float offset = 0;
        if (where == SEEK_START && moveState == STATE_SEEK_START ||
                (where == SEEK_END && moveState == STATE_SEEK_END) ||
                (where == SEEK_PROGRESS && moveState == STATE_PROGRESS_PRESS) ||
                (where == SEEK_PROGRESS && moveState == STATE_PROGRESS_SEEK)) {
            offset = mOffset;
        }
        return (int) (getPaddingStart() + length / (mCount - 1) * progress + offset);
    }

    public static abstract class ValueLabelFormatter {

        @Nullable
        public abstract String getLabel(int input);
    }

    public static class OnValueChangedListener {

        public void onValueChanged(int progress, boolean fromUser) {

        }

        public void onStartChanged(int minProgress, boolean fromUser) {

        }

        public void onEndChanged(int maxProgress, boolean fromUser) {

        }
    }
}
