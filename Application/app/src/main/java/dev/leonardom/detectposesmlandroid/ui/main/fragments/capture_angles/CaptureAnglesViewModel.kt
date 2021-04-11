package dev.leonardom.detectposesmlandroid.ui.main.fragments.capture_angles

import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import com.opencsv.CSVWriter
import dev.leonardom.detectposesmlandroid.model.YogaPose
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

class CaptureAnglesViewModel : ViewModel() {

    private val TAG: String = "CaptureAnglesViewModel"

    private var fileName: String? = null

    var yogaPoseCaptured: YogaPose? = null

    private val dataSet: ArrayList<Array<String>> = ArrayList()
    private var numPoseCaptured: Int = 1

    init {
        generateCSVFile()
        createHeaders()
    }

    private fun generateCSVFile() {
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

    fun saveYogaPose() {
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
    }

}