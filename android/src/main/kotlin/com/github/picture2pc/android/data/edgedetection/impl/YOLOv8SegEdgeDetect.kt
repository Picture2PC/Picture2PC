package com.github.picture2pc.android.data.edgedetection.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import com.github.picture2pc.android.R
import com.github.picture2pc.android.data.edgedetection.EdgeDetect
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfFloat
import org.opencv.core.MatOfInt
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfRect2d
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.dnn.Dnn
import org.opencv.dnn.Net
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.min

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

    override fun detect(bit: Bitmap): List<Point> {
        val rgb = Mat()
        Utils.bitmapToMat(bit, rgb)
        Imgproc.cvtColor(rgb, rgb, Imgproc.COLOR_RGBA2RGB)
        val res = preprocess(rgb, 256, 256)
        val blob = Dnn.blobFromImage(rgb, 1 / 255.0, Size(256.0, 256.0), Scalar(0.0), true, false)
        val result = mutableListOf<Mat>()
        documentModel.setInput(blob)
        documentModel.forward(result, documentModel.unconnectedOutLayersNames)

        val (boxes, masks, _) = postprocess(
            result,
            rgb,
            res.second,
            res.third.first,
            res.third.second,
            0.3f,
            0.5f
        )
        println(boxes)
        return emptyList()
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
        paddedImg.convertTo(paddedImg, CvType.CV_32FC3, 1.0 / 255.0)

        // Transpose HWC to CHW (channel first format)
        val channels = mutableListOf<Mat>()
        Core.split(paddedImg, channels)
        val chwImg = Mat()
        Core.merge(channels, chwImg)

        return Triple(chwImg, ratio, Pair(padW, padH))
    }


    fun postprocess(
        preds: MutableList<Mat>,
        im0: Mat,
        ratio: Pair<Double, Double>,
        padW: Double,
        padH: Double,
        confThreshold: Float,
        iouThreshold: Float,
        nm: Int = 32
    ): Triple<List<Rect>, List<MatOfPoint>, Mat> {
        val x = preds[0]
        val protos = preds[1]

        // Transpose the prediction matrix
        Core.transposeND(x, MatOfInt(0, 2, 1), x)
        // Filter predictions by confidence threshold
        val filteredX = mutableListOf<Mat>()
        for (i in 0 until x.size(0)) {
            val row = x.row(i)
            println(row)
            val conf = Core.minMaxLoc(row.colRange(4, 4 + x.cols() - nm)).maxVal // Max confidence
            if (conf > confThreshold) {
                filteredX.add(row)
            }
        }

        // Merge the columns into one (box, score, cls, nm)
        val mergedX = filteredX.map { row ->
            val box = row.submat(0, 4, 0, 1)
            val maxConf = Core.minMaxLoc(row.colRange(4, 4 + x.cols() - nm)).maxVal // Score
            val cls = Core.minMaxLoc(row.colRange(4, 4 + x.cols() - nm)).maxLoc // Class index
            val mask = row.colRange(x.cols() - nm, x.cols())
            listOf(box, maxConf, cls, mask)
        }
        // Apply Non-Maximum Suppression (NMS)
        val boxes = MatOfRect2d()
        val confidences = MatOfFloat()
        val indices = MatOfInt()
        for (i in mergedX.indices) {
            boxes.push_back(mergedX[i][0] as Mat)
            confidences.push_back(mergedX[i][1] as Mat)
        }
        Dnn.NMSBoxes(boxes, confidences, confThreshold, iouThreshold, indices)

        val outputBoxes = mutableListOf<Rect>()
        val outputMasks = Mat()
        val segments = mutableListOf<MatOfPoint>()
        if (indices.size().area() > 0) {
            for (i in 0 until indices.size().area().toInt()) {
                val idx = indices[intArrayOf(i)][0].toInt()
                val box = mergedX[idx][0] as Rect

                // Convert from cxcywh -> xyxy
                box.x -= (box.width / 2)
                box.y -= (box.height / 2)
                box.width += box.x
                box.height += box.y

                // Rescale bounding boxes from model dimensions to original image
                box.x = ((box.x - padW) / ratio.first).toInt()
                box.y = ((box.y - padH) / ratio.second).toInt()
                box.width = ((box.width - padW) / ratio.first).toInt()
                box.height = ((box.height - padH) / ratio.second).toInt()

                // Clip boxes to image boundaries
                box.x = max(0, min(box.x, im0.cols() - 1))
                box.y = max(0, min(box.y, im0.rows() - 1))
                box.width = max(0, min(box.width, im0.cols() - 1))
                box.height = max(0, min(box.height, im0.rows() - 1))

                outputBoxes.add(box)

                // Process masks (this depends on how you manage masks in Kotlin, assuming a `processMask` function exists)
                val mask = mergedX[idx][3] as Mat
                println(mask)
//                val maskProcessed = processMask(protos.row(0), mask, box, im0.size())
//                segments.add(masks2segments(maskProcessed))
            }
        }

        return Triple(outputBoxes, segments, outputMasks)
    }
}