package dev.leonardom.detectposesmlandroid.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import dev.leonardom.detectposesmlandroid.R
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.opencsv.CSVWriter
import dagger.hilt.android.AndroidEntryPoint
import dev.leonardom.detectposesmlandroid.MainNavGraphDirections
import dev.leonardom.detectposesmlandroid.databinding.ActivityMainBinding
import dev.leonardom.detectposesmlandroid.displayToast
import dev.leonardom.detectposesmlandroid.model.YogaPose
import dev.leonardom.detectposesmlandroid.model.YogaPoseDetector
import dev.leonardom.detectposesmlandroid.model.YogaPoseDetectorListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel: MainViewModel by viewModels()

    // private lateinit var cameraExecutor: ExecutorService

    private var yogaPoseDetector: YogaPoseDetector? = null

    /*private var yogaPoseCaptured: YogaPose? = null

    // private lateinit var fileName: String

    val dataSet: ArrayList<Array<String>> = ArrayList()
    var numPoseCaptured: Int = 1*/

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        onPermission(
            STORAGE_PERMISSION,
            onDeniedAction = {
                this.displayToast("Permissions not granted by the user.")
                finish()
            }, onGrantedAction = {
                // generateCSVFile()
                // createHeaders()
            }
        )

        // Request camera permissions
        onPermission(
            CAMERA_PERMISSION,
            onDeniedAction = {
                this.displayToast("Permissions not granted by the user.")
                finish()
            }, onGrantedAction = {
                // startCamera()
            })

        /*try {
            viewModel.interpreter = Interpreter(viewModel.loadModelFile(), null)
        } catch (exception: Exception) {
            Toast.makeText(this, "Error cargando modelo de ML", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Exception: $exception", Toast.LENGTH_SHORT).show()
        }*/

        // cameraExecutor = Executors.newSingleThreadExecutor()

        /*binding.btnCaptureAngles.setOnClickListener {
            Log.d(TAG, "YogaPoseCaptured: $yogaPoseCaptured")
            saveYogaPose()
        }*/

        /*yogaPoseDetector = YogaPoseDetector(this, options, false)
        imageProcessor = yogaPoseDetector
        yogaPoseDetector!!.yogaPoseDetectorListener = this*/
    }

    /* private fun generateCSVFile() {
        val folder = File((Environment.getExternalStorageDirectory().absolutePath + "/PoseML"))

        var algo = false
        if (!folder.exists()) {
            algo = folder.mkdir()
        }

        println("$algo")

        fileName = "$folder/Test_${Date().time}.csv"
    }

    private fun createHeaders() {
        val writer: CSVWriter?
        try {
            writer = CSVWriter(FileWriter(fileName))

            dataSet.add(
                arrayOf(
                    "POSE",
                    "LeftShoulderAngle",
                    "RightShoulderAngle",
                    "LeftElbowAngle",
                    "RightElbowAngle",
                    "LeftHipAngle",
                    "RightHipAngle",
                    "LeftKneeAngle",
                    "RightKneeAngle"
                )
            )

            writer.writeAll(dataSet)

        } catch (e: IOException) {
            e.printStackTrace()
            Log.d(TAG, "SaveYogaPose, error: ${e.message}")
        }
    }

    private fun saveYogaPose() {
        val writer: CSVWriter?
        try {
            writer = CSVWriter(FileWriter(fileName))

            yogaPoseCaptured?.let {
                Log.d(TAG, "POSE CAPTURED #$numPoseCaptured")
                dataSet.add(
                    arrayOf(
                        getYogaPoseName(),
                        "${it.leftShoulderAngle}",
                        "${it.rightShoulderAngle}",
                        "${it.leftElbowAngle}",
                        "${it.rightElbowAngle}",
                        "${it.leftHipAngle}",
                        "${it.rightHipAngle}",
                        "${it.leftKneeAngle}",
                        "${it.rightKneeAngle}"
                    )
                )

                writer.writeAll(dataSet)

                writer.close()
                numPoseCaptured++
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d(TAG, "SaveYogaPose, error: ${e.message}")
        }
    }

    private fun getYogaPoseName(): String {
        return when (numPoseCaptured) {
            in 1..15 -> "Random"
            else -> "WRONG NUM POSE"
        }
    }*/

    /*private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    // it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, object : ImageAnalysis.Analyzer {

                        @SuppressLint("UnsafeExperimentalUsageError")
                        override fun analyze(imageProxy: ImageProxy) {

                            /*binding.graphicOverlay.setImageSourceInfo(
                                imageProxy.width, imageProxy.height, false
                            )

                            imageProcessor!!.processImageProxy(imageProxy, binding.graphicOverlay)*/
                        }
                    })
                }


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))

    }*/

    /*override fun onDestroy() {
        super.onDestroy()
        // cameraExecutor.shutdown()
    }*/

    /*override fun poseIdentified(yogaPose: YogaPose) {
        /*yogaPoseCaptured = yogaPose
        lifecycleScope.launchWhenStarted {
            val input = viewModel.normalizeValues(yogaPose)
            val result = viewModel.doInference(input).toString()
            withContext(Dispatchers.Main){
                Log.d(TAG, "YOGA POSE PREDICTED: $result")
                Toast.makeText(this@MainActivity, result, Toast.LENGTH_SHORT).show()
            }
        }*/
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.capture_angles -> {
                val action = MainNavGraphDirections.actionGlobalCaptureAnglesFragment()
                navController.navigate(action)
            }
            R.id.identify_pose -> {
                val action = MainNavGraphDirections.actionGlobalIdentifyPoseFragment()
                navController.navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.right_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val TAG = "CameraXBasic"
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}