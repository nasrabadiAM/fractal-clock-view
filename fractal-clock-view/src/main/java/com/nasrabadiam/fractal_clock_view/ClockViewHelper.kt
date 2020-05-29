package com.nasrabadiam.fractal_clock_view

import android.util.Log
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class ClockViewHelper {

    fun getLegLinesToDraw(
        hour: Int,
        minute: Int,
        second: Int,
        milliSeconds: Int,
        centerPoint: Point
    ): List<Line> = mutableListOf<Line>().apply {
        val maximumLegLength = minOf(centerPoint.x, centerPoint.y)

        val hourEndPoint = getHourEndPoint(
            hour,
            minute,
            second,
            maximumLegLength * HOUR_LEG_LENGTH_RATIO,
            centerPoint
        )
        val hourLeg = Line(centerPoint, hourEndPoint, LegType.HOUR)
        add(hourLeg)

        val minuteEndPoint = getMinuteEndPoint(
            minute = minute,
            second = second,
            minuteLegLength = maximumLegLength * MINUTE_LEG_LENGTH_RATIO,
            centerPoint = centerPoint
        )
        val minuteLeg = Line(centerPoint, minuteEndPoint, LegType.MINUTE)
        add(minuteLeg)

        val maximumSecondLegLength = maximumLegLength * SECOND_LEG_LENGTH_RATIO
        val secondEndPoint = getSecondEndPoint(
            second,
            milliSeconds,
            maximumSecondLegLength,
            centerPoint
        )
        val secondLeg = Line(centerPoint, secondEndPoint, LegType.SECOND)
        add(secondLeg)

        val additionalDegree = getDegreeBetweenTwoLegs(secondLeg, hourLeg)
        getSecondsLegs(
            additionalDegree,
            second,
            milliSeconds,
            maximumSecondLegLength,
            secondEndPoint
        )
    }

    private fun MutableList<Line>.getSecondsLegs(
        additionalDegree: Float,
        second: Int,
        milliSeconds: Int,
        maximumLegLength: Float,
        secondEndPoint: Point,
        nestedCount: Int = 0
    ) {
        val _nestedCount = nestedCount + 1
        if (_nestedCount > 5) return

        val maximumNestedLegLength = maximumLegLength * NESTED_LEG_RATIO / _nestedCount
        val secondNestedEndPoint = getSecondEndPoint(
            second,
            milliSeconds,
            maximumNestedLegLength,
            secondEndPoint,
            additionalDegree
        )
        val secondNestedLeg = Line(secondEndPoint, secondNestedEndPoint, LegType.SECOND)
        add(secondNestedLeg)

        getSecondsLegs(
            additionalDegree,
            second,
            milliSeconds,
            maximumNestedLegLength,
            secondNestedEndPoint,
            _nestedCount
        )
    }

    private fun getSecondLegsToDraw() {
    }

    private fun getNestedLegLinesForSecondsLeg(
        second: Int,
        millieSeconds: Int,
        additionalDegree: Float,
        maximumLegLength: Float, // 1/3 of the last nesting
        centerPoint: Point //end point of minute leg
    ): List<Line> = mutableListOf<Line>().apply {

        val secondEndPoint = getSecondEndPoint(
            second = second,
            millieSeconds = millieSeconds,
            secondLegLength = maximumLegLength,
            centerPoint = centerPoint,
            additionalDegree = additionalDegree
        )
        add(Line(centerPoint, secondEndPoint, LegType.SECOND))
    }

    private fun getNestedLegLinesForMinuteLeg(
        minute: Int,
        second: Int,
        additionalDegree: Float,
        maximumLegLength: Float, // 1/3 of the last nesting
        centerPoint: Point //end point of minute leg
    ): List<Line> = mutableListOf<Line>().apply {

        val minuteEndPoint = getMinuteEndPoint(
            minute = minute,
            second = second,
            minuteLegLength = maximumLegLength,
            centerPoint = centerPoint,
            additionalDegree = additionalDegree
        )
        add(Line(centerPoint, minuteEndPoint, LegType.MINUTE))
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
        Log.e("ClockViewHelper", "additionalDegree=$additionalDegree")
        Log.e("ClockViewHelper", "secondDegree=$secondDeg")
        Log.e("ClockViewHelper", "total=$degreeInRadian")

        return getEndPoint(degreeInRadian, secondLegLength, centerPoint)
    }

    private fun getDegreeBetweenTwoLegs(first: Line, second: Line): Float {
        val magnitudeOfBoth = first.getVector() * second.getVector()

        val firstMagnitude = first.getVector().getMagnitude()
        val secondMagnitude = second.getVector().getMagnitude()

        val degreeInRadian = acos(magnitudeOfBoth / (firstMagnitude * secondMagnitude))
        return degreeInRadian
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

    companion object {
        private const val ONE_HOUR_DEGREE_IN_CIRCLE = 360.0 / 12.0
        private const val ONE_MIN_DEGREE_OF_HOUR_IN_CIRCLE = 360.0 / (12.0 * 60.0)
        private const val ONE_MIN_DEGREE_IN_CIRCLE = 360.0 / 60.0
        private const val ONE_SEC_DEGREE_IN_CIRCLE = 360.0 / 60.0
        private const val ONE_SEC_DEGREE_OF_HOUR_IN_CIRCLE = 360.0 / (12.0 * 60.0 * 60.0)
        private const val ONE_SEC_DEGREE_OF_MINUTE_IN_CIRCLE = 360.0 / (60.0 * 60.0)
        private const val ONE_MILLIE_SEC_DEGREE_OF_SECOND_IN_CIRCLE = 360.0 / 60 / 1000
        private const val HOUR_LEG_LENGTH_RATIO = .1f
        private const val MINUTE_LEG_LENGTH_RATIO = .3f
        private const val SECOND_LEG_LENGTH_RATIO = .5f
        private const val NESTED_LEG_RATIO = .7f
    }
}

class Point(val x: Float, val y: Float)
class Vector(val x: Float, val y: Float) {
    fun getMagnitude() = sqrt(x * x + y * y)
}

operator fun Vector.times(vector: Vector) = this.x * vector.x + this.y * vector.y

class Line(val startPoint: Point, val endPoint: Point, val legType: LegType) {

    fun getVector() = Vector(endPoint.x - startPoint.x, endPoint.y - startPoint.y)
}

enum class LegType {
    HOUR, MINUTE, SECOND
}