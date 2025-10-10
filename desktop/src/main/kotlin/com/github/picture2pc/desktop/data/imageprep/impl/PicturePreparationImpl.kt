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
import com.github.picture2pc.desktop.extention.toMat
import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBufferedImage
import org.opencv.core.Core
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
    override var originalBitmap: Bitmap = Bitmap()
    private var _editedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())
    override var editedBitmap: State<Bitmap> = _editedBitmap

    override var ratio: Float = 1f

    override fun contrast() {
        if (editedBitmap.value.isEmpty) return

        val matrix = Mat(3, 3, CvType.CV_32F).apply {
            put(0, 0, 0.0, -1.0, 0.0)
            put(1, 0, -1.0, 5.0, -1.0)
            put(2, 0, 0.0, -1.0, 0.0)
        }

        val mat = editedBitmap.value.toMat()
        val dst = Mat()
        Imgproc.filter2D(mat, mat, -1, matrix, Point(0.0, 0.0))
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGRA2BGR)
        mat.convertTo(mat, CvType.CV_8UC3, 1.9, -80.0)
        Imgproc.bilateralFilter(mat, dst, 10, 75.0, 75.0)
        _editedBitmap.value = dst.toBitmap()
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

    override fun rotate(clockwise: Boolean) {
        val dst = Mat()
        val rotation = if (clockwise) Core.ROTATE_90_CLOCKWISE else Core.ROTATE_90_COUNTERCLOCKWISE
        Core.rotate(editedBitmap.value.toMat(), dst, rotation)
        _editedBitmap.value = dst.toBitmap()
    }

    override fun copy() {
        if (editedBitmap.value.isEmpty) return
        addToClipboard(editedBitmap.value.toBufferedImage())
    }

    override fun calculateRatio(displayPictureSize: Size) {
        if (displayPictureSize == Size(0f, 0f)) return
        ratio = max(
            editedBitmap.value.width.toFloat() / displayPictureSize.width,
            editedBitmap.value.height.toFloat() / displayPictureSize.height
        )
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture
        _editedBitmap.value = originalBitmap
    }
}