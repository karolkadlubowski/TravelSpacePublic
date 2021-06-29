package com.example.travelspace.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.travelspace.R
import com.example.travelspace.adapters.PlaceAdapter
import com.example.travelspace.firebase.FirestoreClass
import com.example.travelspace.models.Place
import com.example.travelspace.models.User
import com.example.travelspace.utils.Constants
import com.example.travelspace.utils.Constants.MY_PROFILE_REQUEST_CODE
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    //private lateinit var  actionBar: ActionBar


    private lateinit var placesList: ArrayList<Place>
    private lateinit var placeAdapter: PlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        fab_create_place.setOnClickListener {
            startActivity(Intent(this, AddPlaceActivity::class.java))
        }

        setupActionBar()

        nav_view.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this)

        // actionBar = this.supportActionBar!!
        loadCards()

        //add page change listener
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

    fun updateNavigationUserDetails(user: User) {
        Glide
            .with(this)//activity
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(nav_user_image)

        tv_username.text = user.name
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            doubleBackToExit()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(
                    Intent(this, EditProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }

            R.id.nav_my_profile -> {
                Toast.makeText(this@MainActivity, "My Profile", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)

        return true
    }

    private fun loadCards() {
        //init list
        placesList = ArrayList()
        //add data

        //placesList.add(Place("ID1","Gory", Uri.fromFile(R.drawable.ic_splash_background),"Test","May 30, 2021"))
        //placesList.add(Place("Zielno","Test","May 30, 2021", R.drawable.ic_green_background))

        //set adapter
        placeAdapter = PlaceAdapter(this, placesList)
        viewPager.adapter = placeAdapter
        viewPager.setPadding(100, 0, 100, 0)
    }

}