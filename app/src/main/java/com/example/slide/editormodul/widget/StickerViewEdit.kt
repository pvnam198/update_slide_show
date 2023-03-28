package com.example.slide.editormodul.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.MotionEventCompat
import com.example.slide.R
import com.example.slide.model.StickerPropertyModel
import com.example.slide.videolib.Util
import kotlin.math.hypot
import kotlin.math.sqrt

class StickerViewEdit : AppCompatImageView {
    private lateinit var deleteBitmap: Bitmap
    private lateinit var flipVBitmap: Bitmap
    private lateinit var topBitmap: Bitmap
    private lateinit var resizeBitmap: Bitmap
    private var mBitmap: Bitmap? = null
    private var dst_delete: Rect? = null
    private var dst_resize: Rect? = null
    private var dst_flipV: Rect? = null
    private var dst_top: Rect? = null
    private var deleteBitmapWidth = 0
    private var deleteBitmapHeight = 0
    private var resizeBitmapWidth = 0
    private var resizeBitmapHeight = 0

    //水平镜像
    private var flipVBitmapWidth = 0
    private var flipVBitmapHeight = 0

    //置顶
    private var topBitmapWidth = 0
    private var topBitmapHeight = 0
    private var localPaint: Paint? = null
    private var mScreenwidth = 0
    private var mScreenHeight = 0
    private val mid = PointF()
    private var operationListener: OperationListener? = null
    private var lastRotateDegree = 0f

    //是否是第二根手指放下
    private var isPointerDown = false

    //手指移动距离必须超过这个数值
    private val pointerLimitDis = 20f
    private val pointerZoomCoeff = 0.09f

    /**
     * 对角线的长度
     */
    private var lastLength = 0f
    private var isInResize = false
    private val mMatrix = Matrix()

    /**
     * 是否在四条线内部
     */
    private var isInSide = false
    private var lastX = 0f
    private var lastY = 0f

    /**
     * 是否在编辑模式
     */
    private var isInEdit = true
    private var MIN_SCALE = 0.5f
    private var MAX_SCALE = 1.2f
    private var halfDiagonalLength = 0.0
    private var oringinWidth = 0f

    //双指缩放时的初始距离
    private var oldDis = 0f
    private val stickerId: Long
    private lateinit var dm: DisplayMetrics
    private var mContext: Context? = null
    private var checkPhotoMain = true

