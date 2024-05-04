package com.github.picture2pc.android.viewmodel.screenselectorviewmodels

import androidx.compose.runtime.mutableStateOf
import com.github.picture2pc.android.ui.main.Screens

class ScreenSelectorViewModel {
    private val currentScreen =  mutableStateOf(Screens.MAIN)

    var value: Screens
            get() = currentScreen.value
            set(newScreen) { currentScreen.value = newScreen }

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