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
import java.io.File

class CameraImageManager (
    private val context: Context,
    private val imageCapture: ImageCapture = ImageCapture.Builder().build(),
    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)
    ) : ImageManager
{
    private val lifecycleOwner: LifecycleOwner = context as LifecycleOwner

    override fun takeImage() {
        val imageFile = File(context.externalCacheDir, "img.png")
        val outFileOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
        imageCapture.takePicture(outFileOptions, {/* my place for your executor */}, object :
            ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("CameraImageManager", "Image saved")
                                    lifecycleOwner.lifecycleScope.launch {
                        _takenImages.emit(getImage())
                    }
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraImageManager", "Error taking picture", exception)
                }
            }
        )
    }

    override fun getImage(): Bitmap {
        return BitmapFactory.decodeFile(File(context.externalCacheDir, "img.png").absolutePath)
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