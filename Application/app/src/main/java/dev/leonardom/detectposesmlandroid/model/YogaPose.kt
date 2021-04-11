package dev.leonardom.detectposesmlandroid.model

import java.math.BigDecimal
import java.math.RoundingMode

data class YogaPose(
    val leftShoulder: Landmark? = null,
    val rightShoulder: Landmark? = null,

    val leftElbow: Landmark? = null,
    val rightElbow: Landmark? = null,

    val leftHip: Landmark? = null,
    val rightHip: Landmark? = null,

    val leftKnee: Landmark? = null,
    val rightKnee: Landmark? = null,

    val leftShoulderAngle: Double? = null,
    val rightShoulderAngle: Double? = null,

    val leftElbowAngle: Double? = null,
    val rightElbowAngle: Double? = null,

    val leftHipAngle: Double? = null,
    val rightHipAngle: Double? = null,

    val leftKneeAngle: Double? = null,
    val rightKneeAngle: Double? = null
) {

    override fun toString(): String {
        return "YOGA POSE ANGLES:\n\n" +
                "LeftShoulder: ${BigDecimal(leftShoulderAngle ?: 0.0).setScale(2, RoundingMode.HALF_EVEN)}\n" +
                "RightShoulder: ${BigDecimal(rightShoulderAngle ?: 0.0).setScale(2, RoundingMode.HALF_EVEN)}\n" +
                "LeftElbow: ${BigDecimal(leftElbowAngle ?: 0.0).setScale(2, RoundingMode.HALF_EVEN)}\n" +
                "RightElbow: ${BigDecimal(rightElbowAngle ?: 0.0).setScale(2, RoundingMode.HALF_EVEN)}\n" +
                "LeftHip: ${BigDecimal(leftHipAngle ?: 0.0).setScale(2, RoundingMode.HALF_EVEN)}\n" +
                "RightHip: ${BigDecimal(rightHipAngle ?: 0.0).setScale(2, RoundingMode.HALF_EVEN)}\n" +
                "LeftKnee: ${BigDecimal(leftKneeAngle ?: 0.0).setScale(2, RoundingMode.HALF_EVEN)}\n" +
                "RightKnee: ${BigDecimal(rightKneeAngle ?: 0.0).setScale(2, RoundingMode.HALF_EVEN)}"
    }

}