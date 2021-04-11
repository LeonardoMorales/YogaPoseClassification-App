package dev.leonardom.detectposesmlandroid.utils

import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.atan2

class CalculateAngle(
    val firstPoint: PoseLandmark?,
    val midPoint: PoseLandmark?,
    val lastPoint: PoseLandmark?
) {

    fun getAngle(): Double? {
        if(firstPoint == null || midPoint == null || lastPoint == null){
            return null
        } else {
            var result = Math.toDegrees(
                (atan2(
                    lastPoint.getPosition().y - midPoint.getPosition().y,
                    lastPoint.getPosition().x - midPoint.getPosition().x
                )
                        - atan2(
                    firstPoint.getPosition().y - midPoint.getPosition().y,
                    firstPoint.getPosition().x - midPoint.getPosition().x
                )).toDouble()
            )

            result = Math.abs(result) // Angle should never be negative

            if (result > 180) {
                result = 360.0 - result // Always get the acute representation of the angle
            }

            return result
        }
    }

}