package com.github.picture2pc.android.viewmodel.mainscreenviewmodels

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.github.picture2pc.android.data.takeimage.PictureManager
import com.github.picture2pc.android.viewmodel.camerascreenviewmodels.CameraViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PicturePickerViewModel(
    private val scope: CoroutineScope,
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
    private val pictureManager: PictureManager,
    private val viewModel: CameraViewModel
) {

    fun processAndSendUris(uris: List<Uri>) {
        scope.launch(dispatcher) {
            for (uri in uris) {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                }

                options.inSampleSize = calculateInSampleSize(options)
                options.inJustDecodeBounds = false

                val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                }

                if (bitmap != null) {
                    pictureManager.emitPicture(bitmap)
                    val emitted = pictureManager.takenImages.first { (bmp, _) -> bmp === bitmap }
                    emitted.second.await()
                    viewModel.sendImage()
                }
            }
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