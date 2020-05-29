package com.nasrabadiam.fractal_clock_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View

class FractalClockView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : View(context, attr) {

    private var hour: Int = 0
    private var minute: Int = 0
    private var second: Int = 0
    private var milliSeconds: Int = 0
    private val clockViewHelper = ClockViewHelper()

    private val paintOfHour = Paint().apply {
        color = Color.RED
        strokeWidth = 8f
        isAntiAlias = true
    }
    private val paintOfMinute = Paint().apply {
        color = Color.RED
        strokeWidth = 4f
        isAntiAlias = true
    }
    private val paintOfSecond = Paint().apply {
        color = Color.RED
        strokeWidth = 2f
        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getMode(widthMeasureSpec)
        )
        val height = measureDimension(
            MeasureSpec.getSize(heightMeasureSpec),
            MeasureSpec.getMode(heightMeasureSpec)
        )
        super.onMeasure(width, height)
    }

    fun updateTime(hour: Int, minute: Int, second: Int, milliSeconds: Int) {
        this.hour = if (hour >= 12) {
            hour - 12
        } else {
            hour
        }
        this.minute = minute
        this.second = second
        this.milliSeconds = milliSeconds
        invalidate()
    }

    private fun getCenterOfViewPoint(): Point {
        return Point(width / 2f, height / 2f)
    }

    override fun onDraw(canvas: Canvas?) {
        val centerOfView = getCenterOfViewPoint()
        val lines = clockViewHelper.getLegLinesToDraw(hour, minute, second, milliSeconds, centerOfView)
        lines.forEach {
            val paint = getPaint(it)
            canvas?.drawLine(
                it.startPoint.x,
                it.startPoint.y,
                it.endPoint.x,
                it.endPoint.y,
                paint
            )
        }
        super.onDraw(canvas)
    }

    private fun getPaint(it: Line) = when (it.legType) {
        LegType.HOUR -> paintOfHour
        LegType.MINUTE -> paintOfMinute
        LegType.SECOND -> paintOfSecond
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = minOf(result, specSize)
            }
        }
        if (result < desiredSize) {
            Log.e(TAG, "The view is too small, the content might get cut")
        }
        return result
    }

    companion object {
        const val TAG = "FractalClockView"
    }
}