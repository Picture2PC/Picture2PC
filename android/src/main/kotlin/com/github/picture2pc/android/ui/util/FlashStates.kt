package com.github.picture2pc.android.ui.util

import com.github.picture2pc.android.R
import com.github.picture2pc.common.ui.Icons

enum class FlashStates(val resourceInt: Int, val path: String) {
    FLASH_AUTO(R.drawable.flash_auto, Icons.Mobile.FLASH_AUTO),
    FLASH_OFF(R.drawable.flash_off, Icons.Mobile.FLASH_OFF),
}

fun FlashStates.next(): FlashStates {
    return when (this) {
        FlashStates.FLASH_AUTO -> FlashStates.FLASH_OFF
        FlashStates.FLASH_OFF -> FlashStates.FLASH_AUTO
    }
}