package com.example.mada_2

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mada_2.databinding.ActivityNewCameraBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit


class NewCamera : AppCompatActivity() {
    private lateinit var viewBinding: ActivityNewCameraBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "start onCreate")
        super.onCreate(savedInstanceState)
        isCameraPermissionGranted()
        isWriteStoragePermissionGranted()
        isReadStoragePermissionGranted()
        setContentView(R.layout.activity_new_camera)
        viewBinding = ActivityNewCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        //viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        Log.d(TAG, "start takePhoto")
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
                .Builder(contentResolver,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues)
                .build()
        val activity = this
        //ImageCapture.OutputFileOptions.Builder(File("/image.jpg")).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun
                            onImageSaved(output: ImageCapture.OutputFileResults) {
                        val msg = "Photo capture succeeded: ${output.savedUri}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)
                        Log.d("MLDebug", "before ML")
                        //val result : String = ML().getPrediction(name, activity) //output.savedUri!!)
                        getPrediction(name)
                        Log.d("MLDebug", "after ML")
                        //finishActivity(result)
                    }
                }
        )
    }

    private fun finishActivity(result: String) {
        val intent = intent
        val id = intent.getIntExtra("id", 1)
        intent.putExtra("meter", result)
        intent.putExtra("id", id)
        setResult(RESULT_OK, intent)
        finish()
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                        //it.setSurfaceProvider(findViewById<androidx.camera.view.PreviewView>(R.id.viewFinder).surfaceProvider)
                    }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun isWriteStoragePermissionGranted(): Boolean {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
        ) {
            Log.v("PhotoDebug", "Permission is granted")
            true
        } else {
            Log.v("PhotoDebug", "Permission is revoked")
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
            )
            false
        }
        /*}
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PhotoDebug","Permission is granted");
            return true;
        }*/
    }

    fun isReadStoragePermissionGranted(): Boolean {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
        ) {
            Log.v("PhotoDebug", "Permission is granted")
            true
        } else {
            Log.v("PhotoDebug", "Permission is revoked")
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
            )
            false
        }
        /*}
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PhotoDebug","Permission is granted");
            return true;
        }*/
    }

    fun isCameraPermissionGranted(): Boolean {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        ) {
            Log.v("PhotoDebug", "Permission is granted")
            true
        } else {
            Log.v("PhotoDebug", "Permission is revoked")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            false
        }
        /*}
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PhotoDebug","Permission is granted");
            return true;
        }*/
    }

    companion object {
        private const val TAG = "PhotoDebug"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
                mutableListOf(
                        Manifest.permission.CAMERA/*,
                        Manifest.permission.RECORD_AUDIO*/
                ).apply {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }
                }.toTypedArray()
    }

    fun getPrediction(name: String): String {
        Log.d("MLDebug", "start getPrediction")
        val client = OkHttpClient().newBuilder()
                .build()
        Log.d("MLDebug", "in getPrediction: 1")
        val mediaType: MediaType = "text/plain".toMediaType()
        Log.d("MLDebug", "in getPrediction: 2")
        //val data = File(imageUri.path!!)
        val data = File(Environment.getExternalStorageDirectory().absoluteFile,
                "Pictures/CameraX-Image/" + name + ".jpg");
        //val data = File("/storage/emulated/0/Pictures/CameraX-Image/1.jpg")

        Log.d("MLDebug", "in getPrediction: 2.5")
        val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file", "${name}.jpg",
                        RequestBody.create(
                                "application/octet-stream".toMediaType(),
                                data
                        )
                )
                .build()

        Log.d("MLDebug", "in getPrediction: 3")
        val request: Request = Request.Builder()
                //.url("http://10.171.75.90:8000/")
                .url("http://10.171.75.90:8000/recognize_meters_data")
                .method("POST", body)
                .build()
        Log.d("MLDebug", "in getPrediction: 4")
        //val response = client.newCall(request).execute()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("MLDebug", "Exception: " + e)
                call.cancel()
                finishActivity("0000000")
            }

            override fun onResponse(call: Call, response: Response) {
                val str: String = response.body!!.string()
                Log.d("MLDebug", str)
                finishActivity(str)
            }
        });
        Log.d("MLDebug", "end getPrediction")
        //return response.body!!.string()
        return "2"
    }
}