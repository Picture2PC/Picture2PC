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
        fun provideButtonColors(): ButtonColors {
            return ButtonDefaults.buttonColors(
                containerColor = PRIMARY,
                contentColor = TEXT,
                disabledContentColor = SECONDARY,
                disabledContainerColor = TEXT,
            )
        }

        val BUTTON: ButtonColors
            @Composable
            get() = provideButtonColors()
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
}