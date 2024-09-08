package com.github.picture2pc.common.ui

const val desktopPath = "icons/desktop/"
const val mobilePath = "icons/mobile/"
const val logoPath = "icons/logo/"

object Icons {
    object Desktop {
        const val CONTRAST = "${desktopPath}contrast.svg"
        const val COPY = "${desktopPath}copy.svg"
        const val CROP = "${desktopPath}crop.svg"
        const val RESET = "${desktopPath}reset.svg"
        const val NEXT_PICTURE = "${desktopPath}nextPicture.svg"
        const val PREVIOUS_PICTURE = "${desktopPath}previousPicture.svg"
        const val ROTATE_RIGHT = "${desktopPath}rotate_right.svg"
        const val ROTATE_LEFT = "${desktopPath}rotate_left.svg"
        const val INFO = "${desktopPath}info.svg"
        const val SLOW = "${desktopPath}trail_length_short.svg"
        const val FAST = "${desktopPath}trail_length.svg"
    }

    object Mobile {
        const val FLASH_AUTO = "${mobilePath}flash_auto.svg"
        const val FLASH_OFF = "${mobilePath}flash_off.svg"
    }

    object Logo {
        const val STANDARD = "${logoPath}app_icon_standard.svg"
    }
}