package com.github.picture2pc.android.data.edgedetection.impl

import android.content.Context
import android.graphics.Bitmap
import com.github.picture2pc.android.R
import com.github.picture2pc.android.data.edgedetection.DetectedBox
import com.github.picture2pc.android.data.edgedetection.EdgeDetect
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfFloat
import org.opencv.core.MatOfInt
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.MatOfRect2d
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Rect2d
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.core.times
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class YOLOv8SegEdgeDetect : EdgeDetect {
    private lateinit var documentModel: Net

    private fun loadModel(context: Context): Net {
        val inputStream = context.resources.openRawResource(R.raw.documentdetect)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()

        return Dnn.readNetFromONNX(MatOfByte(*buffer))
    }

    override fun load(context: Context) {
        documentModel = loadModel(context)
    }

    override fun detect(bit: Bitmap): List<DetectedBox> {
        val rgb = Mat()
        Utils.bitmapToMat(bit, rgb)
        val res = preprocess(rgb, 256, 256)
        val blob =
            Dnn.blobFromImage(res.first, 1 / 255.0, Size(256.0, 256.0), Scalar(0.0), true, false)
        val result = mutableListOf<Mat>()
        documentModel.setInput(blob)
        documentModel.forward(result, documentModel.unconnectedOutLayersNames)

        return postprocess(
            result,
            rgb,
            res.second,
            res.third.first,
            res.third.second,
            0.4f,
            0.45f
        )
    }

    private fun preprocess(
        img: Mat,
        modelWidth: Int,
        modelHeight: Int
    ): Triple<Mat, Pair<Double, Double>, Pair<Double, Double>> {
        // Resize and pad input image using letterbox approach
        val shape = Size(img.cols().toDouble(), img.rows().toDouble()) // original image shape
        val newShape = Size(modelWidth.toDouble(), modelHeight.toDouble())
        val r = minOf(newShape.height / shape.height, newShape.width / shape.width)
        val ratio = Pair(r, r)

        val newUnpad = Size(shape.width * r, shape.height * r)
        val padW = (newShape.width - newUnpad.width) / 2
        val padH = (newShape.height - newUnpad.height) / 2

        // Resize the image if necessary
        var resizedImg = img.clone()
        if (shape != newUnpad) {
            Imgproc.resize(
                img,
                resizedImg,
                Size(newUnpad.width, newUnpad.height),
                0.0,
                0.0,
                Imgproc.INTER_LINEAR
            )
        }

        // Padding the image to maintain aspect ratio (letterbox)
        val top = Math.round(padH - 0.1).toInt()
        val bottom = Math.round(padH + 0.1).toInt()
        val left = Math.round(padW - 0.1).toInt()
        val right = Math.round(padW + 0.1).toInt()
        val paddedImg = Mat()
        Core.copyMakeBorder(
            resizedImg,
            paddedImg,
            top,
            bottom,
            left,
            right,
            Core.BORDER_CONSTANT,
            Scalar(114.0, 114.0, 114.0)
        )

        // Convert BGR to RGB and normalize to [0,1]
        Imgproc.cvtColor(paddedImg, paddedImg, Imgproc.COLOR_BGR2RGB)
//        paddedImg.convertTo(paddedImg, CvType.CV_32FC3, 1.0 / 255.0)

        return Triple(paddedImg, ratio, Pair(padW, padH))
    }

    private fun processMask(protos: Mat, mask: Mat, box: Rect2d, imShape: Size): Mat {
        return cropMask(
            scaleMask(
                (protos.reshape(0, intArrayOf(32, 64 * 64)).t() * mask.t()).reshape(
                    0,
                    intArrayOf(64, 64)
                ), imShape, null
            ), box
        )
    }

    fun cropMask(mask: Mat, box: Rect2d): Mat {
        val maskCropped = Mat.zeros(mask.size(), mask.type())
        val roi = Rect(box.x.toInt(), box.y.toInt(), box.width.toInt(), box.height.toInt())
        Mat(mask, roi).copyTo(Mat(maskCropped, roi))
        return maskCropped
    }

    fun scaleMask(masks: Mat, im0Shape: Size, ratioPad: DoubleArray?): Mat {
        val im1Shape = masks.size() // Get the current size of the mask
        val gain: Double
        val padX: Double
        val padY: Double

        if (ratioPad == null) { // calculate from im0_shape
            gain = minOf(
                im1Shape.height / im0Shape.height,
                im1Shape.width / im0Shape.width
            ) // gain = old / new
            padX = (im1Shape.width - im0Shape.width * gain) / 2.0 // width padding
            padY = (im1Shape.height - im0Shape.height * gain) / 2.0 // height padding
        } else {
            padX = ratioPad[0]
            padY = ratioPad[1]
        }
        // Calculate top-left and bottom-right coordinates for cropping
        val top = (padY - 0.1).roundToInt()
        val left = (padX - 0.1).roundToInt()
        val bottom = (im1Shape.height - padY + 0.1).roundToInt()
        val right = (im1Shape.width - padX + 0.1).roundToInt()

        // Ensure masks are at least 2-dimensional
        if (masks.dims() < 2) {
            throw IllegalArgumentException("`len of masks shape` should be 2 or 3, but got ${masks.dims()}")
        }

        // Crop the mask to remove padding
        val croppedMask = Mat(masks, Rect(left, top, right - left, bottom - top))

        val resizedMask = Mat()
        Imgproc.resize(
            croppedMask,
            resizedMask,
            im0Shape,
            0.0,
            0.0,
            Imgproc.INTER_LINEAR
        )

        return resizedMask
    }

    private fun mask2segments(mask: Mat): List<Point> {
        val contours = mutableListOf<MatOfPoint>()
        val hierarchy = Mat()
        mask.convertTo(mask, CvType.CV_8UC1)
        Imgproc.findContours(
            mask,
            contours,
            hierarchy,
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        )
        val contour = contours.maxByOrNull { Imgproc.contourArea(it) } ?: return emptyList()
        val approx = MatOfPoint2f()
        Imgproc.approxPolyDP(
            MatOfPoint2f(*contour.toArray()),
            approx,
            Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true) * 0.01,
            true
        )
        return approx.toList().map { Point(it.x / mask.cols(), it.y / mask.rows()) }
    }

    private fun postprocess(
        preds: MutableList<Mat>,
        im0: Mat,
        ratio: Pair<Double, Double>,
        padW: Double,
        padH: Double,
        confThreshold: Float,
        iouThreshold: Float,
        nm: Int = 32
    ): List<DetectedBox> {
        val x: Mat = preds[0].reshape(0, 37)
        val protos = preds[1]
        // Transpose the prediction matrix
        Core.transposeND(x, MatOfInt(1, 0), x)
        // Filter predictions by confidence threshold
        val filteredX = mutableListOf<Mat>()
        for (i in 0 until x.size(0)) {
            val row = x.row(i)
            val conf = Core.minMaxLoc(row.colRange(4, 5)).maxVal // Max confidence
            if (conf > confThreshold) {
                filteredX.add(row)
            }
        }

        // Merge the columns into one (box, score, cls, nm)
        val mergedX = filteredX.map { row ->
            val box = Rect2d(row[0, 0][0], row[0, 1][0], row[0, 2][0], row[0, 3][0]) // Bounding box
            val maxConf =
                Core.minMaxLoc(row.colRange(4, 5)).maxVal.toFloat() // Score
            val cls = Core.minMaxLoc(row.colRange(4, 4 + x.cols() - nm)).maxLoc // Class index
            val mask = row.colRange(5, 37)
            listOf(box, maxConf, cls, mask)
        }
        // Apply Non-Maximum Suppression (NMS)
        val boxes = MatOfRect2d()
        val confidences = MatOfFloat()
        val indices = MatOfInt()
        for (i in mergedX.indices) {
            boxes.push_back(MatOfRect2d(mergedX[i][0] as Rect2d))
            confidences.push_back(MatOfFloat(mergedX[i][1] as Float))
        }
        Dnn.NMSBoxes(boxes, confidences, confThreshold, iouThreshold, indices)

        val result = mutableListOf<DetectedBox>()
        if (indices.size().area() > 0) {
            for (i in 0 until indices.size().area().toInt()) {
                val idx = indices[i, 0][0].toInt()
                val box = mergedX[idx][0] as Rect2d


                // Convert from cxcywh -> xyxy
                box.x -= (box.width / 2)
                box.y -= (box.height / 2)
                box.width += box.x
                box.height += box.y

                // Rescale bounding boxes from model dimensions to original image
                box.x = ((box.x - padW) / ratio.first)
                box.y = ((box.y - padH) / ratio.second)
                box.width = ((box.width - padW) / ratio.first)
                box.height = ((box.height - padH) / ratio.second)

                // Clip boxes to image boundaries
                box.x = max(0.0, min(box.x, im0.cols() - 1.0))
                box.y = max(0.0, min(box.y, im0.rows() - 1.0))
                box.width = max(0.0, min(box.width, im0.cols() - 1.0))
                box.height = max(0.0, min(box.height, im0.rows() - 1.0))

                box.width -= box.x
                box.height -= box.y
                val mask = processMask(
                    protos.reshape(1, intArrayOf(32, 64, 64)),
                    mergedX[idx][3] as Mat,
                    box,
                    im0.size()
                )

                result.add(
                    DetectedBox(
                        Rect(box.tl(), box.size()),
                        mask2segments(mask),
                        mask,
                    )
                )
            }
        }

        return result
    }
}