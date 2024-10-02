package com.lyf.study.scaleimage

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.animation.doOnEnd
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import com.lyf.study.dp
import com.lyf.study.getAvatar


private val IMAGE_SIZE = 300.dp.toInt()
private const val EXTRA_SCALE_FRACTOR = 1.5f

class ScaleImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs),
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, Runnable {
    private val bitmap = getAvatar(resources, IMAGE_SIZE)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var offsetX = 0f
    private var offsetY = 0f
    private var oriOffsetX = 0f
    private var oriOffsetY = 0f
    private var smallScale = 0f
    private var bigScale = 0f
    private val gestureDetector = GestureDetectorCompat(context, this)
    private var big = false
    private var scaleFraction = 0f
        set(value) {
            field = value
            invalidate()
        }
    private val scaleAnimator: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this, "scaleFraction", 0f, 1f).apply {
            doOnEnd {
                if (!big) {
                    offsetX = 0f
                    offsetY = 0f
                }
            }
        }
    }
    private val scroller = OverScroller(context)


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        oriOffsetX = (width - IMAGE_SIZE) / 2f
        oriOffsetY = (height - IMAGE_SIZE) / 2f
        if ((bitmap.width / bitmap.height.toFloat()) > width / height.toFloat()) {
            smallScale = width / bitmap.width.toFloat()
            bigScale = height / bitmap.height.toFloat() * EXTRA_SCALE_FRACTOR
        } else {
            smallScale = height / bitmap.height.toFloat()
            bigScale = width / bitmap.width.toFloat() * EXTRA_SCALE_FRACTOR
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(offsetX * scaleFraction, offsetY * scaleFraction)
        val scale = smallScale + (bigScale - smallScale) * scaleFraction
        canvas.scale(scale, scale, width / 2f, height / 2f)
        canvas.drawBitmap(bitmap, oriOffsetX, oriOffsetY, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(
        downEvent: MotionEvent?,
        currentEvent: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (big) {
            scroller.fling(
                offsetX.toInt(),
                offsetY.toInt(),
                velocityX.toInt(),
                velocityY.toInt(),
                (-(bitmap.width * bigScale - width) / 2).toInt(),
                ((bitmap.width * bigScale - width) / 2).toInt(),
                (-(bitmap.height * bigScale - height) / 2).toInt(),
                ((bitmap.height * bigScale - height) / 2).toInt()
            )

            ViewCompat.postOnAnimation(this, this)
        }
        return false
    }


    override fun run() {
        if (scroller.computeScrollOffset()) {
            offsetX = scroller.currX.toFloat()
            offsetY = scroller.currY.toFloat()
            invalidate()
        }
        ViewCompat.postOnAnimation(this, this)
    }

    override fun onScroll(
        downEvent: MotionEvent?,
        currentEvent: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (big) {
            offsetX -= distanceX
            offsetY -= distanceY
            fixOffsetXAndY()
            invalidate()
        }
        return false
    }

    private fun fixOffsetXAndY() {
        if (offsetX > ((bigScale * IMAGE_SIZE - width) / 2)) {
            offsetX = (bigScale * IMAGE_SIZE - width) / 2
        }

        if (offsetX < -(bigScale * IMAGE_SIZE - width) / 2) {
            offsetX = -(bigScale * IMAGE_SIZE - width) / 2
        }


        if (offsetY > (bigScale * IMAGE_SIZE - height) / 2) {
            offsetY = (bigScale * IMAGE_SIZE - height) / 2
        }

        if (offsetY < -(bigScale * IMAGE_SIZE - height) / 2) {
            offsetY = -(bigScale * IMAGE_SIZE - height) / 2
        }


        Log.i("lyftag", "111:" + EXTRA_SCALE_FRACTOR * bigScale * IMAGE_SIZE)
        Log.i("lyftag", "width:" + width)
        Log.i("lyftag", "offsetX:${offsetX}")

    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        big = !big
        if (big) {
            scaleAnimator.start()
        } else {
            scaleAnimator.reverse()
        }
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }


}