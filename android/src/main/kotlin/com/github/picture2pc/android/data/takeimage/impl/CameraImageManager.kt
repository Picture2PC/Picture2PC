package com.github.picture2pc.android.data.takeimage.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.picture2pc.android.data.takeimage.ImageManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CameraImageManager(
    private val context: Context,
    private val imageCapture: ImageCapture = ImageCapture.Builder()
        .setFlashMode(ImageCapture.FLASH_MODE_OFF).build(),
    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)
) : ImageManager
{
    private val lifecycleOwner: LifecycleOwner = context as LifecycleOwner


    override fun takeImage() {
        val outputStream = ByteArrayOutputStream()
        val options = ImageCapture.OutputFileOptions.Builder(outputStream).build()
        imageCapture.takePicture(
            options,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraImageManager", "Error taking picture", exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("CameraImageManager", "Image saved")
                    val image = BitmapFactory.decodeByteArray(
                        outputStream.toByteArray(),
                        0,
                        outputStream.size()
                    )
                    Log.d("CameraImageManager", "Image size: ${image.byteCount}")
                    lifecycleOwner.lifecycleScope.launch {
                        Log.d("CameraImageManager", "Emitting image")
                        _takenImages.emit(image)
                    }
                }
            })
    }

    override fun setViewFinder(previewView:PreviewView){
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
            )
        }, ContextCompat.getMainExecutor(context))
    }

    private val _takenImages = MutableSharedFlow<Bitmap>()                      //Abhören + Schreiben
    override val takenImages: SharedFlow<Bitmap> = _takenImages.asSharedFlow()  //Abhören

}

