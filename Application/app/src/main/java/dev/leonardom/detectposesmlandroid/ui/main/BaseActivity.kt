package dev.leonardom.detectposesmlandroid.ui.main

import android.content.pm.PackageManager
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.leonardom.detectposesmlandroid.utils.VisionImageProcessor

@AndroidEntryPoint
abstract class BaseActivity: AppCompatActivity() {

    // Base pose detector with streaming frames, when depending on the pose-detection sdk
    val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .build()

    val poseDetector = PoseDetection.getClient(options)

    var imageProcessor: VisionImageProcessor? = null

    private val permissionCallbacks = HashMap<Int, PermissionRequest>()

    private data class PermissionRequest(
        val permission: String,
        val onDeniedAction: (() -> Unit)?,
        val onGrantedAction: () -> Unit
    )

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionCallbacks[requestCode]?.let {
            if (checkSelfPermission(baseContext, it.permission) == PackageManager.PERMISSION_GRANTED) {
                it.onGrantedAction()
            } else {
                it.onDeniedAction?.invoke()
            }
            permissionCallbacks.remove(requestCode)
        }
    }

    fun onPermission(
        permission: String,
        onDeniedAction: (() -> Unit)? = null,
        onGrantedAction: () -> Unit
    ) {
        val requestCode = View.generateViewId()
        permissionCallbacks[requestCode] = PermissionRequest(permission, onDeniedAction, onGrantedAction)
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }

}