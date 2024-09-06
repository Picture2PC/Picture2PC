package com.github.picture2pc.common.ui


object Icons {
    private const val DESKTOP_PATH = "icons/desktop/"
    private const val MOBILE_PATH = "icons/mobile/"

    const val LOGO = "icons/app_icon_standard.svg"
    const val DARK_MODE = "icons/dark_mode.svg"
    const val LIGHT_MODE = "icons/light_mode.svg"

    object Desktop {
        const val CONTRAST = "${DESKTOP_PATH}contrast.svg"
        const val COPY = "${DESKTOP_PATH}copy.svg"
        const val CROP = "${DESKTOP_PATH}crop.svg"
        const val RESET = "${DESKTOP_PATH}reset.svg"
        const val NEXT_PICTURE = "${DESKTOP_PATH}nextPicture.svg"
        const val PREVIOUS_PICTURE = "${DESKTOP_PATH}previousPicture.svg"
        const val ROTATE_RIGHT = "${DESKTOP_PATH}rotate_right.svg"
        const val ROTATE_LEFT = "${DESKTOP_PATH}rotate_left.svg"
        const val INFO = "${DESKTOP_PATH}info.svg"
        const val SLOW = "${DESKTOP_PATH}trail_length_short.svg"
        const val FAST = "${DESKTOP_PATH}trail_length.svg"
    }

    object Mobile {
        const val FLASH_AUTO = "${MOBILE_PATH}flash_auto.svg"
        const val FLASH_OFF = "${MOBILE_PATH}flash_off.svg"
    }
}