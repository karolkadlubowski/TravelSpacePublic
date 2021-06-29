package com.example.travelspace.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.travelspace.R
import com.example.travelspace.firebase.FirestoreClass
import com.example.travelspace.models.User
import com.example.travelspace.utils.Constants
import com.example.travelspace.utils.Constants.CAMERA
import com.example.travelspace.utils.Constants.GALLERY
import com.example.travelspace.utils.Constants.IMAGE_DIRECTORY
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.*
import java.util.*
import kotlin.collections.HashMap


class EditProfileActivity : DexterPhotoOriginActivity(), View.OnClickListener {

    //private var saveImageToInternalStorage : Uri? =null
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails : User
    private var mProfileImageURL : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        FirestoreClass().loadUserData(this)

        iv_edit_profile_user_image.setOnClickListener(this)

        btn_edit_profile_update.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))

            if(mSelectedImageFileUri != null){

                uploadUserImage()
            }else{

                updateUserProfileData()
            }
        }
/*
        iv_edit_profile_user_image.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){

            }else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_STORAGE_PERMISSION_CODE
                )
            }
        }

 */
    }

    /*
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if(requestCode == READ_STORAGE_PERMISSION_CODE){
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
            }
        }
    */
    fun setUserDataInUI(user: User) {
        mUserDetails = user

        Glide
            .with(this@EditProfileActivity)//activity
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_edit_profile_user_image)

        et_edit_profile_name.setText(user.name)
        et_edit_profile_email.setText(user.email)
        if (user.mobile != 0L)
            et_edit_profile_mobile.setText(user.mobile.toString())
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_edit_profile_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.edit_profile)
        }

        toolbar_edit_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_edit_profile_user_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from Gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    //val contentURI = data.data
                    try {
                        mSelectedImageFileUri = data.data
                        /*
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)

                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)

                        Log.e("Saved image: ", "Path :: $saveImageToInternalStorage")

                        iv_edit_profile_user_image.setImageBitmap(selectedImageBitmap)

                         */

                        Glide
                            .with(this@EditProfileActivity)//activity
                            .load(mSelectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user_place_holder)
                            .into(iv_edit_profile_user_image)


                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@EditProfileActivity,
                            "Failed to load the Image from Gallery",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else if (requestCode == CAMERA) {
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap

                //mSelectedImageFileUri = saveImageToInternalStorage(thumbnail)

                mSelectedImageFileUri = getImageUriFromBitmap(this,thumbnail)

                Log.e("Saved image: ", "Path :: $mSelectedImageFileUri")


                Glide
                    .with(this@EditProfileActivity)//activity
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(iv_edit_profile_user_image)



            }
        }
    }










    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()
        var anyChangesMade = false

        if(mProfileImageURL.isNotEmpty() && mProfileImageURL!= mUserDetails.image){
            //userHashMap["image"]
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChangesMade=true
        }
        if(et_edit_profile_name.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = et_edit_profile_name.text.toString()
            anyChangesMade=true
        }

        if(et_edit_profile_mobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = et_edit_profile_mobile.text.toString().toLong()
            anyChangesMade=true
        }
        if(anyChangesMade) {

            FirestoreClass().updateUserProfileData(this, userHashMap)

        }else
            hideProgressDialog()


    }

    private fun uploadUserImage() {


        if (mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance()
                .reference.child(
                    "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                        mSelectedImageFileUri
                    )
                )
            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener{ taskSnapshot ->
                Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.i(
                        "Firebase Image URL",
                        uri.toString()
                    )
                    mProfileImageURL = uri.toString()

                        updateUserProfileData()
                }
            }.addOnFailureListener{ exception ->
                Toast.makeText(
                    this@EditProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}