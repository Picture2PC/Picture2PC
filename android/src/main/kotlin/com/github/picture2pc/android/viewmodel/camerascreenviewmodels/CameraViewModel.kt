package com.github.picture2pc.android.viewmodel.camerascreenviewmodels

import android.content.Context
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.picture2pc.android.data.edgedetection.DetectedBox
import com.github.picture2pc.android.data.takeimage.impl.PictureManager
import com.github.picture2pc.android.extentions.toByteArray
import com.github.picture2pc.android.net.datatransmitter.DataTransmitter
import com.github.picture2pc.android.ui.util.FlashStates
import com.github.picture2pc.android.ui.util.next
import com.github.picture2pc.common.net.data.payload.TcpPayload
import com.github.picture2pc.common.ui.notification.NotificationHandler
import com.github.picture2pc.common.ui.notification.NotificationMessages
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CameraViewModel(
    private val pictureManager: PictureManager,
    private val dataTransmitter: DataTransmitter,
    private val notificationHandler: NotificationHandler,
    private val defaultScope: CoroutineScope,
    private val defaultDispatcher: CoroutineDispatcher,
    private val imageCapture: ImageCapture = ImageCapture.Builder()
        .setFlashMode(ImageCapture.FLASH_MODE_OFF)
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build(),
) : ViewModel() {
    val currentPicture = pictureManager.takenImages.map { it.first }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val _flashMode: MutableStateFlow<FlashStates> = MutableStateFlow(FlashStates.FLASH_OFF)
    val flashMode: StateFlow<FlashStates> get() = _flashMode.asStateFlow()

    private val _sendAvailable = MutableStateFlow(true)
    val sendAvailable = _sendAvailable.asStateFlow()

    init {
        pictureManager.takenImages.onEach {
            _refiningCorners.emit(true)
            if (it.second.await() != null)
                _pictureCorners.emit(it.second.await())
            _refiningCorners.emit(false)
        }.launchIn(viewModelScope)
    }

    private val _previewCorners = MutableStateFlow<DetectedBox?>(null)
    val previewCorners = _previewCorners.asStateFlow()


    private val _pictureCorners = MutableStateFlow<DetectedBox?>(null)
    val pictureCorners = _pictureCorners.asStateFlow()

    private val _refiningCorners = MutableStateFlow(false)
    val refiningCorners = _refiningCorners.asStateFlow() // TODO: Show in UI

    @OptIn(ExperimentalCoroutinesApi::class)
    private val singleThreadContext = defaultDispatcher.limitedParallelism(1)

    fun setViewFinder(context: Context, previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val analyzerUseCase = ImageAnalysis.Builder()
                .setOutputImageRotationEnabled(true)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            val scope = CoroutineScope(singleThreadContext)
            analyzerUseCase.setAnalyzer(singleThreadContext.asExecutor()) { image ->
                scope.launch {
                    val res = pictureManager.runDetection(image.toBitmap())
                    if (res != null) _previewCorners.value = res
                }.invokeOnCompletion {
                    image.close()
                }
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture,
                analyzerUseCase
            )
        }, ContextCompat.getMainExecutor(context))
    }

    fun takeImage() {
        val outputStream = ByteArrayOutputStream()
        val options = ImageCapture.OutputFileOptions.Builder(outputStream).build()
        imageCapture.takePicture(
            options,
            defaultDispatcher.asExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraImageManager", "Error taking picture", exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    defaultScope.launch {
                        _pictureCorners.emit(previewCorners.value)
                        pictureManager.emitPicture(outputStream.toByteArray())
                    }
                }
            }
        )
    }


    fun sendImage() {
        if (currentPicture.value == null || !sendAvailable.value) return
        _sendAvailable.value = false
        viewModelScope.launch {
            val success = dataTransmitter.sendPicture(
                TcpPayload.Picture(
                    currentPicture.value!!.toByteArray(),
                    pictureCorners.value?.toFloatPair()
                )
            )
            _sendAvailable.value = true
            val message =
                if (success) NotificationMessages.PICTURE_SENT_SUCCESSFULLY
                else NotificationMessages.PICTURE_NOT_SENT
            notificationHandler.displayNotification(NotificationMessages.TITLE, message, true)
        }
    }

    fun switchFlashMode() {
        if (imageCapture.flashMode == FLASH_MODE_AUTO) {
            imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
        } else {
            imageCapture.flashMode = FLASH_MODE_AUTO
        }
        _flashMode.value = flashMode.value.next()
    }

    /*fun saveImageToCache() {

        val image = pictureManager.takenImages.replayCache.lastOrNull()?.first ?: return

        val fileUri = File.createTempFile("img.png", ".png", context.externalCacheDir)
        try {
            val outStream = FileOutputStream(fileUri)
            image.compress(Bitmap.CompressFormat.PNG, 50, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: IOException) {
            Log.e("CameraImageManager", "Error saving image to cache", e)
        }
    }*/
}