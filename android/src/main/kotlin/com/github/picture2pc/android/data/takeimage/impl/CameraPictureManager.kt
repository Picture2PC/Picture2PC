package com.github.picture2pc.android.data.takeimage.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.Surface.ROTATION_90
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraPictureManager(
    private val context: Context,
    private val imageCapture: ImageCapture = ImageCapture.Builder()
        .setFlashMode(FLASH_MODE_OFF)
        .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
        .setTargetRotation(ROTATION_90)
        .build(),
    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)
) : PictureManager {
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
                    val image = BitmapFactory.decodeByteArray(
                        outputStream.toByteArray(),
                        0,
                        outputStream.size()
                    )
                    lifecycleOwner.lifecycleScope.launch {
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

    override fun saveImageToCache() {
        //TODO: Fix, takenImages is empty when calling the function from BigPicture send button
        val image = takenImages.replayCache.lastOrNull() ?: return

        val fileUri = File(context.externalCacheDir, "img.png")
        try {
            val outStream = FileOutputStream(fileUri)
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: IOException) {
            Log.e("CameraImageManager", "Error saving image to cache", e)
        }
    }

    private val _takenImages = MutableSharedFlow<Bitmap>()                      //listen and write
    override val takenImages: SharedFlow<Bitmap> = _takenImages.asSharedFlow()  //listen only
}