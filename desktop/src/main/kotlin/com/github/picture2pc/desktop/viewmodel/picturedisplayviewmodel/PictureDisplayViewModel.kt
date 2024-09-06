package com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.div
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import com.github.picture2pc.desktop.ui.constants.Settings
import com.github.picture2pc.desktop.ui.interactionhandler.MovementHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.skia.Image
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.coroutines.CoroutineContext

class PictureDisplayViewModel(
    private val dataReceiver: DataReceiver,
    val pP: PicturePreparation,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {
    private val pictures = dataReceiver.pictures

    //MutableStateFlow because needs to be updated for Screen
    val totalPictures = MutableStateFlow(0)
    val selectedPictureIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    val currentPicture = pP.editedBitmap
    val overlayPicture = pP.overlayBitmap

    val isSelectPicture = mutableStateOf(false)
    val rotationState: MutableState<RotationState> = mutableStateOf(RotationState.ROTATION_0)

    val movementHandler = MovementHandler(rotationState, pP)

    init {
        pictures.onEach {
            if (totalPictures.value == 0) {
                val wasRotated = pP.setOriginalPicture(it.toComposeImageBitmap().asSkiaBitmap())
                if (wasRotated) rotationState.value = RotationState.ROTATION_270
            }
            totalPictures.value = pictures.replayCache.size
        }.launchIn(this)
    }


    fun adjustCurrentPictureIndex(amount: Int) {
        if (pictures.replayCache.isEmpty()) return
        val newIndex = selectedPictureIndex.value + amount
        if (newIndex < 0 || newIndex > pictures.replayCache.size - 1) return

        selectedPictureIndex.value = newIndex
        pP.setOriginalPicture(pictures.replayCache[newIndex].toComposeImageBitmap().asSkiaBitmap())
    }

    fun calculateOffset(
        rotationState: RotationState
    ): Pair<Float, Float> {
        val ratio = pP.ratio
        val bound = pP.bounds / ratio
        val radius = Settings.ZOOM_DIAMETER.toFloat() / 2

        val currentDP = (movementHandler.currentDragPoint.value / ratio).translate(
            rotationState, bound
        )

        var offsetPair = Pair(
            currentDP.first - (bound.width / 2),
            currentDP.second - (bound.height / 2)
        )

        if (currentDP.first in 0f..radius) {
            offsetPair = Pair(radius - bound.width / 2, offsetPair.second)
        } else if (currentDP.first in bound.width - radius..bound.width) {
            offsetPair = Pair(bound.width / 2 - radius, offsetPair.second)
        }

        if (currentDP.second in 0f..radius) {
            offsetPair = Pair(offsetPair.first, radius - bound.height / 2)
        } else if (currentDP.second in bound.height - radius..bound.height) {
            offsetPair = Pair(offsetPair.first, bound.height / 2 - radius)
        }

        return offsetPair
    }

    private fun openImage(path: String): Image {
        val file = File(path)
        val bufferedImage = ImageIO.read(file)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        return makeFromEncoded(imageBytes)
    }

    fun loadTestImage() {
        var imgNum = "3"
        if (isSelectPicture.value) {
            print("Enter test image num: ")
            imgNum = readln()
        }

        val testImage = openImage("common/src/main/res/test_images/${imgNum}.png")

        // Add the test image to the pictures flow
        CoroutineScope(coroutineContext).launch {
            dataReceiver.addPicture(testImage)
        }
    }
}