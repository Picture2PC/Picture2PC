package org.picture2pc.picture2pc.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object Padding {
    val Small = 5.dp
    val Medium = 10.dp
    val Large = 15.dp
    val Container = 20.dp
}

object CornerRadius {
    val Default = RoundedCornerShape(25.dp)
    val Small = RoundedCornerShape(15.dp)
    val RoundedBottomDefault = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)
}

object Outline {
    val Container = 4.dp
    val Thin = 2.5.dp
}

object Spacer {
    val XLarge = 20.dp
    val Large = 15.dp
    val Medium = 10.dp
    val Small = 5.dp
    val XSmall = 2.5.dp
}