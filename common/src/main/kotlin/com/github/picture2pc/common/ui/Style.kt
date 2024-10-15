package com.github.picture2pc.common.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Style {
    object Colors {
        val TEXT = Color(0xFFe9e7ff)
        val BACKGROUND = Color(0xFF161616)
        val PRIMARY = Color(0xFF642bcf)
        val SECONDARY = Color(0xFF280f62)
        val ACCENT = Color(0xFF372469)
        val ERROR = Color(0xFFC83538)

        @Composable
        fun provideButtonColors(
            containerColor: Color,
            contentColor: Color,
            disabledContentColor: Color,
            disabledContainerColor: Color
        ): ButtonColors {
            return ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContentColor = disabledContentColor,
                disabledContainerColor = disabledContainerColor,
            )
        }

        val BUTTON_PRIMARY: ButtonColors
            @Composable get() = provideButtonColors(PRIMARY, TEXT, ACCENT, TEXT)
        val BUTTON_SECONDARY: ButtonColors
            @Composable get() = provideButtonColors(PRIMARY, TEXT, ACCENT, TEXT)

        object States {
            val PENDING = Color(0xFF3557C8)
            val CONNECTED = Color(0xFF2FB86C)

            //val WAITING_FOR_DATA = Color(0xFFC0C02C) // °-°
            val RECEIVING = Color(0xFF3557C8)
            val ERROR_WHILE_RECIEVING = Color(0xFFC83538)
            val ERROR_WHILE_SENDING = Color(0xFFC83538)
            val DISCONNECTED = Color(0xFF000000)
        }
    }

    object Shapes {
        val BUTTON = RoundedCornerShape(10.dp)
        val WINDOW = RoundedCornerShape(12.dp)
        val MOBILE = RoundedCornerShape(25.dp)
    }

    object TextStyles {
        val HEADER1 =
            TextStyle(fontSize = 24.sp, color = Colors.TEXT, fontWeight = FontWeight.Bold)
        val HEADER2 =
            TextStyle(fontSize = 16.sp, color = Colors.TEXT, fontWeight = FontWeight.Bold)
        val NORMAL = TextStyle(fontSize = 16.sp, color = Colors.TEXT)
        val SMALL = TextStyle(fontSize = 14.sp, color = Colors.TEXT)
    }

    object Dimensions {
        object Spacers {
            val SMALL = 5.dp
            val MEDIUM = 8.dp
            val NORMAL = 10.dp
            val LARGE = 15.dp
            val EXTRA_LARGE = 20.dp
        }

        object Heights {
            val DIVIDER = 3.dp
            val BUTTON = 50.dp
        }

        object Borders {
            val BORDER_STANDARD = 3.dp
            val BORDER_THICK = 4.dp
        }

        val StateIndicator = 6.dp
    }
}