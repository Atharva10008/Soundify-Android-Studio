package com.example.soundify.ml

import android.content.Context
import org.tensorflow.lite.support.image.TensorImage

interface Model {
    fun process(image: TensorImage): ModelOutput
    fun close()
}

interface ModelOutput {
    val locationsAsTensorBuffer: org.tensorflow.lite.support.tensorbuffer.TensorBuffer
    val classesAsTensorBuffer: org.tensorflow.lite.support.tensorbuffer.TensorBuffer
    val scoresAsTensorBuffer: org.tensorflow.lite.support.tensorbuffer.TensorBuffer
} 