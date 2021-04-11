package dev.leonardom.detectposesmlandroid.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.*

/** Draw the detected pose in preview.  */
class PoseGraphic internal constructor(
  overlay: GraphicOverlay,
  private val pose: Pose,
  private val showInFrameLikelihood: Boolean
) :
  GraphicOverlay.Graphic(overlay) {
  private val leftPaint: Paint
  private val rightPaint: Paint
  private val whitePaint: Paint
  private val transparentPaint: Paint
  override fun draw(canvas: Canvas) {
    val landmarks =
      pose.allPoseLandmarks
    if (landmarks.isEmpty()) {
      return
    }
    // Draw all the points
    run loop@{
      for (landmark in landmarks) {
        if(isNotValidPointToDraw(landmark)){
          // return@loop
          drawPoint(canvas, landmark.position, transparentPaint)
        } else {
          drawPoint(canvas, landmark.position, whitePaint)
        }

        if (showInFrameLikelihood) {
          canvas.drawText(
            String.format(Locale.US, "%.2f", landmark.inFrameLikelihood),
            translateX(landmark.position.x),
            translateY(landmark.position.y),
            whitePaint
          )
        }
      }
    }

    val leftShoulder =
      pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
    val rightShoulder =
      pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
    val leftElbow =
      pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
    val rightElbow =
      pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
    val leftWrist =
      pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
    val rightWrist =
      pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
    val leftHip =
      pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
    val rightHip =
      pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
    val leftKnee =
      pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
    val rightKnee =
      pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
    val leftAnkle =
      pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
    val rightAnkle =
      pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
    /*val leftPinky =
      pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
    val rightPinky =
      pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
    val leftIndex =
      pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
    val rightIndex =
      pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
    val leftThumb =
      pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
    val rightThumb =
      pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
    val leftHeel =
      pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
    val rightHeel =
      pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
    val leftFootIndex =
      pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
    val rightFootIndex =
      pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)*/
    drawLine(canvas, leftShoulder!!.position, rightShoulder!!.position, whitePaint)
    drawLine(canvas, leftHip!!.position, rightHip!!.position, whitePaint)
    // Left body
    drawLine(canvas, leftShoulder.position, leftElbow!!.position, leftPaint)
    drawLine(canvas, leftElbow.position, leftWrist!!.position, leftPaint)
    drawLine(canvas, leftShoulder.position, leftHip.position, leftPaint)
    drawLine(canvas, leftHip.position, leftKnee!!.position, leftPaint)
    drawLine(canvas, leftKnee.position, leftAnkle!!.position, leftPaint)
    // drawLine(canvas, leftWrist.position, leftThumb!!.position, leftPaint)
    // drawLine(canvas, leftWrist.position, leftPinky!!.position, leftPaint)
    // drawLine(canvas, leftWrist.position, leftIndex!!.position, leftPaint)
    // rawLine(canvas, leftAnkle.position, leftHeel!!.position, leftPaint)
    // drawLine(canvas, leftHeel.position, leftFootIndex!!.position, leftPaint)
    // Right body
    drawLine(canvas, rightShoulder.position, rightElbow!!.position, rightPaint)
    drawLine(canvas, rightElbow.position, rightWrist!!.position, rightPaint)
    drawLine(canvas, rightShoulder.position, rightHip.position, rightPaint)
    drawLine(canvas, rightHip.position, rightKnee!!.position, rightPaint)
    drawLine(canvas, rightKnee.position, rightAnkle!!.position, rightPaint)
    // drawLine(canvas, rightWrist.position, rightThumb!!.position, rightPaint)
    // drawLine(canvas, rightWrist.position, rightPinky!!.position, rightPaint)
    // drawLine(canvas, rightWrist.position, rightIndex!!.position, rightPaint)
    // drawLine(canvas, rightAnkle.position, rightHeel!!.position, rightPaint)
    // drawLine(canvas, rightHeel.position, rightFootIndex!!.position, rightPaint)
  }

  fun drawPoint(canvas: Canvas, point: PointF?, paint: Paint?) {
    if (point == null) {
      return
    }
    canvas.drawCircle(
      translateX(point.x),
      translateY(point.y),
      DOT_RADIUS,
      paint!!
    )
  }

  fun drawLine(
    canvas: Canvas,
    start: PointF?,
    end: PointF?,
    paint: Paint?
  ) {
    if (start == null || end == null) {
      return
    }
    canvas.drawLine(
      translateX(start.x), translateY(start.y), translateX(end.x), translateY(end.y), paint!!
    )
  }

  private fun isNotValidPointToDraw(landmark: PoseLandmark): Boolean {
    return when(landmark.landmarkType){
      PoseLandmark.RIGHT_INDEX -> true
      PoseLandmark.RIGHT_PINKY -> true
      PoseLandmark.RIGHT_THUMB -> true

      PoseLandmark.LEFT_INDEX -> true
      PoseLandmark.LEFT_PINKY -> true
      PoseLandmark.LEFT_THUMB -> true

      PoseLandmark.LEFT_FOOT_INDEX -> true
      PoseLandmark.RIGHT_FOOT_INDEX -> true
      PoseLandmark.RIGHT_HEEL -> true
      PoseLandmark.LEFT_HEEL -> true

      PoseLandmark.RIGHT_EAR -> true
      PoseLandmark.LEFT_EAR -> true

      PoseLandmark.RIGHT_EYE -> true
      PoseLandmark.LEFT_EYE -> true
      PoseLandmark.LEFT_EYE_INNER -> true
      PoseLandmark.LEFT_EYE_OUTER -> true
      PoseLandmark.RIGHT_EYE_INNER -> true
      PoseLandmark.RIGHT_EYE_OUTER -> true

      PoseLandmark.RIGHT_MOUTH -> true
      PoseLandmark.LEFT_MOUTH -> true

      PoseLandmark.NOSE -> true

      else -> false
    }
  }

  companion object {
    private const val DOT_RADIUS = 8.0f
    private const val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
  }

  init {
    whitePaint = Paint()
    whitePaint.color = Color.WHITE
    whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE

    transparentPaint = Paint()
    transparentPaint.color = Color.TRANSPARENT

    leftPaint = Paint()
    leftPaint.color = Color.BLUE

    rightPaint = Paint()
    rightPaint.color = Color.YELLOW
  }
}