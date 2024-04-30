package com.github.picture2pc.android.data.takeimage.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.github.picture2pc.android.data.takeimage.ImageManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File

class CameraImageManager (
    private val context: Context,
    private val imageCapture: ImageCapture = ImageCapture.Builder().build(),
    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)
    ) : ImageManager
{
    private val lifecycleOwner: LifecycleOwner = context as LifecycleOwner

    init {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
            )
        }, ContextCompat.getMainExecutor(context))
    }

    override fun takeImage() {
        val imageFile = File(context.externalCacheDir, "img.png")
        val outFileOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()
        imageCapture.takePicture(outFileOptions, {/* my place for your executor */}, object :
            ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                }
                override fun onError(exception: ImageCaptureException) {

                }
            }
        )
        _takenImages.tryEmit(getImage())
    }

    override fun getImage(): Bitmap {
        return BitmapFactory.decodeFile(File(context.externalCacheDir, "img.png").absolutePath)
    }

    private val _takenImages = MutableSharedFlow<Bitmap>()                      //Abhören + Schreiben
    override val takenImages: SharedFlow<Bitmap> = _takenImages.asSharedFlow()  //Abhören

    override lateinit var viewFinder: PreviewView
}