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
    object Colors{
        val TEXT = Color(0xFFe9e7ff)
        val BACKGROUND = Color(0xFF161616)
        val PRIMARY = Color(0xFF642bcf)
        val SECONDARY = Color(0xFF280f62)
        val ACCENT = Color(0xFF372469)

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
        @Composable get() = provideButtonColors( PRIMARY, TEXT, ACCENT, TEXT )

        val BUTTON_SECONDARY: ButtonColors
        @Composable get() = provideButtonColors( PRIMARY, TEXT, ACCENT, TEXT )
    }

    object Shapes{
        val WINDOW = RoundedCornerShape(12.dp)
        val BUTTON = RoundedCornerShape(10.dp)
    }

    object TextStyles{
        val HEADER = TextStyle(fontSize = 24.sp, color = Colors.TEXT, fontWeight = FontWeight.Bold)
        val NORMAL = TextStyle(fontSize = 16.sp, color = Colors.TEXT)
        val SMALL = TextStyle(fontSize = 14.sp, color = Colors.TEXT)
    }

    object Dimensions{
        object Spacers{
            val SMALL = 5.dp
            val MEDIUM = 8.dp
            val NORMAL = 10.dp
            val LARGE = 15.dp
        }

        object Heights{
            val DIVIDER = 3.dp
            val BUTTON = 40.dp
        }

        object Borders{
            val STANDARD = 2.dp
            val MEDIUM = 3.dp
            val THICK = 4.dp
        }
    }
}