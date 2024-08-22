package com.github.picture2pc.desktop.viewmodel.picturedisplayviewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.picture2pc.desktop.data.RotationState
import com.github.picture2pc.desktop.data.imageprep.PicturePreparation
import com.github.picture2pc.desktop.extention.toPair
import com.github.picture2pc.desktop.extention.translate
import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver
import com.github.picture2pc.desktop.ui.interactionhandler.ClickHandler
import com.github.picture2pc.desktop.ui.interactionhandler.DragHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
    val zoomedBitmap = pP.zoomedBitmap

    val isSelectPicture = mutableStateOf(false)
    val rotationState: MutableState<RotationState> = mutableStateOf(RotationState.ROTATION_0)

    val clickHandler = ClickHandler(rotationState, pP)
    val dragHandler = DragHandler(pP, rotationState.value, clickHandler)

    init {
        pictures.onEach {
            if (totalPictures.value == 0) pP.setOriginalPicture(
                it.toComposeImageBitmap().asSkiaBitmap()
            )
            totalPictures.value = pictures.replayCache.size
        }.launchIn(this)
    }


    fun adjustCurrentPictureIndex(amount: Int) {
        if (pictures.replayCache.isEmpty()) return
        val newIndex = selectedPictureIndex.value + amount
        if (newIndex < 0 || newIndex > pictures.replayCache.size - 1) return

        selectedPictureIndex.value = newIndex
        pP.setOriginalPicture(
            pictures.replayCache[newIndex].toComposeImageBitmap().asSkiaBitmap()
        )
    }

    fun calculateOffset(): Pair<Float, Float> {
        val currentDP = dragHandler.currentDragPoint.value
        val ratio = pP.ratio
        val bound = pP.editedBitmapBound

        val translatedPair = currentDP.toPair().translate(rotationState.value, bound)
        val offsetPair = Pair(
            (translatedPair.first / ratio) - ((bound.width / ratio) / 2),
            (translatedPair.second / ratio) - ((bound.height / ratio) / 2)
        )

        return offsetPair
    }

    fun loadTestImage() {
        var imgNum = "3"
        if (isSelectPicture.value) {
            print("Enter test image num: ")
            imgNum = readln()
        }
        val file = File("common/src/main/res/test_images/${imgNum}.png")
        val bufferedImage = ImageIO.read(file)
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()
        val testImage = makeFromEncoded(imageBytes)

        // Add the test image to the pictures flow
        CoroutineScope(coroutineContext).launch {
            dataReceiver.addPicture(testImage)
        }
    }
}