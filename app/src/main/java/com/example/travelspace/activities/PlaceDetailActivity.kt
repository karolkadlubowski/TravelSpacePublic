package com.example.travelspace.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.travelspace.R
import com.example.travelspace.models.Place
import com.example.travelspace.utils.Constants
import com.google.android.libraries.places.api.Places
import kotlinx.android.synthetic.main.activity_add_place.*
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.card_item.view.*

class PlaceDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setupActionBar()


        var place : Place? = null
        if(intent.hasExtra(Constants.PLACE)) {
            place = intent.getParcelableExtra(Constants.PLACE)

            Glide
                .with(this@PlaceDetailActivity)//activity
                .load(place?.image)
                .centerCrop()
                .placeholder(R.drawable.detail_screen_image_placeholder)
                .into(iv_detail_place_image)

            tv_title.text=place?.title
            tv_description.text=place?.description
            tv_location.text=place?.location
            tv_date.text=place?.date
        }



        btn_view_on_map.setOnClickListener {
            val intent = Intent(this,MapActivity::class.java)
            intent.putExtra(Constants.PLACE,place)
            startActivity(intent)
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_place_detail_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = getString(R.string.place_details)

        }

        toolbar_place_detail_activity.setNavigationOnClickListener { onBackPressed() }


    }
}