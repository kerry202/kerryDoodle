package com.locojoy.mydooble.dood

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.locojoy.mydooble.BaseApp
import com.locojoy.mydooble.R

/**
 * @author: kerry
 * date: On $ {DATE}
 */
class DiyBallView : View {

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    var paint: Paint? = null
    var size = 32f
    private val point = PointF(size, size)

    fun init() {
        paint = Paint()
        paint?.isAntiAlias = true
        paint?.strokeWidth = 2f
    }

    fun setPoint(x: Float, y: Float, size: Float) {
        point.x = x
        point.y = y
        this.size = size
        postInvalidate()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint?.color = BaseApp.mContent.resources.getColor(R.color.color_E9EEFF)
        paint?.style = Paint.Style.FILL
        canvas.drawCircle(point.x, point.y, size, paint!!)
        paint?.color = BaseApp.mContent.resources.getColor(R.color.color_C1CFFF)
        paint?.style = Paint.Style.STROKE
        canvas.drawCircle(point.x, point.y, size, paint!!)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}