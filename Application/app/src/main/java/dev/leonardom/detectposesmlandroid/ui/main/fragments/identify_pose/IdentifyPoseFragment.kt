package dev.leonardom.detectposesmlandroid.ui.main.fragments.identify_pose

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.opencsv.CSVWriter
import dagger.hilt.android.AndroidEntryPoint
import dev.leonardom.detectposesmlandroid.databinding.FragmentIdentifyPoseBinding
import dev.leonardom.detectposesmlandroid.displayToast
import dev.leonardom.detectposesmlandroid.model.YogaPose
import dev.leonardom.detectposesmlandroid.model.YogaPoseDetector
import dev.leonardom.detectposesmlandroid.model.YogaPoseDetectorListener
import dev.leonardom.detectposesmlandroid.ui.main.fragments.BaseFragment
import dev.leonardom.detectposesmlandroid.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class IdentifyPoseFragment : BaseFragment() {

    private val TAG: String = "IdentifyPoseFragment"

    private var _binding: FragmentIdentifyPoseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IdentifyPoseViewModel by viewModels()

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdentifyPoseBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        try {
            viewModel.interpreter = Interpreter(viewModel.loadModelFile(), null)
        } catch (exception: Exception) {
            Toast.makeText(requireContext(), "Error cargando modelo de ML", Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), "Exception: $exception", Toast.LENGTH_SHORT).show()
        }

    }

    override fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, object : ImageAnalysis.Analyzer {

                        @SuppressLint("UnsafeExperimentalUsageError")
                        override fun analyze(imageProxy: ImageProxy) {

                            binding.graphicOverlay.setImageSourceInfo(
                                imageProxy.width, imageProxy.height, false
                            )

                            imageProcessor!!.processImageProxy(imageProxy, binding.graphicOverlay)
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
                Log.e(MainActivity.TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    override fun poseIdentified(yogaPose: YogaPose) {
        lifecycleScope.launchWhenStarted {
            val input = viewModel.normalizeValues(yogaPose)
            val result = viewModel.doInference(input).toString()
            withContext(Dispatchers.Main){
                delay(300)
                // Log.d(MainActivity.TAG, "YOGA POSE PREDICTED: $result")
                displayToast(requireContext(), result)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}