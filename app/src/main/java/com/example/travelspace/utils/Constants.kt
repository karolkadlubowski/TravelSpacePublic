package com.example.travelspace.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val PLACES: String = "places"
    const val USERS: String = "users"
    const val PLACE: String = "place"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val GALLERY = 1
    const val CAMERA = 2
    const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    const val MY_PROFILE_REQUEST_CODE : Int = 11
    const val CREATE_PLACE_REQUEST_CODE : Int = 12
    const val ASSIGNED_TO : String = "assignedTo"

    fun getFileExtension(activity: Activity, uri : Uri?) : String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }


}