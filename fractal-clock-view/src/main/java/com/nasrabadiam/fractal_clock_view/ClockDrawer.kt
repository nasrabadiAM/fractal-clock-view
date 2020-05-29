package com.nasrabadiam.fractal_clock_view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ClockDrawer {

    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = LEG_MOST_THICKNESS
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
    }

    fun drawFractal(
        canvas: Canvas,
        hour: Int,
        minute: Int,
        second: Int,
        milliSeconds: Int,
        maximumLegLength: Float,
        centerOfView: Point
    ) {

        val hourEndPoint = getHourEndPoint(
            hour,
            minute,
            second,
            maximumLegLength * HOUR_LEG_LENGTH_RATIO,
            centerOfView
        )
        drawLine(
            canvas,
            LEG_MOST_THICKNESS,
            centerOfView.x,
            centerOfView.y,
            hourEndPoint.x,
            hourEndPoint.y
        )

        drawBranch(
            canvas,
            second,
            milliSeconds,
            minute,
            centerOfView,
            0,
            maximumLegLength * MINUTE_LEG_LENGTH_RATIO,
            LEG_MOST_THICKNESS
        )
        drawBranch(
            canvas,
            second,
            milliSeconds,
            minute,
            centerOfView,
            0,
            maximumLegLength * SECOND_LEG_LENGTH_RATIO,
            LEG_MOST_THICKNESS
        )
    }

    private fun drawBranch(
        canvas: Canvas,
        second: Int,
        milliSeconds: Int,
        minute: Int,
        centerOfView: Point,
        previousDepth: Int,
        previousLength: Float,
        previousThickness: Float
    ) {
        val secondLength = previousLength * .8f
        val minuteLength = previousLength * .6f
        val depth = previousDepth + 1
        val thickness = previousThickness * 2 / 3

        val currentMinuteEndOfView: Point = getMinuteEndPoint(
            minute,
            second,
            minuteLength,
            centerOfView,
            depth * .5f
        )
        drawLine(
            canvas,
            thickness,
            centerOfView.x,
            centerOfView.y,
            currentMinuteEndOfView.x,
            currentMinuteEndOfView.y
        )

        val currentSecondEndOfView: Point = getSecondEndPoint(
            second,
            milliSeconds,
            secondLength,
            centerOfView,
            depth * .5f
        )
        drawLine(
            canvas,
            thickness,
            centerOfView.x,
            centerOfView.y,
            currentSecondEndOfView.x,
            currentSecondEndOfView.y
        )

        if (depth > MAX_FRACTAL_DEPTH) return
        drawBranch(
            canvas,
            second,
            milliSeconds,
            minute,
            currentMinuteEndOfView,
            depth,
            minuteLength,
            thickness
        )
        drawBranch(
            canvas,
            second,
            milliSeconds,
            minute,
            currentSecondEndOfView,
            depth,
            secondLength,
            thickness
        )
    }

    private fun drawLine(
        canvas: Canvas,
        thickness: Float,
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float
    ) {
        paint.strokeWidth = thickness
        canvas.drawLine(startX, startY, endX, endY, paint)
    }

    private fun getHourEndPoint(
        hour: Int,
        minute: Int,
        second: Int,
        hourLegLength: Float,
        centerPoint: Point
    ): Point {
        val hourDeg = hour * ONE_HOUR_DEGREE_IN_CIRCLE +
                minute * ONE_MIN_DEGREE_OF_HOUR_IN_CIRCLE +
                second * ONE_SEC_DEGREE_OF_HOUR_IN_CIRCLE
        val degreeInRadian = getInRadian(hourDeg)
        return getEndPoint(degreeInRadian, hourLegLength, centerPoint)
    }

    private fun getMinuteEndPoint(
        minute: Int,
        second: Int,
        minuteLegLength: Float,
        centerPoint: Point,
        additionalDegree: Float = 0f
    ): Point {
        val minuteDeg = minute * ONE_MIN_DEGREE_IN_CIRCLE +
                second * ONE_SEC_DEGREE_OF_MINUTE_IN_CIRCLE
        val degreeInRadian = getInRadian(minuteDeg) + additionalDegree
        return getEndPoint(degreeInRadian, minuteLegLength, centerPoint)
    }

    private fun getSecondEndPoint(
        second: Int,
        millieSeconds: Int,
        secondLegLength: Float,
        centerPoint: Point,
        additionalDegree: Float = 0f
    ): Point {
        val secondDeg = second * ONE_SEC_DEGREE_IN_CIRCLE +
                millieSeconds * ONE_MILLIE_SEC_DEGREE_OF_SECOND_IN_CIRCLE
        val degreeInRadian = getInRadian(secondDeg) + additionalDegree
        return getEndPoint(degreeInRadian, secondLegLength, centerPoint)
    }

    private fun getEndPoint(
        degreeInRadian: Double,
        legLength: Float,
        centerPoint: Point
    ): Point {
        val endPointX = (sin(degreeInRadian) * legLength) + centerPoint.x
        val endPointY = -(cos(degreeInRadian) * legLength) + centerPoint.y
        return Point(endPointX.toFloat(), endPointY.toFloat())
    }

    private fun getInRadian(degree: Double) = degree * PI / 180.0

    class Point(val x: Float, val y: Float)

    companion object {
        private const val ONE_HOUR_DEGREE_IN_CIRCLE = 360.0 / 12.0
        private const val ONE_MIN_DEGREE_OF_HOUR_IN_CIRCLE = 360.0 / (12.0 * 60.0)
        private const val ONE_MIN_DEGREE_IN_CIRCLE = 360.0 / 60.0
        private const val ONE_SEC_DEGREE_IN_CIRCLE = 360.0 / 60.0
        private const val ONE_SEC_DEGREE_OF_HOUR_IN_CIRCLE = 360.0 / (12.0 * 60.0 * 60.0)
        private const val ONE_SEC_DEGREE_OF_MINUTE_IN_CIRCLE = 360.0 / (60.0 * 60.0)
        private const val ONE_MILLIE_SEC_DEGREE_OF_SECOND_IN_CIRCLE = 360.0 / 60 / 1000
        private const val LEG_MOST_THICKNESS = 8f
        private const val HOUR_LEG_LENGTH_RATIO = .2f
        private const val MINUTE_LEG_LENGTH_RATIO = .4f
        private const val SECOND_LEG_LENGTH_RATIO = .6f
        private const val MAX_FRACTAL_DEPTH = 10
    }
}