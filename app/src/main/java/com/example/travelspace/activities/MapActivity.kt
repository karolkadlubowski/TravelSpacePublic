package com.example.travelspace.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.travelspace.R
import com.example.travelspace.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import kotlinx.android.synthetic.main.activity_map.*

//wlacz mape sdk a nie placz
class MapActivity : AppCompatActivity(),OnMapReadyCallback {
    private var mPlaceDetails : com.example.travelspace.models.Place? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        if(intent.hasExtra(Constants.PLACE)){
            mPlaceDetails = intent.getParcelableExtra(Constants.PLACE)!!
        }

        if(mPlaceDetails != null){
            setSupportActionBar(toolbar_map)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = mPlaceDetails!!.title

            toolbar_map.setNavigationOnClickListener {
                onBackPressed()
            }

            val supportMapFragment:SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            supportMapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val position = LatLng(mPlaceDetails!!.latitude, mPlaceDetails!!.longitude)
        googleMap!!.addMarker(MarkerOptions().position(position).title(mPlaceDetails!!.location))
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 9f)
        googleMap.animateCamera(newLatLngZoom)
    }


}