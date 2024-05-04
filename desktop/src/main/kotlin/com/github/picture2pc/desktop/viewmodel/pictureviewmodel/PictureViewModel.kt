package com.github.picture2pc.desktop.viewmodel.pictureviewmodel

import com.github.picture2pc.desktop.net.datatransmitter.DataReceiver

class PictureViewModel(
    dataReceiver: DataReceiver
) {
    val pictures = dataReceiver.pictures
}