package com.example.slide.ui.splash

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.slide.R
import kotlin.math.abs

class SplashAnimationView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {

        const val SCALE_DISTANCE = 0.4f

        const val ROTATE_DISTANCE = -90f
    }

    private var image1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_splash1)

    private var image2: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_splash2)

    private var image3: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_splash3)

    val paint = Paint()

    private val camera = Camera()

    private var translateX = 0f

    private var halfWidth = 1f

    private var realHalfWidth = 1f

    private var visualWidth = 1f

    private var halfImageWidth = 1f

    private var halfImageHeight = 1f

    private var distance = 0f

    private val myMatrix = Matrix()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        translateX += 2f
        if (translateX > measuredWidth)
            translateX = 0f
        val x1 = translateX - measuredWidth.toFloat()
        if (x1 > -distance) {
            val factor = (realHalfWidth - x1) / visualWidth
            camera.save()
            camera.rotateY(factor * ROTATE_DISTANCE)
            camera.getMatrix(myMatrix)
            camera.restore()
            val scale = 1f - abs(factor) * SCALE_DISTANCE
            myMatrix.preTranslate(-halfImageWidth, -halfImageHeight)
            myMatrix.postScale(scale, scale)
            myMatrix.postTranslate(x1 + halfImageWidth, paddingTop.toFloat() + halfImageHeight)
            canvas.drawBitmap(image1, myMatrix, paint)
        }

        val x2 = translateX - distance * 2
        if (x2 > -distance) {
            val factor = (realHalfWidth - x2) / visualWidth
            val scale = 1f - abs(factor) * SCALE_DISTANCE
            camera.save()
            camera.rotateY(factor * ROTATE_DISTANCE)
            camera.getMatrix(myMatrix)
            camera.restore()
            myMatrix.postScale(scale, scale)
            myMatrix.preTranslate(-halfImageWidth, -halfImageHeight)
            myMatrix.postTranslate(x2 + halfImageWidth, paddingTop.toFloat() + halfImageHeight)
            canvas.drawBitmap(image2, myMatrix, paint)
        }

        var factor = (realHalfWidth - translateX + distance) / visualWidth
        camera.save()
        camera.rotateY(factor * ROTATE_DISTANCE)
        camera.getMatrix(myMatrix)
        camera.restore()
        myMatrix.preTranslate(-halfImageWidth, -halfImageHeight)
        var scale = 1f - abs(factor) * SCALE_DISTANCE
        myMatrix.postScale(scale, scale)
        myMatrix.postTranslate(
            translateX - distance + halfImageWidth,
            paddingTop.toFloat() + halfImageHeight
        )
        canvas.drawBitmap(image3, myMatrix, paint)

        factor = (realHalfWidth - translateX) / visualWidth
        camera.save()
        camera.rotateY(factor * ROTATE_DISTANCE)
        camera.getMatrix(myMatrix)
        camera.restore()
        scale = 1f - abs(factor) * SCALE_DISTANCE
        myMatrix.preScale(1f, 1f)
        myMatrix.preTranslate(-halfImageWidth, -halfImageHeight)
        myMatrix.postScale(scale, scale)
        myMatrix.postTranslate(translateX + halfImageWidth, paddingTop.toFloat() + halfImageHeight)
        canvas.drawBitmap(image1, myMatrix, paint)

        if (translateX + distance < measuredWidth) {
            factor = (realHalfWidth - translateX - distance) / visualWidth
            scale = 1f - abs(factor) * SCALE_DISTANCE
            camera.save()
            camera.rotateY(factor * ROTATE_DISTANCE)
            camera.getMatrix(myMatrix)
            camera.restore()
            myMatrix.preTranslate(-halfImageWidth, -halfImageHeight)
            myMatrix.postScale(scale, scale)
            myMatrix.postTranslate(
                translateX + distance + halfImageWidth,
                paddingTop.toFloat() + halfImageHeight
            )
            canvas.drawBitmap(image2, myMatrix, paint)
        }

        if (translateX + distance * 2 < measuredWidth) {
            factor = (realHalfWidth - translateX - distance * 2) / visualWidth
            scale = 1f - abs(factor) * SCALE_DISTANCE
            camera.save()
            camera.rotateY(factor * ROTATE_DISTANCE)
            camera.getMatrix(myMatrix)
            camera.restore()
            myMatrix.preTranslate(-halfImageWidth, -halfImageHeight)
            myMatrix.postScale(scale, scale)
            myMatrix.postTranslate(
                translateX + distance * 2 + halfImageWidth,
                paddingTop.toFloat() + halfImageHeight
            )
            canvas.drawBitmap(image3, myMatrix, paint)
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d("kimkakasplash","mswidth:"+ measuredWidth)
        distance = measuredWidth / 3f
        halfWidth = measuredWidth / 2f

        halfImageWidth = image1.width / 2f
        halfImageHeight = image1.height / 2f

        realHalfWidth = halfWidth - halfImageWidth
        visualWidth = realHalfWidth + halfWidth
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }
}