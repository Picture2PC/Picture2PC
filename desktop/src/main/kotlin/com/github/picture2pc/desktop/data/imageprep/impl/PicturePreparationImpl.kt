package com.github.picture2pc.desktop.data.imageprep.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.github.picture2pc.desktop.data.addToClipboard
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.data.imageprep.constants.Paints
import com.github.picture2pc.desktop.data.imageprep.constants.UnstratifiedValues
import com.github.picture2pc.desktop.extention.toBitmap
import com.github.picture2pc.desktop.extention.toImage
import com.github.picture2pc.desktop.extention.toMat
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix
import org.jetbrains.skia.IRect
import org.jetbrains.skia.ImageInfo
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Path
import org.jetbrains.skiko.toBufferedImage
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
import androidx.compose.ui.geometry.Rect as MathRect
import org.jetbrains.skia.Point as SkPoint

class PicturePreparationImpl(
    override val coroutineContext: CoroutineContext
) : PicturePreparation, CoroutineScope {
    //Bitmaps for the original, edited, overlay and drag overlay images
    override var originalBitmap: Bitmap = Bitmap()

    private var _editedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())
    override var editedBitmap: State<Bitmap> = _editedBitmap

    private var _overlayBitmap: MutableState<Bitmap> = mutableStateOf(clearBitmap())
    override var overlayBitmap: State<Bitmap> = _overlayBitmap

    override var zoomedBitmap: MutableState<Bitmap> = mutableStateOf(Bitmap())

    //Important other variables
    override var ratio: Float = 1f
    override var editedBitmapBound = MathRect(Offset(0f, 0f), 0f)

    override val clicks: MutableList<SkPoint> = mutableListOf()

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

        Canvas(_editedBitmap.value.makeClone())
            .drawImage(editedBitmap.value.toImage(), 0f, 0f, paint)
        updateEditedBitmap()
    }

    override fun crop() {
        if (clicks.size != 4) return
        if (editedBitmap.value.isEmpty) return

        val tl = clicks[0] // Top Left
        val tr = clicks[1] // Top Right
        val br = clicks[2] // Bottom Right
        val bl = clicks[3] // Bottom Left

        val widthA = sqrt((tr.x - tl.x).pow(2) + (tr.y - tl.y).pow(2))
        val widthB = sqrt((br.x - bl.x).pow(2) + (br.y - bl.y).pow(2))
        val maxWidth = max(widthA, widthB).toDouble()

        val heightA = sqrt((tr.x - br.x).pow(2) + (tr.y - br.y).pow(2))
        val heightB = sqrt((tl.x - bl.x).pow(2) + (tl.y - bl.y).pow(2))
        val maxHeight = max(heightA, heightB).toDouble()

        val mat = editedBitmap.value.toMat()
        val dst = Mat(Size(maxWidth, maxHeight), CvType.CV_8UC3)

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
        Imgproc.warpPerspective(mat, dst, perspectiveTransform, Size(maxWidth, maxHeight))

        _editedBitmap.value = dst.toBitmap()
        _overlayBitmap.value = clearBitmap()
        updateEditedBitmap()
        reset(resetEditedBitmap = false, resetClicks = false, resetDragOverlay = false)
    }

    override fun copy() {
        if (editedBitmap.value.isEmpty) return
        addToClipboard(editedBitmap.value.toBufferedImage())
    }

    override fun reset(
        resetEditedBitmap: Boolean,
        resetClicks: Boolean,
        resetOverlay: Boolean,
        resetDragOverlay: Boolean
    ) {
        if (resetEditedBitmap) _editedBitmap.value = originalBitmap
        if (resetClicks) clicks.clear()
        if (resetOverlay) _overlayBitmap.value = clearBitmap()
        updateEditedBitmap()
    }

    override fun calculateRatio(displayPictureSize: IntSize) {
        ratio = editedBitmap.value.width.toFloat() / displayPictureSize.width.toFloat()
    }

    override fun updateEditedBitmap() {
        _editedBitmap.value = editedBitmap.value.makeClone()
        editedBitmapBound = MathRect(
            Offset(0f, 0f),
            Offset(editedBitmap.value.width.toFloat(), editedBitmap.value.height.toFloat())
        )
    }

    private fun clearBitmap(): Bitmap {
        val bitmap = Bitmap().apply {
            allocPixels(
                ImageInfo.makeN32(
                    editedBitmap.value.width, editedBitmap.value.height, ColorAlphaType.UNPREMUL
                )
            )
            erase(Color.TRANSPARENT)
        }

        return bitmap
    }

    override fun setDisplayedZoomedBitmap(point: SkPoint) {
        val x = point.x.toInt()
        val y = point.y.toInt()
        val radius = 70

        val rect = IRect.makeLTRB(
            x - radius,
            y - radius,
            x + radius,
            y + radius
        )

        editedBitmap.value.extractSubset(zoomedBitmap.value, rect)
    }

    override fun setOriginalPicture(picture: Bitmap) {
        originalBitmap = picture
        editedBitmapBound = MathRect(
            Offset(0f, 0f),
            Offset(picture.width.toFloat(), picture.height.toFloat())
        )
        reset()
    }

    /*private fun sortPoint(corners: Array<Point>): Array<Point> {
        val tr: Point
        val tl: Point
        val bl: Point
        val br: Point

        val f = ratio.pow(-1)

        val sum = IntArray(corners.size)
        val diff = IntArray(corners.size)

        for (i in corners.indices) {
            sum[i] = (corners[i].x + corners[i].y).toInt()
            diff[i] = (corners[i].x - corners[i].y).toInt()
        }

        println(corners.joinToString(""))

        tl = corners[sum.indexOf(sum.minOrNull()!!)]
        br = corners[sum.indexOf(sum.maxOrNull()!!)]

        tr = corners[diff.indexOf(diff.maxOrNull()!!)]
        bl = corners[diff.indexOf(diff.minOrNull()!!)]

        tl.x += (tl.x * f - tl.x).toInt()
        tl.y += (tl.y * f - tl.y).toInt()
        tr.x += (tr.x * f - tr.x).toInt()
        tr.y += (tr.y * f - tr.y).toInt()
        bl.x += (bl.x * f - bl.x).toInt()
        bl.y += (bl.y * f - bl.y).toInt()
        br.x += (br.x * f - br.x).toInt()
        br.y += (br.y * f - br.y).toInt()

        return arrayOf(tl, tr, bl, br)
    }

    override fun getCorners(): MutableList<Array<Point>>? {
        if (editedBitmap.value.isEmpty) return null
        reset()

        val listOfCorners = mutableListOf(arrayOf(Point(), Point(), Point(), Point()))

        val mat = editedBitmap.value.toMat()
        val blur = Mat()
        val gray = Mat()
        val canny = Mat()

        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(gray, blur, Size(5.0, 5.0), 1.0)
        Imgproc.adaptiveThreshold(
            blur,
            canny,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,
            69,
            0.0,
        )

        val allContours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(
            canny,
            allContours,
            Mat(),
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_NONE
        )

        val contours = mutableListOf<MatOfPoint2f>()
        for (contour in allContours) {
            if (Imgproc.contourArea(contour) > 10000 * ratio) {
                contours.add(MatOfPoint2f(*contour.toArray()))
            }
        }

        for (contour in contours) {
            val approx = MatOfPoint2f()
            Imgproc.approxPolyDP(contour, approx, Imgproc.arcLength(contour, true) * 0.02, true)
            val corners = approx.toArray()

            if (corners.size in 4..10) {
                listOfCorners.add(sortPoint(corners))
            }
        }

        listOfCorners.sortBy { Imgproc.contourArea(MatOfPoint(*it)) }
        listOfCorners.reverse()
        println(Imgproc.contourArea(MatOfPoint(*listOfCorners[0])))
        println(Imgproc.contourArea(MatOfPoint(*listOfCorners[1])))

        for (corners in listOfCorners) {
            for (corner in corners) {
                Imgproc.drawMarker(
                    mat,
                    corner,
                    Scalar(0.0, 0.0, 255.0),
                    Imgproc.MARKER_CROSS,
                    20,
                    2
                )
            }
        }
        return listOfCorners
    }*/

    override fun drawPolygon(points: List<SkPoint>, paint: Paint) {
        if (points.isEmpty()) throw IllegalArgumentException("At least one point is required")
        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }.closePath()
        Canvas(_overlayBitmap.value).drawPath(path, paint)
        updateEditedBitmap()
    }

    override fun drawCircle(point: SkPoint, filled: Boolean) {
        val canvas = Canvas(_overlayBitmap.value)
        canvas.drawCircle(
            point.x,
            point.y,
            UnstratifiedValues.CIRCLE_RADIUS_STROKE * ratio,
            Paints.stroke.apply { strokeWidth = UnstratifiedValues.STROKE_WIDTH * ratio })
        if (filled) canvas.drawCircle(
            point.x,
            point.y,
            UnstratifiedValues.CIRCLE_RADIUS_FILL * ratio,
            Paints.fill
        )
        updateEditedBitmap()
    }
}