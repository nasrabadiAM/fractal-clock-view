package com.nasrabadiam.fractal_clock_view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View

class FractalClockView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : View(context, attr) {

    var isPreview = false
    var _previewSpeed: Int? = null
    private val previewSpeed: Int
        get() = _previewSpeed ?: 6

    private var hour: Int = 0
    private var minute: Int = 0
    private var second: Int = 0
    private var milliSeconds: Int = 0
    private val clockDrawer = ClockDrawer()

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
        val speed = if (isPreview) previewSpeed else 1
        this.hour = (if (hour >= 12) hour - 12 else hour) * speed
        this.minute = minute * speed
        this.second = second * speed
        this.milliSeconds = milliSeconds * speed
        invalidate()
    }

    private fun getCenterOfViewPoint(): ClockDrawer.Point {
        return ClockDrawer.Point(width / 2f, height / 2f)
    }

    override fun onDraw(canvas: Canvas?) {
        val centerOfView = getCenterOfViewPoint()
        val maximumLegLength = minOf(centerOfView.x, centerOfView.y)

        canvas?.let {
            clockDrawer.drawFractal(
                it,
                hour,
                minute,
                second,
                milliSeconds,
                maximumLegLength,
                centerOfView
            )
        }
        super.onDraw(canvas)
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