package com.github.picture2pc.android.data.galleryimageselection

import android.content.Context
import android.content.Intent
import android.provider.MediaStore

class GalleryManager {

    fun manageGallery(context: Context) {
        openGallery(context)
    }

    private fun openGallery(context: Context) {
        context.startActivity(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
    }
}