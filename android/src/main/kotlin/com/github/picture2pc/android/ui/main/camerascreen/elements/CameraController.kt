package com.github.picture2pc.android.ui.main.camerascreen.elements

import android.content.Context
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

    init {
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview
            )
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePicture() {
        val imgCapture = ImageCapture.Builder().build()
        val outFileOptions = ImageCapture.OutputFileOptions.Builder(File("tmp/img.png")).build()
        
        imgCapture.takePicture(outFileOptions, {}, object : OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                TODO("Not yet implemented")
            }

            override fun onError(exception: ImageCaptureException) {
                TODO("Not yet implemented")
            }
        })
    }
}