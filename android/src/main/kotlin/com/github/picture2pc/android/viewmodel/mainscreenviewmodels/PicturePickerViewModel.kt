package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import android.content.Context
import android.net.Uri
import com.github.picture2pc.android.data.takeimage.impl.PictureManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PicturePickerViewModel(
    private val scope: CoroutineScope,
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
    private val pictureManager: PictureManager,
) {
    fun injectUri(uri: Uri) {
        scope.launch(dispatcher) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                pictureManager.emitPicture(stream.readBytes())
            }
        }
    }
}