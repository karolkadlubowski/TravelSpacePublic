package com.example.travelspace.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import com.example.travelspace.R
import com.example.travelspace.models.Place
import kotlinx.android.synthetic.main.card_item.view.*

class PlaceAdapter(private val context: Context, private val placeArrayList: ArrayList<Place>) : PagerAdapter() {
    override fun getCount(): Int {
        return placeArrayList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.card_item,container,false)
//get data
        val model = placeArrayList[position]
        val title = model.title
        val description = model.description
        val date = model.date
        val image = model.image

        // set data to ui views
        view.iv_banner.setImageURI(Uri.parse(image))
        view.tv_banner_title.text = title
        view.tv_banner_description.text=description
        view.tv_date.text=date
//handle item/card click
        view.setOnClickListener {
            Toast.makeText(context,"$title \n $description \n $date", Toast.LENGTH_SHORT).show()
        }

        container.addView(view,position)

        return  view
        //return super.instantiateItem(container, position)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object` as View)
    }




}