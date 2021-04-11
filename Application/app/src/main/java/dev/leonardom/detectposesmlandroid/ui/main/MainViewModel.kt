package dev.leonardom.detectposesmlandroid.ui.main

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import dev.leonardom.detectposesmlandroid.model.YogaPose
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val applicationContext: Context
): ViewModel() {

    lateinit var interpreter: Interpreter

    /** Labels corresponding to the output of the vision model.  */
    private var labels: List<String>? = null

    init {
        // Loads labels out from the label file.
        labels = FileUtil.loadLabels(applicationContext, "labels.txt")
    }

    val mean = arrayListOf(90.131819f, 89.043270f, 144.747361f, 147.245133f, 106.229700f, 103.194946f, 116.386812f, 113.702324f)
    val std = arrayListOf(62.528951f, 62.945958f, 41.655029f, 41.981025f, 43.127208f, 42.296699f, 57.592427f, 56.854638f)

    fun doInference(input: Array<FloatArray>): String? {
        val output = Array(1){ FloatArray(10) }
        interpreter.run(input, output)

        for(x in 0..9){
            Log.d(MainActivity.TAG, "OUTPUT: ${output[0][x]}")
        }

        // return output[0][0]
        return getYogaPosePredicted(output[0])
    }

    @Throws(IOException::class)
    fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = applicationContext.assets.openFd("ypc_m1.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }

    private fun getYogaPosePredicted(array: FloatArray): String? {
        var max = array[0]
        var pos = 0
        for(i in 0..array.size-1){
            if(array[i] > max){
                max = array[i]
                pos = i
            }
        }
        Log.d(MainActivity.TAG, "Largest element: $max at position: $pos")

        val yogaName = labels?.get(pos)
        Log.d(MainActivity.TAG, "YogaPose Name: $yogaName")
        return yogaName
    }

    fun normalizeValues(yogaPose: YogaPose): Array<FloatArray> {
        val floats = Array(1){ FloatArray(8) }
        floats[0][0] = (yogaPose.leftShoulderAngle!!.toFloat() - mean[0]) / std[0]
        floats[0][1] = (yogaPose.rightShoulderAngle!!.toFloat() - mean[1]) / std[1]
        floats[0][2] = (yogaPose.leftElbowAngle!!.toFloat() - mean[2]) / std[2]
        floats[0][3] = (yogaPose.rightElbowAngle!!.toFloat() - mean[3]) / std[3]
        floats[0][4] = (yogaPose.leftHipAngle!!.toFloat() - mean[4]) / std[4]
        floats[0][5] = (yogaPose.rightHipAngle!!.toFloat() - mean[5]) / std[5]
        floats[0][6] = (yogaPose.leftKneeAngle!!.toFloat() - mean[6]) / std[6]
        floats[0][7] = (yogaPose.rightKneeAngle!!.toFloat() - mean[7]) / std[7]

        return floats
    }
}