package com.example.soundify.ui.main

import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.soundify.R
import com.example.soundify.databinding.FragmentMainBinding
import com.example.soundify.ml.DetectedObject
import com.example.soundify.ml.SsdMobilenetV11Metadata1Impl
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import android.os.Build
import org.tensorflow.lite.DataType

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }

    lateinit var auth: FirebaseAuth
    lateinit var takePictureButton: Button
    lateinit var launchGalleryButton: Button
    lateinit var playButton: Button
    lateinit var pauseButton: Button
    lateinit var imageView: ImageView
    lateinit var resultText: TextView
    lateinit var photoURI: Uri
    lateinit var bitmap: Bitmap
    private var currentPhotoPath: String = ""

    lateinit var textToSpeech: TextToSpeech

    val paint = Paint()
    var detectedObjects = mutableListOf<DetectedObject>()
    lateinit var labels: List<String>
    lateinit var model: SsdMobilenetV11Metadata1Impl

    val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                val loadedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                }
                // Convert to ARGB_8888 if needed
                bitmap = if (loadedBitmap.config == Bitmap.Config.ARGB_8888) {
                    loadedBitmap
                } else {
                    loadedBitmap.copy(Bitmap.Config.ARGB_8888, true)
                }
                imageView.setImageBitmap(bitmap)
                getPredictions()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            try {
                val file = File(currentPhotoPath)
                val loadedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file))
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, Uri.fromFile(file))
                }
                // Convert to ARGB_8888 if needed
                bitmap = if (loadedBitmap.config == Bitmap.Config.ARGB_8888) {
                    loadedBitmap
                } else {
                    loadedBitmap.copy(Bitmap.Config.ARGB_8888, true)
                }
                imageView.setImageBitmap(bitmap)
                getPredictions()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        try {
            // Initialize ML model
            labels = FileUtil.loadLabels(requireContext(), "labels.txt")
            model = SsdMobilenetV11Metadata1Impl.newInstance(requireContext())
            Toast.makeText(requireContext(), "Model initialized successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error initializing model: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

        // Initialize views
        takePictureButton = binding.button
        launchGalleryButton = binding.button2
        imageView = binding.imageView
        resultText = binding.result
        playButton = binding.playButton
        pauseButton = binding.pauseButton

        resultText.movementMethod = ScrollingMovementMethod()

        // Set up click listeners
        takePictureButton.setOnClickListener { dispatchTakePictureIntent() }
        launchGalleryButton.setOnClickListener {
            getContent.launch("image/*")
        }

        // Initialize TextToSpeech
        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.US
            }
        }

        // Play button to start speaking detected object
        playButton.setOnClickListener {
            val text = resultText.text.toString()
            if (text.isNotEmpty()) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        // Pause button to stop speaking
        pauseButton.setOnClickListener {
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
            }
        }

        // Set up logout button
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_main_to_login)
        }

        // Set up touch listener for image view
        imageView.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                val x = event.x
                val y = event.y

                for (detectedObject in detectedObjects) {
                    if (detectedObject.boundingBox.contains(x, y)) {
                        fetchObjectInformation(detectedObject.label)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(
                requireContext(),
                "com.example.soundify.fileprovider",
                photoFile
            )
            takePicture.launch(photoURI)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(null)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun getPredictions() {
        if (!::model.isInitialized) {
            Toast.makeText(requireContext(), "Model not initialized yet", Toast.LENGTH_SHORT).show()
            return
        }

        if (!::bitmap.isInitialized) {
            Toast.makeText(requireContext(), "No image loaded", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            detectedObjects.clear()
            val image = TensorImage.fromBitmap(bitmap)
            val processedImage = imageProcessor.process(image)
            val outputs = model.process(processedImage)
            val locations = outputs.locationsAsTensorBuffer.floatArray
            val classes = outputs.classesAsTensorBuffer.floatArray
            val scores = outputs.scoresAsTensorBuffer.floatArray

            val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(mutableBitmap)
            val h = mutableBitmap.height
            val w = mutableBitmap.width

            paint.textSize = h / 15f
            paint.strokeWidth = h / 85f

            var detectionCount = 0
            scores.forEachIndexed { index, score ->
                if (score > 0.5) {
                    detectionCount++
                    val x = index * 4
                    val boundingBox = RectF(
                        locations[x + 1] * w, locations[x] * h,
                        locations[x + 3] * w, locations[x + 2] * h
                    )
                    val labelText = labels[classes[index].toInt()]
                    detectedObjects.add(DetectedObject(labelText, score, boundingBox))

                    paint.style = Paint.Style.STROKE
                    paint.color = Color.RED
                    canvas.drawRect(boundingBox, paint)
                    paint.style = Paint.Style.FILL
                    paint.color = Color.BLUE
                    canvas.drawText(labelText, locations[x + 1] * w, locations[x] * h, paint)
                }
            }

            if (detectionCount == 0) {
                Toast.makeText(requireContext(), "No objects detected with confidence > 0.5", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Detected $detectionCount objects", Toast.LENGTH_SHORT).show()
            }

            imageView.setImageBitmap(mutableBitmap)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error during object detection: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun fetchObjectInformation(query: String) {
        Thread {
            try {
                val encodedQuery = URLEncoder.encode(query, "UTF-8")
                val urlStr = "https://en.wikipedia.org/api/rest_v1/page/summary/$encodedQuery"
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = connection.inputStream
                    val result = stream.bufferedReader().use { it.readText() }
                    val jsonObj = JSONObject(result)
                    val extract = jsonObj.optString("extract", "No information available.")
                    runOnUiThread {
                        resultText.text = extract
                        textToSpeech.speak(extract, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                } else {
                    runOnUiThread {
                        resultText.text = "Error: $responseCode"
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    resultText.text = "Error fetching information."
                }
            }
        }.start()
    }

    private fun runOnUiThread(action: () -> Unit) {
        activity?.runOnUiThread(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::model.isInitialized) {
            model.close()
        }
        if (::textToSpeech.isInitialized) {
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
            }
            textToSpeech.shutdown()
        }
        _binding = null
    }
} 