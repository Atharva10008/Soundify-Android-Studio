package com.example.soundify.ml

import android.content.Context
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.DataType

class SsdMobilenetV11Metadata1Impl private constructor(private val model: org.tensorflow.lite.Interpreter) {
    companion object {
        fun newInstance(context: Context): SsdMobilenetV11Metadata1Impl {
            val modelFile = FileUtil.loadMappedFile(context, "ssd_mobilenet_v1_1_metadata_1.tflite")
            val interpreter = org.tensorflow.lite.Interpreter(modelFile)
            return SsdMobilenetV11Metadata1Impl(interpreter)
        }
    }

    fun process(image: TensorImage): ModelOutput {
        try {
            // Create output tensors with correct shapes based on model output
            val outputLocations = Array(1) { Array(10) { FloatArray(4) } }
            val outputClasses = Array(1) { FloatArray(10) }
            val outputScores = Array(1) { FloatArray(10) }
            
            // Create output map with proper tensor buffers
            val outputs = mapOf(
                0 to outputLocations,
                1 to outputClasses,
                2 to outputScores
            )
            
            // Run inference
            model.runForMultipleInputsOutputs(arrayOf(image.buffer), outputs)
            
            // Convert to 1D arrays
            val locations1D = FloatArray(10 * 4)
            val classes1D = FloatArray(10)
            val scores1D = FloatArray(10)
            
            for (i in 0 until 10) {
                for (j in 0 until 4) {
                    locations1D[i * 4 + j] = outputLocations[0][i][j]
                }
                classes1D[i] = outputClasses[0][i]
                scores1D[i] = outputScores[0][i]
            }
            
            return object : ModelOutput {
                override val locationsAsTensorBuffer: TensorBuffer
                    get() = TensorBuffer.createFixedSize(intArrayOf(1, 10, 4), DataType.FLOAT32).apply {
                        loadArray(locations1D)
                    }
                override val classesAsTensorBuffer: TensorBuffer
                    get() = TensorBuffer.createFixedSize(intArrayOf(1, 10), DataType.FLOAT32).apply {
                        loadArray(classes1D)
                    }
                override val scoresAsTensorBuffer: TensorBuffer
                    get() = TensorBuffer.createFixedSize(intArrayOf(1, 10), DataType.FLOAT32).apply {
                        loadArray(scores1D)
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun close() {
        model.close()
    }
} 