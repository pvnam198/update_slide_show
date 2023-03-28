package com.example.slide.framework.texttovideo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;

import com.example.slide.R;
import com.example.slide.ui.edit_image.framework.BeautyFloatingItem;
import com.example.slide.ui.edit_image.framework.BitmapFloatingItemIcon;
import com.example.slide.ui.edit_image.framework.FloatingItem;
import com.example.slide.ui.edit_image.framework.StickerView;
import com.example.slide.ui.edit_image.framework.TextFloatingItem;
import com.example.slide.ui.edit_image.utils.StickerUtils;
import com.example.slide.ui.edit_image.utils.SystemUtil;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TextListToVideoView extends RelativeLayout {

    private static final String TAG = "StickerView";

    private static final int DEFAULT_MIN_CLICK_DELAY_TIME = 200;

    public static final int FLIP_HORIZONTALLY = 1;

    public static final int FLIP_VERTICALLY = 1 << 1;

    private boolean showIcons;
    private boolean showBorder;
    private boolean bringToFrontCurrentSticker;
    //private boolean shapeType;

    private final ArrayList<FloatingItem> floatingItems = new ArrayList<>();

    private final List<BitmapActionIcon> icons = new ArrayList<>(4);

    private final Paint borderPaint = new Paint();
    private final Paint linePaint = new Paint();
    private final RectF stickerRect = new RectF();

    private final Matrix sizeMatrix = new Matrix();
    private final Matrix downMatrix = new Matrix();
    private final Matrix moveMatrix = new Matrix();

    // region storing variables
    private final float[] bitmapPoints = new float[8];
    private final float[] bounds = new float[8];
    private final float[] point = new float[2];
    private final PointF currentCenterPoint = new PointF();
    private final float[] tmp = new float[2];
    private PointF midPoint = new PointF();
    private boolean drawCirclePoint = false;
    private boolean onMoving = false;
    private float currentMoveingX, currentMoveingY;
    private Paint paintCircle;
    private int circleRadius;

    // endregion
    private int touchSlop;

    private BitmapActionIcon currentIcon;
    //the first point down position
    private float downX;
    private float downY;

    private float oldDistance = 0f;
    private float oldRotation = 0f;

    @StickerView.ActionMode
    private int currentMode = StickerView.ActionMode.NONE;

    private FloatingItem handlingFloatingItem;

    private FloatingItem lastHandlingFloatingItem;

    private boolean locked;
    private boolean constrained;

    private OnStickerOperationListener onStickerOperationListener;

    private long lastClickTime = 0;
    private int minClickDelayTime = DEFAULT_MIN_CLICK_DELAY_TIME;

    private int currentTime = 0;

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public VideoTextFloatingItem getVideoTextSticker(long id) {
        for (FloatingItem floatingItem : floatingItems) {
            VideoTextFloatingItem videoTextSticker = (VideoTextFloatingItem) floatingItem;
            if (id == videoTextSticker.getId()) {
                return videoTextSticker;
            }
        }
        return null;
    }

    public ArrayList<VideoTextFloatingItem> getFloatingTextItems() {
        ArrayList<VideoTextFloatingItem> videoTextStickers = new ArrayList<>();
        for (FloatingItem floatingItem : floatingItems) {
            if (floatingItem instanceof VideoTextFloatingItem)
                videoTextStickers.add((VideoTextFloatingItem) floatingItem);
        }
        return videoTextStickers;
    }

    public ArrayList<DrawableVideoFloatingItem> getDrawableVideoSticker() {
        ArrayList<DrawableVideoFloatingItem> drawableVideoStickers = new ArrayList<>();
        for (FloatingItem floatingItem : floatingItems) {
            if (floatingItem instanceof DrawableVideoFloatingItem) {
                DrawableVideoFloatingItem drawableVideoSticker = (DrawableVideoFloatingItem) floatingItem;
                drawableVideoStickers.add(drawableVideoSticker);
                Log.d(TAG, "getDrawableVideoSticker: " + drawableVideoSticker.getId());
            }
        }
        return drawableVideoStickers;
    }

    public ArrayList<FloatingItem> getFloatingItems() {
        return floatingItems;
    }

    public void removeSticker(long id) {
        for (FloatingItem floatingItem : floatingItems) {
            if (floatingItem instanceof VideoTextFloatingItem) {
                VideoTextFloatingItem videoTextSticker = (VideoTextFloatingItem) floatingItem;
                if (id == videoTextSticker.getId()) {
                    floatingItems.remove(floatingItem);
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerDeleted(floatingItem);
                    }
                    if (handlingFloatingItem == floatingItem) {
                        handlingFloatingItem = null;
                    }
                    invalidate();
                    return;
                }
            }
        }
    }

    public void removeDrawableVideoSicker(long id) {
        for (FloatingItem floatingItem : floatingItems) {
            if (floatingItem instanceof DrawableVideoFloatingItem) {
                DrawableVideoFloatingItem drawableVideoSticker = (DrawableVideoFloatingItem) floatingItem;
                if (id == drawableVideoSticker.getId()) {
                    floatingItems.remove(floatingItem);
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerDeleted(floatingItem);
                    }
                    if (handlingFloatingItem == floatingItem) {
                        handlingFloatingItem = null;
                    }
                    invalidate();
                    return;
                }
            }
        }
    }

    public TextListToVideoView(Context context) {
        super(context);
        initConfig(context, null);
    }

    public TextListToVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initConfig(context, attrs);
    }

    public TextListToVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initConfig(context, attrs);
    }

    private void initConfig(Context context, AttributeSet attrs) {
        //khoi tao list textCrop
        paintCircle = new Paint();
        paintCircle.setAntiAlias(true);
        paintCircle.setDither(true);
        paintCircle.setColor(getContext().getResources().getColor(R.color.colorAccent));
        paintCircle.setStrokeWidth(SystemUtil.dpToPx(getContext(), 2));
        paintCircle.setStyle(Paint.Style.STROKE);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.StickerView);
            showIcons = a.getBoolean(R.styleable.StickerView_showIcons, false);
            showBorder = a.getBoolean(R.styleable.StickerView_showBorder, false);
            bringToFrontCurrentSticker =
                    a.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false);

            borderPaint.setAntiAlias(true);
            borderPaint.setColor(a.getColor(R.styleable.StickerView_borderColor, Color.parseColor("#ff4d6a")));
            borderPaint.setAlpha(a.getInteger(R.styleable.StickerView_borderAlpha, 255));
            configDefaultIcons(context);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    public Matrix getSizeMatrix() {
        return sizeMatrix;
    }

    public Matrix getDownMatrix() {
        return downMatrix;
    }

    public Matrix getMoveMatrix() {
        return moveMatrix;
    }

    public void configDefaultIcons(Context context) {
        icons.clear();
        icons.add(StickIconProvider.INSTANCE.getDeleteIconIcon(context));
        icons.add(StickIconProvider.INSTANCE.getZoomIcon(context));
        icons.add(StickIconProvider.INSTANCE.getFlipIcon(context));
        icons.add(StickIconProvider.INSTANCE.getEditIcon(context));
    }

    /**
     * Swaps sticker at layer [[oldPos]] with the one at layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
    public void swapLayers(int oldPos, int newPos) {
        if (floatingItems.size() >= oldPos && floatingItems.size() >= newPos) {
            Collections.swap(floatingItems, oldPos, newPos);
            invalidate();
        }
    }

    public void setHandlingFloatingItem(VideoTextFloatingItem handlingFloatingItem) {
        this.lastHandlingFloatingItem = this.handlingFloatingItem;
        this.handlingFloatingItem = handlingFloatingItem;
        invalidate();
    }

    public void showLastHandlingSticker() {
        if (this.lastHandlingFloatingItem != null && !this.lastHandlingFloatingItem.isShow()) {
            this.lastHandlingFloatingItem.setShow(true);
            invalidate();
        }
    }

    /**
     * Sends sticker from layer [[oldPos]] to layer [[newPos]].
     * Does nothing if either of the specified layers doesn't exist.
     */
  /*  public void sendToLayer(int oldPos, int newPos) {
        if (textStickers.size() >= oldPos && textStickers.size() >= newPos) {
            VideoTextSticker s = textStickers.get(oldPos);
            textStickers.remove(oldPos);
            textStickers.add(newPos, s);
            invalidate();
        }
    }*/
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            stickerRect.left = left;
            stickerRect.top = top;
            stickerRect.right = right;
            stickerRect.bottom = bottom;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (drawCirclePoint && onMoving) {
            canvas.drawCircle(downX, downY, circleRadius, paintCircle);
            canvas.drawLine(downX, downY, currentMoveingX, currentMoveingY, paintCircle);
        }
        drawStickers(canvas);
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    protected void drawStickers(Canvas canvas) {
        for (FloatingItem floatingItem : floatingItems) {
            if (floatingItem instanceof VideoTextFloatingItem) {
                VideoTextFloatingItem videoTextSticker = (VideoTextFloatingItem) floatingItem;
                if (videoTextSticker.isShow() && (videoTextSticker.isInTime(currentTime) || videoTextSticker == handlingFloatingItem)) {
                    floatingItem.getMatrix().getValues(floatingItem.getMatrixValues());
                    floatingItem.draw(canvas);
                }
            } else if (floatingItem instanceof DrawableVideoFloatingItem) {
                DrawableVideoFloatingItem drawableVideoSticker = (DrawableVideoFloatingItem) floatingItem;
                if (drawableVideoSticker.isShow() && (drawableVideoSticker.isInTime(currentTime) || drawableVideoSticker == handlingFloatingItem)) {
                    floatingItem.getMatrix().getValues(floatingItem.getMatrixValues());
                    floatingItem.draw(canvas);
                }
            }
        }
        if (handlingFloatingItem != null && !locked && (showBorder || showIcons)) {

            getStickerPoints(handlingFloatingItem, bitmapPoints);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];

            if (showBorder) {
                canvas.drawLine(x1, y1, x2, y2, borderPaint);
                canvas.drawLine(x1, y1, x3, y3, borderPaint);
                canvas.drawLine(x2, y2, x4, y4, borderPaint);
                canvas.drawLine(x4, y4, x3, y3, borderPaint);
            }

            //draw icons
            if (showIcons) {
                float rotation = calculateRotation(x4, y4, x3, y3);
                for (int i = 0; i < icons.size(); i++) {
                    BitmapActionIcon icon = icons.get(i);
                    switch (icon.getPosition()) {
                        case BitmapFloatingItemIcon.LEFT_TOP:
                            configIconMatrix(icon, x1, y1, rotation);
                            icon.draw(canvas, borderPaint);
                            break;

                        case BitmapFloatingItemIcon.RIGHT_TOP:
                            configIconMatrix(icon, x2, y2, rotation);
                            icon.draw(canvas, borderPaint);
                            break;

                        case BitmapFloatingItemIcon.LEFT_BOTTOM:
                            configIconMatrix(icon, x3, y3, rotation);
                            icon.draw(canvas, borderPaint);
                            break;

                        case BitmapFloatingItemIcon.RIGHT_BOTOM:
                            configIconMatrix(icon, x4, y4, rotation);
                            icon.draw(canvas, borderPaint);
                            break;
                    }
                }
            }
        }

        invalidate();
    }

    protected void configIconMatrix(@NonNull BitmapActionIcon icon, float x, float y,
                                    float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();

        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2, icon.getHeight() / 2);
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (locked) return super.onInterceptTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();

                return findCurrentIconTouched() != null || findHandlingSticker() != null;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public void setDrawCirclePoint(boolean drawCirclePoint) {
        this.drawCirclePoint = drawCirclePoint;
        onMoving = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (locked) {
            return super.onTouchEvent(event);
        }

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!onTouchDown(event)) {
                    if (onStickerOperationListener == null)
                        return false;
                    onStickerOperationListener.onStickerTouchOutside();
                    invalidate();
                    if (!drawCirclePoint)
                        return false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (handlingFloatingItem != null && isInStickerArea(handlingFloatingItem, event.getX(1),
                        event.getY(1)) && findCurrentIconTouched() == null) {
                    currentMode = StickerView.ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                onTouchUp(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (currentMode == StickerView.ActionMode.ZOOM_WITH_TWO_FINGER && handlingFloatingItem != null) {
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerZoomFinished(handlingFloatingItem);
                    }
                }
                currentMode = StickerView.ActionMode.NONE;
                break;
        }

        return true;
    }

    /**
     * @param event MotionEvent received from {@link #onTouchEvent)
     * @return true if has touch something
     */
    protected boolean onTouchDown(@NonNull MotionEvent event) {
        currentMode = StickerView.ActionMode.DRAG;

        downX = event.getX();
        downY = event.getY();
        onMoving = true;
        currentMoveingX = event.getX();
        currentMoveingY = event.getY();

        midPoint = calculateMidPoint();
        oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);

        currentIcon = findCurrentIconTouched();
        if (currentIcon != null) {
            currentMode = StickerView.ActionMode.ICON;
            currentIcon.onActionDown(this, event);
        } else {
            handlingFloatingItem = findHandlingSticker();
        }

        if (handlingFloatingItem != null) {
            downMatrix.set(handlingFloatingItem.getMatrix());
            if (bringToFrontCurrentSticker) {
                floatingItems.remove(handlingFloatingItem);
                floatingItems.add(handlingFloatingItem);
            }
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerTouchedDown(handlingFloatingItem);
            }
        }

        if (drawCirclePoint) {
            onStickerOperationListener.onTouchDownForBeauty(currentMoveingX, currentMoveingY);
            invalidate();
            return true;
        }

        if (currentIcon == null && handlingFloatingItem == null) {
            return false;
        }
        invalidate();
        return true;
    }

    protected void onTouchUp(@NonNull MotionEvent event) {
        long currentTime = SystemClock.uptimeMillis();
        onMoving = false;

        if (drawCirclePoint)
            onStickerOperationListener.onTouchUpForBeauty(event.getX(), event.getY());
        if (currentMode == StickerView.ActionMode.ICON && currentIcon != null && handlingFloatingItem != null) {
            currentIcon.onActionUp(this, event);
        }

        if (currentMode == StickerView.ActionMode.DRAG
                && Math.abs(event.getX() - downX) < touchSlop
                && Math.abs(event.getY() - downY) < touchSlop
                && handlingFloatingItem != null) {
            currentMode = StickerView.ActionMode.CLICK;
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerClicked(handlingFloatingItem);
            }
            if (currentTime - lastClickTime < minClickDelayTime) {
                if (onStickerOperationListener != null) {
                    onStickerOperationListener.onStickerDoubleTapped(handlingFloatingItem);
                }
            }
        }

        if (currentMode == StickerView.ActionMode.DRAG && handlingFloatingItem != null) {
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDragFinished(handlingFloatingItem);
            }
        }

        currentMode = StickerView.ActionMode.NONE;
        lastClickTime = currentTime;
    }

    protected void handleCurrentMode(@NonNull MotionEvent event) {
        switch (currentMode) {
            case StickerView.ActionMode.NONE:
            case StickerView.ActionMode.CLICK:
                break;
            case StickerView.ActionMode.DRAG:
                currentMoveingX = event.getX();
                currentMoveingY = event.getY();
                if (drawCirclePoint) {
                    onStickerOperationListener.onTouchDragForBeauty(currentMoveingX, currentMoveingY);
                }
                if (handlingFloatingItem != null) {
                    moveMatrix.set(downMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    handlingFloatingItem.setMatrix(moveMatrix);
                    if (constrained) {
                        constrainSticker(handlingFloatingItem);
                    }
                }
                break;
            case StickerView.ActionMode.ZOOM_WITH_TWO_FINGER:
                if (handlingFloatingItem != null) {
                    float newDistance = calculateDistance(event);
                    float newRotation = calculateRotation(event);

                    moveMatrix.set(downMatrix);
                    moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                            midPoint.y);
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
                    handlingFloatingItem.setMatrix(moveMatrix);
                }

                break;

            case StickerView.ActionMode.ICON:
                if (handlingFloatingItem != null && currentIcon != null) {
                    currentIcon.onActionMove(this, event);
                }
                break;
        }
    }

    public void zoomAndRotateCurrentSticker(@NonNull MotionEvent event) {
        zoomAndRotateSticker(handlingFloatingItem, event);
    }

    public void alignHorizontally() {
        moveMatrix.set(downMatrix);
        moveMatrix.postRotate(-getCurrentSticker().getCurrentAngle(), midPoint.x, midPoint.y);
        handlingFloatingItem.setMatrix(moveMatrix);
    }

    public void zoomAndRotateSticker(@Nullable FloatingItem floatingItem, @NonNull MotionEvent event) {
        if (floatingItem != null) {
            if (floatingItem instanceof BeautyFloatingItem) {
                BeautyFloatingItem beautySticker = (BeautyFloatingItem) floatingItem;
                if (beautySticker.getType() == BeautyFloatingItem.TALL_1 || beautySticker.getType() == BeautyFloatingItem.TALL_2)
                    return;
            }
            float newDistance;
            //TODO sticker
            if (floatingItem instanceof TextFloatingItem)
                newDistance = oldDistance;
            else
                newDistance = calculateDistance(midPoint.x, midPoint.y, event.getX(), event.getY());
            float newRotation = calculateRotation(midPoint.x, midPoint.y, event.getX(), event.getY());

            moveMatrix.set(downMatrix);
            moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                    midPoint.y);
            if (!(floatingItem instanceof BeautyFloatingItem))
                moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
            handlingFloatingItem.setMatrix(moveMatrix);
        }
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

    @Nullable
    protected BitmapActionIcon findCurrentIconTouched() {
        for (BitmapActionIcon icon : icons) {
            float x = icon.getX() - downX;
            float y = icon.getY() - downY;
            float distance_pow_2 = x * x + y * y;
            if (distance_pow_2 <= Math.pow(icon.getIconRadius() + icon.getIconRadius(), 2)) {
                return icon;
            }
        }

        return null;
    }

    /**
     * find the touched Sticker
     **/
    @Nullable
    protected FloatingItem findHandlingSticker() {
        for (int i = floatingItems.size() - 1; i >= 0; i--) {
            if (isInStickerArea(floatingItems.get(i), downX, downY)) {
                return floatingItems.get(i);
            }
        }
        return null;
    }

    protected boolean isInStickerArea(@NonNull FloatingItem floatingItem, float downX, float downY) {
        tmp[0] = downX;
        tmp[1] = downY;
        return floatingItem.contains(tmp);
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
        if (handlingFloatingItem == null) {
            midPoint.set(0, 0);
            return midPoint;
        }
        handlingFloatingItem.getMappedCenterPoint(midPoint, point, tmp);
        return midPoint;
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     **/
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

    /**
     * calculate Distance in two fingers
     **/
    protected float calculateDistance(@Nullable MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) {
            return 0f;
        }
        return calculateDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    protected float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Sticker's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the sticker image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     **/
    protected void transformSticker(@Nullable FloatingItem floatingItem) {
        if (floatingItem == null) {
            Log.e(TAG, "transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null");
            return;
        }

        sizeMatrix.reset();

        float width = getWidth();
        float height = getHeight();
        float stickerWidth = floatingItem.getWidth();
        float stickerHeight = floatingItem.getHeight();
        //step 1
        float offsetX = (width - stickerWidth) / 2;
        float offsetY = (height - stickerHeight) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (width < height) {
            scaleFactor = width / stickerWidth;
        } else {
            scaleFactor = height / stickerHeight;
        }

        sizeMatrix.postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);

        floatingItem.getMatrix().reset();
        floatingItem.setMatrix(sizeMatrix);

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        for (int i = 0; i < floatingItems.size(); i++) {
            FloatingItem floatingItem = floatingItems.get(i);
            if (floatingItem != null) {
                transformSticker(floatingItem);
            }
        }
    }

    public void flipCurrentSticker(int direction) {
        flip(handlingFloatingItem, direction);
    }

    public void flip(@Nullable FloatingItem floatingItem, @StickerView.Flip int direction) {
        if (floatingItem != null) {
            floatingItem.getCenterPoint(midPoint);
            if ((direction & FLIP_HORIZONTALLY) > 0) {
                floatingItem.getMatrix().preScale(-1, 1, midPoint.x, midPoint.y);
                floatingItem.setFlippedHorizontally(!floatingItem.isFlippedHorizontally());
            }
            if ((direction & FLIP_VERTICALLY) > 0) {
                floatingItem.getMatrix().preScale(1, -1, midPoint.x, midPoint.y);
                floatingItem.setFlippedVertically(!floatingItem.isFlippedVertically());
            }

            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerFlipped(floatingItem);
            }

            invalidate();
        }
    }

    public boolean replace(@Nullable VideoTextFloatingItem sticker) {
        return replace(sticker, true);
    }

    public FloatingItem getLastHandlingFloatingItem() {
        return lastHandlingFloatingItem;
    }

    public boolean replace(@Nullable VideoTextFloatingItem sticker, boolean needStayState) {

        if (handlingFloatingItem == null)
            handlingFloatingItem = lastHandlingFloatingItem;

        if (handlingFloatingItem != null && sticker != null) {
            float width = getWidth();
            float height = getHeight();
            if (needStayState) {
                sticker.setMatrix(handlingFloatingItem.getMatrix());
                sticker.setFlippedVertically(handlingFloatingItem.isFlippedVertically());
                sticker.setFlippedHorizontally(handlingFloatingItem.isFlippedHorizontally());
            } else {
                handlingFloatingItem.getMatrix().reset();
                // reset scale, angle, and put it in center
                float offsetX = (width - handlingFloatingItem.getWidth()) / 2f;
                float offsetY = (height - handlingFloatingItem.getHeight()) / 2f;
                sticker.getMatrix().postTranslate(offsetX, offsetY);

                float scaleFactor;
                if (width < height) {
                    if (handlingFloatingItem instanceof VideoTextFloatingItem)
                        scaleFactor = width / handlingFloatingItem.getWidth();
                    else
                        scaleFactor = width / handlingFloatingItem.getDrawable().getIntrinsicWidth();
                } else {
                    if (handlingFloatingItem instanceof VideoTextFloatingItem)
                        scaleFactor = height / handlingFloatingItem.getHeight();
                    else
                        scaleFactor = height / handlingFloatingItem.getDrawable().getIntrinsicHeight();
                }
                sticker.getMatrix().postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f);
            }
            int index = floatingItems.indexOf(handlingFloatingItem);
            floatingItems.set(index, sticker);
            handlingFloatingItem = sticker;

            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(@Nullable FloatingItem floatingItem) {
        if (floatingItems.contains(floatingItem)) {
            floatingItems.remove(floatingItem);
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDeleted(floatingItem);
            }
            if (handlingFloatingItem == floatingItem) {
                handlingFloatingItem = null;
            }
            invalidate();
            return true;
        } else {
            Log.d(TAG, "remove: the sticker is not in this StickerView");
            return false;
        }
    }

    public boolean removeCurrentSticker() {
        return remove(handlingFloatingItem);
    }

    public void removeAllStickers() {
        floatingItems.clear();
        if (handlingFloatingItem != null) {
            handlingFloatingItem.release();
            handlingFloatingItem = null;
        }
        invalidate();
    }

    @NonNull
    public TextListToVideoView addSticker(@NonNull FloatingItem floatingItem) {
        return addSticker(floatingItem, FloatingItem.Position.CENTER);
    }

    public TextListToVideoView addSticker(@NonNull final FloatingItem floatingItem,
                                          final @FloatingItem.Position int position) {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(floatingItem, position);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    addStickerImmediately(floatingItem, position);
                }
            });
        }
        return this;
    }

    protected void addStickerImmediately(@NonNull FloatingItem floatingItem, @FloatingItem.Position int position) {
        setStickerPosition(floatingItem, position);

        floatingItem.getMatrix().postScale(1, 1, getWidth(), getHeight());

        handlingFloatingItem = floatingItem;
        floatingItems.add(floatingItem);
        if (onStickerOperationListener != null) {
            onStickerOperationListener.onStickerAdded(floatingItem);
        }
        invalidate();
    }

    public void getAndAddStickers(List<FloatingItem> floatingItems) {
        this.floatingItems.clear();
        this.floatingItems.addAll(floatingItems);
        invalidate();
    }

    protected void setStickerPosition(@NonNull FloatingItem floatingItem, @FloatingItem.Position int position) {
        float width = getWidth();
        float height = getHeight();
        float offsetX = width - floatingItem.getWidth();
        float offsetY = height - floatingItem.getHeight();

        if (floatingItem instanceof BeautyFloatingItem) {
            BeautyFloatingItem beautySticker = (BeautyFloatingItem) floatingItem;
            offsetY /= 2f;
            if (beautySticker.getType() == BeautyFloatingItem.BUST_0) {
                offsetX = offsetX / 3f;
            } else if (beautySticker.getType() == BeautyFloatingItem.BUST_1) {
                offsetX = offsetX * 2 / 3f;
            } else if (beautySticker.getType() == BeautyFloatingItem.HIP_1) {
                offsetX = offsetX / 2;
            } else if (beautySticker.getType() == BeautyFloatingItem.FACE) {
                offsetX = offsetX / 2;
            } else if (beautySticker.getType() == BeautyFloatingItem.TALL_1) {
                offsetX = offsetX / 2;
                offsetY = offsetY * 2f / 3f;
            } else if (beautySticker.getType() == BeautyFloatingItem.TALL_2) {
                offsetX = offsetX / 2;
                offsetY = offsetY * 3f / 2f;
            }
        } else {
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
        }

        Matrix matrix = new Matrix();
        matrix.postTranslate(offsetX, offsetY);
        matrix.getValues(floatingItem.getMatrixValues());
        floatingItem.setMatrix(matrix);
    }

    public void editTextSticker() {
        onStickerOperationListener.onStickerDoubleTapped(handlingFloatingItem);
    }


    public ArrayList<VideoTextExport> getVideoTextExports(int quality) {
        ArrayList<VideoTextExport> texts = new ArrayList<>();
        for (FloatingItem floatingItem : floatingItems) {
            if (floatingItem instanceof FloatingItemAdded) {
                FloatingItemAdded videoTextSticker = (FloatingItemAdded) floatingItem;
                texts.add(videoTextSticker.convertToExport(getContext(), quality, getWidth(), getHeight()));
            }
        }
        return texts;
    }


    @NonNull
    public float[] getStickerPoints(@Nullable FloatingItem floatingItem) {
        float[] points = new float[8];
        getStickerPoints(floatingItem, points);
        return points;
    }

    public void getStickerPoints(@Nullable FloatingItem floatingItem, @NonNull float[] dst) {
        if (floatingItem == null) {
            Arrays.fill(dst, 0);
            return;
        }
        floatingItem.getBoundPoints(bounds);
        floatingItem.getMappedPoints(dst, bounds);
    }

    public void save(@NonNull File file) {
        try {
            StickerUtils.saveImageToGallery(file, createBitmap());
            StickerUtils.notifySystemGallery(getContext(), file);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
            //
        }
    }

    @NonNull
    public Bitmap createBitmap() throws OutOfMemoryError {
        handlingFloatingItem = null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public int getStickerCount() {
        return floatingItems.size();
    }

    public boolean isNoneSticker() {
        return getStickerCount() == 0;
    }

    @NonNull
    public TextListToVideoView setLocked(boolean locked) {
        this.locked = locked;
        invalidate();
        return this;
    }

    @NonNull
    public TextListToVideoView setMinClickDelayTime(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
        return this;
    }

    public int getMinClickDelayTime() {
        return minClickDelayTime;
    }

    public boolean isConstrained() {
        return constrained;
    }

    @NonNull
    public TextListToVideoView setConstrained(boolean constrained) {
        this.constrained = constrained;
        postInvalidate();
        return this;
    }

    @NonNull
    public TextListToVideoView setOnStickerOperationListener(
            @Nullable OnStickerOperationListener onStickerOperationListener) {
        this.onStickerOperationListener = onStickerOperationListener;
        return this;
    }

    @Nullable
    public OnStickerOperationListener getOnStickerOperationListener() {
        return onStickerOperationListener;
    }

    @Nullable
    public FloatingItem getCurrentSticker() {
        return handlingFloatingItem;
    }

    @NonNull
    public List<BitmapActionIcon> getIcons() {
        return icons;
    }

    public void setIcons(@NonNull List<BitmapActionIcon> icons) {
        this.icons.clear();
        this.icons.addAll(icons);
        invalidate();
    }

    public void update() {
        invalidate();
    }

    public void updateSticker(long id, String newText, int width, int height) {
        VideoTextFloatingItem sticker = getVideoTextSticker(id);
        if (sticker != null) {
            sticker.updateConfig(newText, width, height);
            invalidate();
        }
    }

    public interface OnStickerOperationListener {
        void onStickerAdded(@NonNull FloatingItem floatingItem);

        void onStickerClicked(@NonNull FloatingItem floatingItem);

        void onStickerDeleted(@NonNull FloatingItem floatingItem);

        void onStickerTouchOutside();

        void onStickerDragFinished(@NonNull FloatingItem floatingItem);

        void onStickerTouchedDown(@NonNull FloatingItem floatingItem);

        void onStickerZoomFinished(@NonNull FloatingItem floatingItem);

        void onStickerFlipped(@NonNull FloatingItem floatingItem);

        void onStickerDoubleTapped(@NonNull FloatingItem floatingItem);

        void onTouchDownForBeauty(float x, float y);

        void onTouchDragForBeauty(float x, float y);

        void onTouchUpForBeauty(float x, float y);
    }

    @IntDef({
            StickerView.ActionMode.NONE, StickerView.ActionMode.DRAG, StickerView.ActionMode.ZOOM_WITH_TWO_FINGER, StickerView.ActionMode.ICON,
            StickerView.ActionMode.CLICK
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionMode {
        int NONE = 0;
        int DRAG = 1;
        int ZOOM_WITH_TWO_FINGER = 2;
        int ICON = 3;
        int CLICK = 4;
    }

    @IntDef(flag = true, value = {FLIP_HORIZONTALLY, FLIP_VERTICALLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flip {
    }
}
