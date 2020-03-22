package com.nasrabadiam.fractal_clock_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class FractalClockView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : View(context, attr) {

    private var hour: Int = 0
    private var minute: Int = 0
    private var second: Int = 0
    private var milliSeconds: Int = 0

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
        return Point((width / 2f).toInt(), (height / 2f).toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        val centerOfView = getCenterOfViewPoint()
        val hourEndPoint = getHourEndPoint(hour, minute, second)
        val minuteEndPoint = getMinuteEndPoint(minute, second)
        val secondEndPoint = getSecondEndPoint(second, milliSeconds)

        canvas?.apply {
            drawLine(
                centerOfView.x.toFloat(),
                centerOfView.y.toFloat(),
                hourEndPoint.x.toFloat(),
                hourEndPoint.y.toFloat(),
                paintOfHour
            )
            drawLine(
                centerOfView.x.toFloat(),
                centerOfView.y.toFloat(),
                minuteEndPoint.x.toFloat(),
                minuteEndPoint.y.toFloat(),
                paintOfMinute
            )
            drawLine(
                centerOfView.x.toFloat(),
                centerOfView.y.toFloat(),
                secondEndPoint.x.toFloat(),
                secondEndPoint.y.toFloat(),
                paintOfSecond
            )
        }
        super.onDraw(canvas)
    }

    private fun getHourEndPoint(hour: Int, minute: Int, second: Int): Point {
        val halfOfView = minOf(height, width) / 2
        val hourLegLength = halfOfView * .4
        val hourDeg = hour * ONE_HOUR_DEGREE_IN_CIRCLE + minute * ONE_MIN_DEGREE_OF_HOUR_IN_CIRCLE + second * ONE_SEC_DEGREE_OF_HOUR_IN_CIRCLE
        return getEndPoint(hourDeg, hourLegLength)
    }

    private fun getMinuteEndPoint(minute: Int, second: Int): Point {
        val halfOfView = minOf(height, width) / 2
        val minuteLegLength = halfOfView * .5
        val minuteDeg = minute * ONE_MIN_DEGREE_IN_CIRCLE + second * ONE_SEC_DEGREE_OF_MINUTE_IN_CIRCLE
        return getEndPoint(minuteDeg, minuteLegLength)
    }

    private fun getSecondEndPoint(second: Int, millieSeconds: Int): Point {
        val halfOfView = minOf(height, width) / 2
        val secondLegLength = halfOfView * .6
        val secondDeg = second * ONE_SEC_DEGREE_IN_CIRCLE + millieSeconds * (360.0 / 60 / 1000)
        return getEndPoint(secondDeg, secondLegLength)
    }

    private fun getEndPoint(degree: Double, legLength: Double): Point {
        val secondRadian = getInRadian(degree)
        val endPointX = (sin(secondRadian) * legLength) + (width / 2)
        val endPointY = -(cos(secondRadian) * legLength) + (height / 2)
        return Point(endPointX.toInt(), endPointY.toInt())
    }

    private fun getInRadian(degree: Double) = degree * PI / 180.0

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
        const val ONE_HOUR_DEGREE_IN_CIRCLE = 360.0 / 12.0
        const val ONE_MIN_DEGREE_OF_HOUR_IN_CIRCLE = 360.0 / (12.0 * 60.0)
        const val ONE_MIN_DEGREE_IN_CIRCLE = 360.0 / 60.0
        const val ONE_SEC_DEGREE_IN_CIRCLE = 360.0 / 60.0
        const val ONE_SEC_DEGREE_OF_HOUR_IN_CIRCLE = 360.0 / (12.0 * 60.0 * 60.0)
        const val ONE_SEC_DEGREE_OF_MINUTE_IN_CIRCLE = 360.0 / (60.0 * 60.0)
    }
}