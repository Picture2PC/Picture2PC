package com.github.picture2pc.android.viewmodel.screenselectorviewmodels

import androidx.compose.runtime.mutableStateOf

class ScreenSelectorViewModel {
    private val currentScreen = mutableStateOf(Screens.CAMERA)

    enum class Screens { MAIN, CAMERA, BIG_PICTURE }

    var value: Screens
        get() = currentScreen.value
        set(newScreen) {
            currentScreen.value = newScreen
        }

    fun toCamera() {
        currentScreen.value = Screens.CAMERA
    }

    fun toMain() {
        currentScreen.value = Screens.MAIN
    }

    fun toBigPicture() {
        currentScreen.value = Screens.BIG_PICTURE
    }
}