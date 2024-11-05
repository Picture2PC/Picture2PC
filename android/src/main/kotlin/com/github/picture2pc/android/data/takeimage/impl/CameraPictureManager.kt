package com.github.picture2pc.android.data.takeimage.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.picture2pc.android.data.edgedetection.DetectedBox
import com.github.picture2pc.android.data.edgedetection.EdgeDetect
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class CameraPictureManager(
    private val context: Context,
    private val edgeDetect: EdgeDetect,
    private val imageCapture: ImageCapture = ImageCapture.Builder()
        .setFlashMode(ImageCapture.FLASH_MODE_OFF)
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build(),
    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(
        context
    )
) : PictureManager {
    private val lifecycleOwner: LifecycleOwner = context as LifecycleOwner
    private val _pictureCorners: MutableStateFlow<DetectedBox?> =
        MutableStateFlow(null) //read and write
    override val pictureCorners: StateFlow<DetectedBox?> = _pictureCorners.asStateFlow()


    override fun switchFlashMode() {
        if (imageCapture.flashMode == FLASH_MODE_AUTO) {
            imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
        } else {
            imageCapture.flashMode = FLASH_MODE_AUTO
        }
    }

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
                    val imageData = outputStream.toByteArray()
                    val image =
                        BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    val rotatedImage = rotateImageIfRequired(image, imageData)
                    lifecycleOwner.lifecycleScope.launch {
                        _takenImages.emit(rotatedImage)
                    }
                }
            }
        )
    }

    override fun setViewFinder(previewView: PreviewView) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val analyzerUseCase = ImageAnalysis.Builder()
                .setOutputImageRotationEnabled(true)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analyzerUseCase.setAnalyzer(ContextCompat.getMainExecutor(context)) { image ->
                val res = edgeDetect.detect(image.toBitmap()).filter { it.points.size >= 4 }.minByOrNull { it.points.size }
                if (res != null)
                    _pictureCorners.value = res
                image.close()
            }
            edgeDetect.load(context)
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture,
                analyzerUseCase
            )
        }, ContextCompat.getMainExecutor(context))
    }

    override fun saveImageToCache() {
        val image = takenImages.replayCache.lastOrNull() ?: return

        val fileUri = File.createTempFile("img.png", ".png", context.externalCacheDir)
        try {
            val outStream = FileOutputStream(fileUri)
            image.compress(Bitmap.CompressFormat.PNG, 50, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: IOException) {
            Log.e("CameraImageManager", "Error saving image to cache", e)
        }
    }

    fun rotateImageIfRequired(image: Bitmap, imageData: ByteArray): Bitmap {
        val exif = ExifInterface(ByteArrayInputStream(imageData))
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(image, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(image, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(image, 270f)
            else -> image
        }
    }

    fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    private val _takenImages =
        MutableSharedFlow<Bitmap>(replay = 3)            //read and write
    override val takenImages: SharedFlow<Bitmap> =
        _takenImages.asSharedFlow()  //read only
}