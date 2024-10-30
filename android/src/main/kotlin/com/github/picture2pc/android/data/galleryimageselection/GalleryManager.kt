package com.github.picture2pc.android.data.galleryimageselection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf

class GalleryManager(private val context: Context) {
    private val _selectedImageUri = mutableStateOf<Uri?>(null)
    private val selectedImageUri get() = _selectedImageUri.value

    private var galleryLauncher: ActivityResultLauncher<Intent>? = null

    fun registerGalleryLauncher(activity: ComponentActivity) {
        galleryLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                _selectedImageUri.value = result.data?.data
            }
        }
    }

    fun getGalleryImage(): Bitmap? {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher?.launch(intent)
        return selectedImageUri?.let { uri ->
            try {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}