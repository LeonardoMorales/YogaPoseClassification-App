package dev.leonardom.detectposesmlandroid.model

import android.content.Context
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import dev.leonardom.detectposesmlandroid.utils.CalculateAngle
import dev.leonardom.detectposesmlandroid.utils.PoseDetectorProcessor
import kotlinx.coroutines.*

class YogaPoseDetector(
    context: Context,
    options: PoseDetectorOptions,
    showInFrameLikelihood: Boolean
): PoseDetectorProcessor(context, options, showInFrameLikelihood) {

    private var yogaPose: YogaPose? = null
    var yogaPoseDetectorListener: YogaPoseDetectorListener? = null

    override fun identifyPose(context: Context, pose: Pose) {
        /*
        FIRST METHOD
         */
        CoroutineScope(Dispatchers.Default).launch {
            val leftShoulder = async { return@async getLeftShoulderAngle(pose) }.await()
            val rightShoulder = async { return@async getRightShoulderAngle(pose) }.await()

            val leftElbow = async { return@async getLeftElbowAngle(pose) }.await()
            val rightElbow = async { return@async getRightElbowAngle(pose) }.await()

            val leftHipAngle = async { return@async getLeftHipAngle(pose) }.await()
            val rightHipAngle = async { return@async getRightHipAngle(pose) }.await()

            val leftKneeAngle = async { return@async getLeftKneeAngle(pose) }.await()
            val rightKneeAngle = async {return@async getRightKneeAngle(pose) }.await()

            yogaPose = YogaPose(
                leftElbowAngle = leftElbow,
                rightElbowAngle = rightElbow,
                leftShoulderAngle = leftShoulder,
                rightShoulderAngle = rightShoulder,
                leftHipAngle = leftHipAngle,
                rightHipAngle = rightHipAngle,
                leftKneeAngle = leftKneeAngle,
                rightKneeAngle = rightKneeAngle
            )

            launch {
                withContext(Dispatchers.Main){
                    yogaPose?.let {
                        yogaPoseDetectorListener?.poseIdentified(it)
                    }
                }
            }
        }

        /*
        SECOND METHOD
         */
        /*CoroutineScope(Dispatchers.Default).launch {
            val leftShoulder = async { return@async getLeftShoulder(pose) }.await()
            val rightShoulder = async { return@async getRightShoulder(pose) }.await()

            val leftElbow = async { return@async getLeftElbow(pose) }.await()
            val rightElbow = async { return@async getRightElbow(pose) }.await()

            val leftHip = async { return@async getLeftHip(pose) }.await()
            val rightHip = async { return@async getRightHip(pose) }.await()

            val leftKnee = async { return@async getLeftKnee(pose) }.await()
            val rightKnee = async {return@async getRightKnee(pose) }.await()

            yogaPose = YogaPose(
                leftShoulder = leftShoulder,
                rightShoulder = rightShoulder,
                leftElbow = leftElbow,
                rightElbow = rightElbow,
                leftHip = leftHip,
                rightHip = rightHip,
                leftKnee = leftKnee,
                rightKnee = rightKnee
            )

            launch {
                withContext(Dispatchers.Main){
                    yogaPose?.let {
                        yogaPoseDetectorListener?.poseIdentified(it)
                    }
                }
            }
        }*/
    }

    /*
        LANDMARKS
     */
    private fun getLeftShoulder(pose: Pose): Landmark? {
        var leftShoulder: Landmark?

        val x = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position?.x
        val y = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position?.y

        val firstPoint = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val midPoint = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            leftShoulder = Landmark(x,y,it)
            return leftShoulder
        } ?: return null
    }

    private fun getRightShoulder(pose: Pose): Landmark? {
        var rightShoulder: Landmark?

        val x = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position?.x
        val y = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position?.y

        val firstPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val midPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            rightShoulder = Landmark(x,y,it)
            return rightShoulder
        } ?: return null
    }

    private fun getLeftElbow(pose: Pose): Landmark? {
        var leftElbow: Landmark?

        val x = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position?.x
        val y = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position?.y

        val firstPoint = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val midPoint = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            leftElbow = Landmark(x,y,it)
            return leftElbow
        } ?: return null
    }

    private fun getRightElbow(pose: Pose): Landmark? {
        var rightElbow: Landmark?

        val x = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position?.x
        val y = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)?.position?.y

        val firstPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val midPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            rightElbow = Landmark(x,y,it)
            return rightElbow
        } ?: return null
    }

    private fun getLeftHip(pose: Pose): Landmark? {
        var leftHip: Landmark?

        val x = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)?.position?.x
        val y = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)?.position?.y

        val firstPoint = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val midPoint = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            leftHip = Landmark(x,y,it)
            return leftHip
        } ?: return null
    }

    private fun getRightHip(pose: Pose): Landmark? {
        var rightHip: Landmark?

        val x = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)?.position?.x
        val y = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)?.position?.y

        val firstPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val midPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            rightHip = Landmark(x,y,it)
            return rightHip
        } ?: return null
    }

    private fun getLeftKnee(pose: Pose): Landmark? {
        var leftKnee: Landmark?

        val x = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)?.position?.x
        val y = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)?.position?.y

        val firstPoint = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val midPoint = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            leftKnee = Landmark(x,y,it)
            return leftKnee
        } ?: return null
    }

    private fun getRightKnee(pose: Pose): Landmark? {
        var rightKnee: Landmark?

        val x = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)?.position?.x
        val y = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)?.position?.y

        val firstPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val midPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            rightKnee = Landmark(x,y,it)
            return rightKnee
        } ?: return null
    }

    /*
        LANDMARKS ANGLES
     */

    private fun getLeftShoulderAngle(pose: Pose): Double {
        val firstPoint = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val midPoint = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            return it
        } ?: return 0.0
    }

    private fun getRightShoulderAngle(pose: Pose): Double {
        val firstPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val midPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            return it
        } ?: return 0.0
    }

    private fun getLeftElbowAngle(pose: Pose): Double {
        val firstPoint = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val midPoint = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            return it
        } ?: return 0.0
    }

    private fun getRightElbowAngle(pose: Pose): Double {
        val firstPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val midPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            return it
        } ?: return 0.0
    }

    private fun getLeftHipAngle(pose: Pose): Double {
        val firstPoint = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val midPoint = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            return it
        } ?: return 0.0
    }

    private fun getRightHipAngle(pose: Pose): Double {
        val firstPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val midPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            return it
        } ?: return 0.0
    }

    private fun getLeftKneeAngle(pose: Pose): Double {
        val firstPoint = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val midPoint = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            return it
        } ?: return 0.0
    }

    private fun getRightKneeAngle(pose: Pose): Double {
        val firstPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val midPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val lastPoint = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        val angle = CalculateAngle(firstPoint, midPoint, lastPoint).getAngle()
        angle?.let {
            return it
        } ?: return 0.0
    }

}