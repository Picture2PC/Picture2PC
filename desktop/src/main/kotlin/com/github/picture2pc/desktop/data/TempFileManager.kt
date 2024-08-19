package com.github.picture2pc.desktop.data

import com.github.picture2pc.desktop.data.Variables.PATH_TO_TEMP_FOLDER
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.DosFileAttributeView
import kotlin.io.path.deleteIfExists

object Variables {
    val PATH_TO_TEMP_FOLDER: Path = Paths.get("tmp")
}

var tempImageHash = 0

fun setHidden(path: Path){
    Files.getFileAttributeView(path, DosFileAttributeView::class.java).setHidden(true)
}

fun makeTempDir(){
    Files.createDirectories(PATH_TO_TEMP_FOLDER)
    setHidden(PATH_TO_TEMP_FOLDER)
}

fun deleteTempImage(){
    PATH_TO_TEMP_FOLDER.resolve("$tempImageHash.png").deleteIfExists()
}