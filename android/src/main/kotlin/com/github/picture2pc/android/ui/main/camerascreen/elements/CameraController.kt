package com.github.picture2pc.android.ui.main.camerascreen.elements

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File


class CameraController(context: Context, lifecycleOwner: LifecycleOwner) {
    lateinit var viewFinder: PreviewView
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    private lateinit var imageCapture: ImageCapture

    init {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
            )
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePicture(context: Context) {
        val imageDir = File(context.getExternalFilesDir(null), "tmp")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }

        val imageFile = File(context.externalCacheDir, "img.png")
        val outFileOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
        imageCapture.takePicture(outFileOptions, {/* my place for your executor */}, object : OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

            }
            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraController", "Error saving image: ${exception.message}")
            }
        })
    }
}