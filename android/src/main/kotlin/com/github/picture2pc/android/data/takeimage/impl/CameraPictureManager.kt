package com.github.picture2pc.android.data.takeimage.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.github.picture2pc.android.data.edgedetection.EdgeDetect
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
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
                    val image = BitmapFactory.decodeByteArray(
                        outputStream.toByteArray(),
                        0,
                        outputStream.size()
                    )
                    lifecycleOwner.lifecycleScope.launch {
                        _takenImages.emit(image)
                    }
                }
            }
        )
    }

    fun addWeightedToGreen(
        mat: Mat,
        rgb: Mat,
        alpha: Double = 0.3,
        beta: Double = 0.7,
        gamma: Double = 0.0
    ): Mat {
        // Check if both matrices have the same size
        if (mat.size() != rgb.size()) {
            throw IllegalArgumentException("mat and rgb must have the same size")
        }

        // Split the RGB channels
        val channels = mutableListOf<Mat>()
        Core.split(rgb, channels) // channels[0] = blue, channels[1] = green, channels[2] = red

        val matConverted = Mat()
        mat.convertTo(matConverted, CvType.CV_8UC1, 255.0)
        // Add weighted mat to the green channel
        println("mat: $matConverted channel[1]: ${channels[1]}")
        Core.addWeighted(
            matConverted,
            alpha,
            channels[1],
            beta,
            gamma,
            channels[1]
        ) // Modify the green channel

        // Merge the channels back into the RGB image
        val result = Mat()
        Core.merge(channels, result)

        // Release the individual channel matrices to avoid memory leaks
        channels.forEach { it.release() }

        return result
    }

    override fun setViewFinder(previewView: PreviewView) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val analyzerUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analyzerUseCase.setAnalyzer(ContextCompat.getMainExecutor(context)) { image ->
                val b = image.toBitmap()
                var rgb = Mat()
                Utils.bitmapToMat(b, rgb)
                val mat = edgeDetect.detect(b)
                if (mat.empty()) {
                    image.close()
                    return@setAnalyzer
                }

                lifecycleOwner.lifecycleScope.launch {
                    mat.convertTo(mat, CvType.CV_8UC1, 255.0)
                    Utils.matToBitmap(mat, b)
                    _takenImages.emit(b)
                }
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

    private val _takenImages = MutableSharedFlow<Bitmap>(replay = 3)            //read and write
    override val takenImages: SharedFlow<Bitmap> = _takenImages.asSharedFlow()  //read only
}