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

    fun injectUri(uri: Uri) {
        scope.launch(dispatcher) {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }

            options.inSampleSize = calculateInSampleSize(options)
            options.inJustDecodeBounds = false

            bitmap.value = context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, options)
            }
            bitmap.value?.let { cameraViewModel.injectImage(it) }
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int = 3000,
        reqHeight: Int = 4000
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}