    //水平镜像
    private var isHorizonMirror = false

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        mContext = context
        stickerId = 0
        init()
    }

    constructor(context: Context) : super(context) {
        stickerId = 0
        init()
    }

    constructor(
        context: Context,
        checkPhotoMainN: Boolean
    ) : super(context) {
        checkPhotoMain = checkPhotoMainN
        stickerId = 0
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        stickerId = 0
        init()
    }

    private fun init() {
        dst_delete = Rect()
        dst_resize = Rect()
        dst_flipV = Rect()
        dst_top = Rect()
        localPaint = Paint()
        localPaint!!.color = Color.parseColor("#1e87e4")
        localPaint!!.isAntiAlias = true
        localPaint!!.isDither = true
        localPaint!!.style = Paint.Style.STROKE
        localPaint!!.strokeWidth = 2.0f
        dm = resources.displayMetrics
        mScreenwidth = dm.widthPixels
        mScreenHeight = dm.heightPixels
    }

    override fun onDraw(canvas: Canvas) {
        if (mBitmap != null) {
            val arrayOfFloat = FloatArray(9)
            mMatrix.getValues(arrayOfFloat)
            val f1 =
                0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
            val f2 =
                0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
            val f3 =
                arrayOfFloat[0] * mBitmap!!.width + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
            val f4 =
                arrayOfFloat[3] * mBitmap!!.width + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
            val f5 =
                0.0f * arrayOfFloat[0] + arrayOfFloat[1] * mBitmap!!.height + arrayOfFloat[2]
            val f6 =
                0.0f * arrayOfFloat[3] + arrayOfFloat[4] * mBitmap!!.height + arrayOfFloat[5]
            val f7 =
                arrayOfFloat[0] * mBitmap!!.width + arrayOfFloat[1] * mBitmap!!.height + arrayOfFloat[2]
            val f8 =
                arrayOfFloat[3] * mBitmap!!.width + arrayOfFloat[4] * mBitmap!!.height + arrayOfFloat[5]
            canvas.save()
            canvas.drawBitmap(mBitmap!!, mMatrix, null)
            //删除在右上角
            dst_top!!.left = (f3 - deleteBitmapWidth / 2).toInt()
            dst_top!!.right = (f3 + deleteBitmapWidth / 2).toInt()
            dst_top!!.top = (f4 - deleteBitmapHeight / 2).toInt()
            dst_top!!.bottom = (f4 + deleteBitmapHeight / 2).toInt()
            //拉伸等操作在右下角
            dst_resize!!.left = (f7 - resizeBitmapWidth / 2).toInt()
            dst_resize!!.right = (f7 + resizeBitmapWidth / 2).toInt()
            dst_resize!!.top = (f8 - resizeBitmapHeight / 2).toInt()
            dst_resize!!.bottom = (f8 + resizeBitmapHeight / 2).toInt()
            //垂直镜像在左上角
            dst_delete!!.left = (f1 - flipVBitmapWidth / 2).toInt()
            dst_delete!!.right = (f1 + flipVBitmapWidth / 2).toInt()
            dst_delete!!.top = (f2 - flipVBitmapHeight / 2).toInt()
            dst_delete!!.bottom = (f2 + flipVBitmapHeight / 2).toInt()
            //水平镜像在左下角
            dst_flipV!!.left = (f5 - topBitmapWidth / 2).toInt()
            dst_flipV!!.right = (f5 + topBitmapWidth / 2).toInt()
            dst_flipV!!.top = (f6 - topBitmapHeight / 2).toInt()
            dst_flipV!!.bottom = (f6 + topBitmapHeight / 2).toInt()
            if (isInEdit) {
                canvas.drawLine(f1, f2, f3, f4, localPaint!!)
                canvas.drawLine(f3, f4, f7, f8, localPaint!!)
                canvas.drawLine(f5, f6, f7, f8, localPaint!!)
                canvas.drawLine(f5, f6, f1, f2, localPaint!!)
                canvas.drawBitmap(resizeBitmap, null, dst_resize!!, null)
                canvas.drawBitmap(deleteBitmap, null, dst_delete!!, null)
                canvas.drawBitmap(flipVBitmap, null, dst_flipV!!, null)
                canvas.drawBitmap(topBitmap, null, dst_top!!, null)
            }
            canvas.restore()
        }
    }

    override fun setImageResource(resId: Int) {
        setBitmap(BitmapFactory.decodeResource(resources, resId), 0)
    }

    fun setBitmap(bitmap: Bitmap?, y: Int) {
        mMatrix.reset()
        mBitmap = bitmap
        setDiagonalLength()
        initBitmaps()
        val w = mBitmap!!.width
        val h = mBitmap!!.height
        oringinWidth = w.toFloat()
        if (!checkPhotoMain) {
            val xrandom: Int = Util.getRandomIndex(0, Math.abs(mScreenwidth - w))
            val yrandom: Int =
                Util.getRandomIndex(0, Math.abs(mScreenwidth - mScreenwidth / 2))
            mMatrix.postTranslate(xrandom.toFloat(), yrandom.toFloat())
        } else {
            val initScale = (MIN_SCALE + MAX_SCALE) / 6
            mMatrix.postScale(initScale, initScale, w / 2.toFloat(), h / 2.toFloat())
            //Y坐标为 （顶部操作栏+正方形图）/2
            val xrandom: Int = Util.getRandomIndex(w, Math.abs(mScreenwidth - w))
            val yrandom: Int =
                Util.getRandomIndex(w, Math.abs(mScreenwidth - mScreenwidth / 2))
            mMatrix.postTranslate(xrandom.toFloat(), yrandom.toFloat())
        }

//        matrix.postTranslate(mScreenwidth / 2 - w / 2, (mScreenwidth) / 2 - h / 2);
        invalidate()
    }

    private fun setDiagonalLength() {
        halfDiagonalLength =
            hypot(mBitmap!!.width.toDouble(), mBitmap!!.height.toDouble()) / 2
    }

    private fun initBitmaps() {
        //当图片的宽比高大时 按照宽计算 缩放大小根据图片的大小而改变 最小为图片的1/8 最大为屏幕宽
        if (mBitmap!!.width >= mBitmap!!.height) {
            val minWidth = mScreenwidth / 6.toFloat()
            MIN_SCALE = if (mBitmap!!.width < minWidth) {
                1f
            } else {
                1.0f * minWidth / mBitmap!!.width
            }
            MAX_SCALE = if (mBitmap!!.width > mScreenwidth) {
                1f
            } else {
                1.0f * mScreenwidth / mBitmap!!.width
            }
        } else {
            //当图片高比宽大时，按照图片的高计算
            val minHeight = mScreenwidth / 6.toFloat()
            MIN_SCALE = if (mBitmap!!.height < minHeight) {
                1f
            } else {
                1.0f * minHeight / mBitmap!!.height
            }
            MAX_SCALE = if (mBitmap!!.height > mScreenwidth) {
                1f
            } else {
                1.0f * mScreenwidth / mBitmap!!.height
            }
        }
        topBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_edit_duplicated)
        deleteBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_edit_cancel)
        flipVBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_edit_reflect)
        resizeBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_edit_zoom)
        deleteBitmapWidth =
            (deleteBitmap.getWidth() * BITMAP_SCALE).toInt()
        deleteBitmapHeight =
            (deleteBitmap.getHeight() * BITMAP_SCALE).toInt()
        resizeBitmapWidth =
            (resizeBitmap.getWidth() * BITMAP_SCALE).toInt()
        resizeBitmapHeight =
            (resizeBitmap.getHeight() * BITMAP_SCALE).toInt()
        flipVBitmapWidth = (flipVBitmap.getWidth() * BITMAP_SCALE).toInt()
        flipVBitmapHeight =
            (flipVBitmap.getHeight() * BITMAP_SCALE).toInt()
        topBitmapWidth = (topBitmap.getWidth() * BITMAP_SCALE).toInt()
        topBitmapHeight = (topBitmap.getHeight() * BITMAP_SCALE).toInt()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(event)
        var handled = true
        when (action) {
            MotionEvent.ACTION_DOWN -> if (isInButton(event, dst_delete)) {
                if (operationListener != null) {
                    operationListener!!.onDeleteClick()
                }
            } else if (isInResize(event)) {
                isInResize = true
                lastRotateDegree = rotationToStartPoint(event)
                midPointToStartPoint(event)
                lastLength = diagonalLength(event)
            } else if (isInButton(event, dst_flipV)) {
                //水平镜像
                flipV()
            } else if (isInButton(event, dst_top)) {
                //置顶
                bringToFront()
                if (operationListener != null) {
                    operationListener!!.onTop(this)
                }
            } else if (isInBitmap(event)) {
                isInSide = true
                lastX = event.getX(0)
                lastY = event.getY(0)
            } else {
                handled = false
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (spacing(event) > pointerLimitDis) {
                    oldDis = spacing(event)
                    isPointerDown = true
                    midPointToStartPoint(event)
                } else {
                    isPointerDown = false
                }
                isInSide = false
                isInResize = false
            }
            MotionEvent.ACTION_MOVE ->                 //双指缩放
                if (isPointerDown) {
                    var scale: Float
                    val disNew = spacing(event)
                    if (disNew == 0f || disNew < pointerLimitDis) {
                        scale = 1f
                    } else {
                        scale = disNew / oldDis
                        //缩放缓慢
                        scale = (scale - 1) * pointerZoomCoeff + 1
                    }
                    val scaleTemp =
                        scale * Math.abs(dst_flipV!!.left - dst_resize!!.left) / oringinWidth
                    if (scaleTemp <= MIN_SCALE && scale < 1 ||
                        scaleTemp >= MAX_SCALE && scale > 1
                    ) {
                        scale = 1f
                    } else {
                        lastLength = diagonalLength(event)
                    }
                    mMatrix.postScale(scale, scale, mid.x, mid.y)
                    invalidate()
                } else if (isInResize) {
                    mMatrix.postRotate(
                        (rotationToStartPoint(event) - lastRotateDegree) * 2,
                        mid.x,
                        mid.y
                    )
                    lastRotateDegree = rotationToStartPoint(event)
                    var scale = diagonalLength(event) / lastLength
                    if (diagonalLength(event) / halfDiagonalLength <= MIN_SCALE && scale < 1 ||
                        diagonalLength(event) / halfDiagonalLength >= MAX_SCALE && scale > 1
                    ) {
                        scale = 1f
                        if (!isInResize(event)) {
                            isInResize = false
                        }
                    } else {
                        lastLength = diagonalLength(event)
                    }
                    mMatrix.postScale(scale, scale, mid.x, mid.y)
                    invalidate()
                } else if (isInSide) {
                    val x = event.getX(0)
                    val y = event.getY(0)

                    //TODO 移动区域判断 不能超出屏幕
                    mMatrix.postTranslate(x - lastX, y - lastY)
                    lastX = x
                    lastY = y
                    invalidate()
                }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isInResize = false
                isInSide = false
                isPointerDown = false
            }
        }
        if (handled && operationListener != null) {
            operationListener!!.onEdit(this)
        }
        return handled
    }

    fun flipV() {
        val localPointF = PointF()
        midDiagonalPoint(localPointF)
        mMatrix.postScale(-1.0f, 1.0f, localPointF.x, localPointF.y)
        isHorizonMirror = !isHorizonMirror
        invalidate()
    }

    fun calculate(model: StickerPropertyModel): StickerPropertyModel {
        val v = FloatArray(9)
        mMatrix.getValues(v)
        // translation is simple
        val tx = v[Matrix.MTRANS_X]
        val ty = v[Matrix.MTRANS_Y]
        Log.d(TAG, "tx : $tx ty : $ty")
        // calculate real scale
        val scalex = v[Matrix.MSCALE_X]
        val skewy = v[Matrix.MSKEW_Y]
        val rScale =
            Math.sqrt(scalex * scalex + skewy * skewy.toDouble()).toFloat()
        Log.d(TAG, "rScale : $rScale")
        // calculate the degree of rotation
        val rAngle = Math.round(
            Math.atan2(
                v[Matrix.MSKEW_X].toDouble(),
                v[Matrix.MSCALE_X].toDouble()
            ) * (180 / Math.PI)
        ).toFloat()
        Log.d(TAG, "rAngle : $rAngle")
        val localPointF = PointF()
        midDiagonalPoint(localPointF)
        Log.d(
            TAG,
            " width  : " + mBitmap!!.width * rScale + " height " + mBitmap!!.height * rScale
        )
        val minX = localPointF.x
        val minY = localPointF.y
        Log.d(TAG, "midX : $minX midY : $minY")
        model.setDegree(Math.toRadians(rAngle.toDouble()).toFloat())
        //TODO 占屏幕百分比
        val precentWidth = mBitmap!!.width * rScale / mScreenwidth
        model.setScaling(precentWidth)
        model.setxLocation(minX / mScreenwidth)
        model.setyLocation(minY / mScreenwidth)
        model.setStickerId(stickerId)
        if (isHorizonMirror) {
            model.setHorizonMirror(1)
        } else {
            model.setHorizonMirror(2)
        }
        return model
    }

    private fun isInBitmap(event: MotionEvent): Boolean {
        val arrayOfFloat1 = FloatArray(9)
        mMatrix.getValues(arrayOfFloat1)
        //左上角
        val f1 =
            0.0f * arrayOfFloat1[0] + 0.0f * arrayOfFloat1[1] + arrayOfFloat1[2]
        val f2 =
            0.0f * arrayOfFloat1[3] + 0.0f * arrayOfFloat1[4] + arrayOfFloat1[5]
        //右上角
        val f3 =
            arrayOfFloat1[0] * mBitmap!!.width + 0.0f * arrayOfFloat1[1] + arrayOfFloat1[2]
        val f4 =
            arrayOfFloat1[3] * mBitmap!!.width + 0.0f * arrayOfFloat1[4] + arrayOfFloat1[5]
        //左下角
        val f5 =
            0.0f * arrayOfFloat1[0] + arrayOfFloat1[1] * mBitmap!!.height + arrayOfFloat1[2]
        val f6 =
            0.0f * arrayOfFloat1[3] + arrayOfFloat1[4] * mBitmap!!.height + arrayOfFloat1[5]
        //右下角
        val f7 =
            arrayOfFloat1[0] * mBitmap!!.width + arrayOfFloat1[1] * mBitmap!!.height + arrayOfFloat1[2]
        val f8 =
            arrayOfFloat1[3] * mBitmap!!.width + arrayOfFloat1[4] * mBitmap!!.height + arrayOfFloat1[5]
        val arrayOfFloat2 = FloatArray(4)
        val arrayOfFloat3 = FloatArray(4)
        //确定X方向的范围
        arrayOfFloat2[0] = f1 //左上的x
        arrayOfFloat2[1] = f3 //右上的x
        arrayOfFloat2[2] = f7 //右下的x
        arrayOfFloat2[3] = f5 //左下的x
        //确定Y方向的范围
        arrayOfFloat3[0] = f2 //左上的y
        arrayOfFloat3[1] = f4 //右上的y
        arrayOfFloat3[2] = f8 //右下的y
        arrayOfFloat3[3] = f6 //左下的y
        return pointInRect(arrayOfFloat2, arrayOfFloat3, event.getX(0), event.getY(0))
    }

    private fun pointInRect(
        xRange: FloatArray,
        yRange: FloatArray,
        x: Float,
        y: Float
    ): Boolean {
        //四条边的长度
        val a1 = Math.hypot(
            xRange[0] - xRange[1].toDouble(),
            yRange[0] - yRange[1].toDouble()
        )
        val a2 = Math.hypot(
            xRange[1] - xRange[2].toDouble(),
            yRange[1] - yRange[2].toDouble()
        )
        val a3 = Math.hypot(
            xRange[3] - xRange[2].toDouble(),
            yRange[3] - yRange[2].toDouble()
        )
        val a4 = Math.hypot(
            xRange[0] - xRange[3].toDouble(),
            yRange[0] - yRange[3].toDouble()
        )
        //待检测点到四个点的距离
        val b1 =
            Math.hypot(x - xRange[0].toDouble(), y - yRange[0].toDouble())
        val b2 =
            Math.hypot(x - xRange[1].toDouble(), y - yRange[1].toDouble())
        val b3 =
            Math.hypot(x - xRange[2].toDouble(), y - yRange[2].toDouble())
        val b4 =
            Math.hypot(x - xRange[3].toDouble(), y - yRange[3].toDouble())
        val u1 = (a1 + b1 + b2) / 2
        val u2 = (a2 + b2 + b3) / 2
        val u3 = (a3 + b3 + b4) / 2
        val u4 = (a4 + b4 + b1) / 2

        //矩形的面积
        val s = a1 * a2
        //海伦公式 计算4个三角形面积
        val ss = (Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
                + Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
                + Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
                + Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1)))
        return Math.abs(s - ss) < 0.5
    }

    private fun isInButton(event: MotionEvent, rect: Rect?): Boolean {
        val left = rect!!.left
        val right = rect.right
        val top = rect.top
        val bottom = rect.bottom
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(
            0
        ) <= bottom
    }

    private fun isInResize(event: MotionEvent): Boolean {
        val left = -20 + dst_resize!!.left
        val top = -20 + dst_resize!!.top
        val right = 20 + dst_resize!!.right
        val bottom = 20 + dst_resize!!.bottom
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(
            0
        ) <= bottom
    }

    private fun midPointToStartPoint(event: MotionEvent) {
        val arrayOfFloat = FloatArray(9)
        mMatrix.getValues(arrayOfFloat)
        val f1 =
            0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
        val f2 =
            0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
        val f3 = f1 + event.getX(0)
        val f4 = f2 + event.getY(0)
        mid[f3 / 2] = f4 / 2
    }

    private fun midDiagonalPoint(paramPointF: PointF) {
        val arrayOfFloat = FloatArray(9)
        mMatrix.getValues(arrayOfFloat)
        val f1 =
            0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
        val f2 =
            0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
        val f3 =
            arrayOfFloat[0] * mBitmap!!.width + arrayOfFloat[1] * mBitmap!!.height + arrayOfFloat[2]
        val f4 =
            arrayOfFloat[3] * mBitmap!!.width + arrayOfFloat[4] * mBitmap!!.height + arrayOfFloat[5]
        val f5 = f1 + f3
        val f6 = f2 + f4
        paramPointF[f5 / 2.0f] = f6 / 2.0f
    }

    private fun rotationToStartPoint(event: MotionEvent): Float {
        val arrayOfFloat = FloatArray(9)
        mMatrix.getValues(arrayOfFloat)
        val x =
            0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2]
        val y =
            0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5]
        val arc =
            Math.atan2(event.getY(0) - y.toDouble(), event.getX(0) - x.toDouble())
        return Math.toDegrees(arc).toFloat()
    }

    private fun diagonalLength(event: MotionEvent): Float {
        return Math.hypot(
            event.getX(0) - mid.x.toDouble(),
            event.getY(0) - mid.y.toDouble()
        ).toFloat()
    }

    private fun spacing(event: MotionEvent): Float {
        return if (event.pointerCount == 2) {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            sqrt(x * x + y * y.toDouble()).toFloat()
        } else {
            0F
        }
    }

    interface OperationListener {
        fun onDeleteClick()
        fun onEdit(stickerViewEdit: StickerViewEdit?)
        fun onTop(stickerViewEdit: StickerViewEdit?)
    }

    fun setOperationListener(operationListener: OperationListener?) {
        this.operationListener = operationListener
    }

    fun setInEdit(isInEdit: Boolean) {
        this.isInEdit = isInEdit
        invalidate()
    }

    companion object {
        private const val TAG = "StickerView"
        private const val BITMAP_SCALE = 0.7f
    }
}