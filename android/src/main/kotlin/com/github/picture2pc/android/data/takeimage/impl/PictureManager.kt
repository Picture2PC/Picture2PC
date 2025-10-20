package com.github.picture2pc.android.data.takeimage.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import com.github.picture2pc.android.data.edgedetection.DetectedBox
import com.github.picture2pc.android.data.edgedetection.EdgeDetect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream

class PictureManager(
    private val coroutineScope: CoroutineScope,
    private val edgeDetect: EdgeDetect,
    private val context: Context
) {
    private val _takenImages =
        MutableSharedFlow<Pair<Bitmap, Deferred<DetectedBox?>>>(replay = 3) //read and write
    val takenImages: SharedFlow<Pair<Bitmap, Deferred<DetectedBox?>>> =
        _takenImages.asSharedFlow()  //read only

    private val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = false
        inSampleSize = calculateInSampleSize(this)
    }
    private val loaded = coroutineScope.launch { edgeDetect.load(context) }

    suspend fun emitPicture(pictureArray: ByteArray) {
        var picture = BitmapFactory.decodeByteArray(pictureArray, 0, pictureArray.size, options)
        picture = rotateImageIfRequired(picture, pictureArray)
        val cJob = coroutineScope.async {
            val res = runDetection(picture)
            return@async res
        }
        cJob.start()
        _takenImages.emit(Pair(picture, cJob))
    }

    private fun rotateImageIfRequired(image: Bitmap, imageData: ByteArray): Bitmap {
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

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    suspend fun runDetection(image: Bitmap): DetectedBox? {
        loaded.join()
        return edgeDetect.detect(image).filter { it.points.size >= 4 }
            .minByOrNull { it.points.size }
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