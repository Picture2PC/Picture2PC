package com.github.picture2pc.desktop.data.imageprep.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.github.picture2pc.desktop.data.addToClipboard
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.denormalize
import com.github.picture2pc.desktop.extention.toBitmap
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.extention.toMat
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skiko.toBufferedImage
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import org.opencv.core.Size as CvSize

class PicturePreparationImpl : PicturePreparation {
    //Bitmaps for the original, edited, overlay and drag overlay images
    override var originalBitmap: Bitmap = Bitmap()
    private var _editedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())
    override var editedBitmap: State<Bitmap> = _editedBitmap

    override var ratio: Float = 1f

    override fun contrast() {
        if (editedBitmap.value.isEmpty) return
        val contrast = 1.6f
        val cM = ColorMatrix(
            contrast, 0f, 0f, 0f, 0f,
            0f, contrast, 0f, 0f, 0f,
            0f, 0f, contrast, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        val paint = Paint().apply { colorFilter = ColorFilter.makeMatrix(cM) }

        val bitmap = clearBitmap()
        Canvas(bitmap).drawImage(editedBitmap.value.toImage(), 0f, 0f, paint).close()
        _editedBitmap.value = bitmap
    }

    override fun crop(clicks: List<Offset>, displayPictureSize: Size) {
        if (clicks.size != 4) return
        if (editedBitmap.value.isEmpty) return

        val tl = clicks[0].denormalize(displayPictureSize) * ratio
        val tr = clicks[1].denormalize(displayPictureSize) * ratio
        val br = clicks[2].denormalize(displayPictureSize) * ratio
        val bl = clicks[3].denormalize(displayPictureSize) * ratio

        val widthA = sqrt((tr.x - tl.x).pow(2) + (tr.y - tl.y).pow(2))
        val widthB = sqrt((br.x - bl.x).pow(2) + (br.y - bl.y).pow(2))
        val maxWidth = max(widthA, widthB).toDouble()

        val heightA = sqrt((tr.x - br.x).pow(2) + (tr.y - br.y).pow(2))
        val heightB = sqrt((tl.x - bl.x).pow(2) + (tl.y - bl.y).pow(2))
        val maxHeight = max(heightA, heightB).toDouble()

        val mat = editedBitmap.value.toMat()
        val dst = Mat(CvSize(maxWidth, maxHeight), CvType.CV_8UC3)

        val srcPoints = listOf(
            Point(tl.x.toDouble(), tl.y.toDouble()),
            Point(tr.x.toDouble(), tr.y.toDouble()),
            Point(bl.x.toDouble(), bl.y.toDouble()),
            Point(br.x.toDouble(), br.y.toDouble())
        )
        val dstPoints = listOf(
            Point(0.0, 0.0),
            Point(maxWidth, 0.0),
            Point(0.0, maxHeight),
            Point(maxWidth, maxHeight)
        )

        val perspectiveTransform = Imgproc.getPerspectiveTransform(
            MatOfPoint2f(*srcPoints.toTypedArray()),
            MatOfPoint2f(*dstPoints.toTypedArray())
        )
        Imgproc.warpPerspective(
            mat,
            dst,
            perspectiveTransform,
            CvSize(maxWidth, maxHeight)
        )

        _editedBitmap.value = dst.toBitmap()
    }

    override fun copy() {
        if (editedBitmap.value.isEmpty) return
        addToClipboard(editedBitmap.value.toBufferedImage())
    }

    override fun calculateRatio(displayPictureSize: Size) {
        if (displayPictureSize == Size(0f, 0f)) return
        ratio = editedBitmap.value.width.toFloat() / displayPictureSize.width
    }

    private fun clearBitmap(): Bitmap {
        val bitmap = Bitmap().apply {
            allocPixels(
                ImageInfo.makeN32(
                    editedBitmap.value.width,
                    editedBitmap.value.height,
                    ColorAlphaType.UNPREMUL
                )
            )
            erase(Color.TRANSPARENT)
        }
        return bitmap
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture
        _editedBitmap.value = originalBitmap
    }
}