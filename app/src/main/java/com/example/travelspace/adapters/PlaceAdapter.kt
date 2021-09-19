package com.example.travelspace.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.travelspace.R
import com.example.travelspace.activities.AddPlaceActivity
import com.example.travelspace.activities.MainActivity
import com.example.travelspace.activities.PlaceDetailActivity
import com.example.travelspace.models.Place
import com.example.travelspace.utils.Constants
import kotlinx.android.synthetic.main.card_item.view.*


class PlaceAdapter(
    private val context: Context,
    private val placeArrayList: ArrayList<Place>,
    private val activity: MainActivity
) : PagerAdapter() {


    override fun getCount(): Int {
        return placeArrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.card_item, container, false)
//get data
        val model = placeArrayList[position]
        val title = model.title
        val description = model.description
        val date = model.date
        val image = model.image
        val location = model.location

        view.iv_banner.setImageURI(Uri.parse(image))

        Glide
            .with(activity)//activity
            .load(image)
            .centerCrop()
            .placeholder(R.drawable.detail_screen_image_placeholder)
            .into(view.iv_banner)

        view.tv_banner_title.text = title
        view.tv_banner_description.text=description
        view.tv_date.text=date
        view.tv_location.text=location
        view.setOnClickListener {
             val intent = Intent(context, PlaceDetailActivity::class.java)
            intent.putExtra(Constants.PLACE,model)
            startActivity(context,intent,null)
        }

        container.addView(view, 0)

        return  view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }
}