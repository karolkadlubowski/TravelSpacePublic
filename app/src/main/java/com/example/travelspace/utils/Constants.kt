package com.example.travelspace.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS: String = "users"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"

    const val GALLERY = 1
    const val CAMERA = 2
    const val IMAGE_DIRECTORY = "TravelSpaceImages"
    const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3

    const val MY_PROFILE_REQUEST_CODE : Int = 11

/*
    const val READ_STORAGE_PERMISSION_CODE = 4
    const val PICK_IMAGE_REQUEST_CODE = 5


    fun showImageChooser(activity: Activity){
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)

    }


    fun getFileExtension(activity: Activity, uri : Uri?) : String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

 */
}