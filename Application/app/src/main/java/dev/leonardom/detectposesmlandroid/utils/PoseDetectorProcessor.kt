/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.leonardom.detectposesmlandroid.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.*
import dev.leonardom.detectposesmlandroid.model.YogaPoseDetectorListener

/** A processor to run pose detector.  */
abstract class PoseDetectorProcessor(
  private val context: Context,
  options: PoseDetectorOptionsBase,
  private val showInFrameLikelihood: Boolean
) : VisionProcessorBase<Pose>(context) {

  private val detector: PoseDetector

  override fun stop() {
    super.stop()
    detector.close()
  }

  override fun detectInImage(image: InputImage): Task<Pose> {
    return detector.process(image)
  }

  override fun onSuccess(
    results: Pose,
    graphicOverlay: GraphicOverlay
  ) {
    graphicOverlay.add(PoseGraphic(graphicOverlay, results, showInFrameLikelihood))
    // getLeftKneeAngle(results)

    identifyPose(context, results)

    // poseIdentified(context, results)
  }

  abstract fun identifyPose(context: Context, pose: Pose)

  /*fun getLeftKneeAngle(pose: Pose){
    val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
    val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
    val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

    if(leftHip != null && leftKnee != null && leftAnkle != null){
      val leftKneeAngle = getAngle(
        leftHip,
        leftKnee,
        leftAnkle
      )
      Log.d(MainActivity.TAG, "LEFT KNEE ANGLE: $leftKneeAngle")
    }
  }*/

  override fun onFailure(e: Exception) {
    Log.e(
      TAG,
      "Pose detection failed!",
      e
    )
  }

  companion object {
    private const val TAG = "PoseDetectorProcessor"
  }

  /*override fun poseIdentified(context: Context, pose: Pose) {

  }*/

  init {
    detector = PoseDetection.getClient(options)
  }
}
