package com.github.picture2pc.desktop.ui.util

import java.awt.Cursor
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage

fun customCursor(): Cursor {
    val toolkit = Toolkit.getDefaultToolkit()
    val image = BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
    return toolkit.createCustomCursor(image, Point(0, 0), "Custom Cursor")
}

//TODO: Implement setCursorPosition (find a way to get the window location)
/*
fun setCursorPosition(position: Pair<Int, Int>) {

    val robot = Robot()

    val x = windowLocation.x + position.first
    val y = windowLocation.y + position.second

    robot.mouseMove(x, y)
}*/
