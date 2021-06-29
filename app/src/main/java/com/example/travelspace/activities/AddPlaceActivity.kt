package com.example.travelspace.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.travelspace.R
import com.example.travelspace.utils.Constants.CAMERA
import com.example.travelspace.utils.Constants.GALLERY
import com.example.travelspace.utils.Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddPlaceActivity : DexterPhotoOriginActivity(), View.OnClickListener {

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var mLatitude : Double = 0.0
    private var mLongitude: Double = 0.0
    //private var saveImageToInternalStorage : Uri? =null

    private var mSelectedImageFileUri: Uri? = null
    private var mEditImageURL : Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()

        if(!Places.isInitialized()){
            Places.initialize(
                this@AddPlaceActivity,
                resources.getString(R.string.google_maps_api_key)
            )
        }


        updateDateInView()

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        et_date.setOnClickListener(this)

        tv_add_image.setOnClickListener(this)
        et_location.setOnClickListener(this)

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_add_place_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = getString(R.string.add_a_place)

        }

        toolbar_add_place_activity.setNavigationOnClickListener { onBackPressed() }


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()

            }
            R.id.tv_add_image -> {
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
            R.id.et_location -> {
                try {
                    val fields = listOf(
                        Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
                    )

                    val intent = Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN,
                        fields
                    )
                        .build(this@AddPlaceActivity)
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*val status = Autocomplete.getStatusFromIntent(data!!)
        Log.e("PlaceError", status.toString())*/
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if(data != null){
                    val contentUri = data.data
                    try{
                        mSelectedImageFileUri = data.data
                        Glide
                            .with(this@AddPlaceActivity)//activity
                            .load(mSelectedImageFileUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_add_screen_image_placeholder)
                            .into(iv_place_image)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddPlaceActivity,
                            "Picture loading failed!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }else if(requestCode== CAMERA){
                val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap


                mSelectedImageFileUri = getImageUriFromBitmap(this,thumbnail)

                Log.e("Saved image: ", "Path :: $mSelectedImageFileUri")


                Glide
                    .with(this@AddPlaceActivity)//activity
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_add_screen_image_placeholder)
                    .into(iv_place_image)
            }else if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
                val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                et_location.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
            }
        }
    }


    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        et_date.setText(sdf.format(cal.time).toString())
    }

    /*
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try{
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()//klepnięcie tego co w streamie
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
    }
    */



}