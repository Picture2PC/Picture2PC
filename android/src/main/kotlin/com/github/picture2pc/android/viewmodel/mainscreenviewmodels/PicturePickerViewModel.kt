package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PicturePickerViewModel(
    private val scope: CoroutineScope,
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
    private val cameraViewModel: CameraViewModel,
) {
    private val bitmap = mutableStateOf<Bitmap?>(null)

    fun injectUri(uri: Uri){
        scope.launch(dispatcher) {
            bitmap.value =
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    Bitmap.createBitmap(BitmapFactory.decodeStream(stream))
                }
            bitmap.value?.let { cameraViewModel.injectImage(it) }
        }
    }
}