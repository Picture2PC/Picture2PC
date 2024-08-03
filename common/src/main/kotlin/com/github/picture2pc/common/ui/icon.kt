package com.github.picture2pc.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource

object Icons {
    object Desktop {
        const val CONTRAST = "icons/desktop/contrast.svg"
        const val COPY = "icons/desktop/copy.svg"
        const val CROP = "icons/desktop/crop.svg"
        const val RESET = "icons/desktop/reset.svg"
        const val NEXT_PICTURE = "icons/desktop/nextPicture.svg"
        const val PREVIOUS_PICTURE = "icons/desktop/previousPicture.svg"
    }

    object Mobile {
        const val FLASH_ON = "icons/mobile/flash_on.svg"
        const val FLASH_AUTO = "icons/mobile/flash_auto.svg"
        const val FLASH_OFF = "icons/mobile/flash_off.svg"
    }

    object Logo {
        const val STANDARD = "icons/logo/app_icon_standard.svg"
        const val NO_BACKGROUND = "icons/logo/app_icon_no_bg.svg"
        const val FILLED = "icons/logo/app_icon_filled.svg"
        const val FILLED_DESKTOP_SMALL = "icons/logo/app_icon_filled_desktop_s.svg"
        const val FILLED_SMALL = "icons/logo/app_icon_filled_s.svg"
    }
}

@Composable
fun getIcon(path: String) = painterResource(path)