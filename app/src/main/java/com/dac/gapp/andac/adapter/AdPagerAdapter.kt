package com.dac.gapp.andac.adapter

import android.app.Activity
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dac.gapp.andac.EventDetailActivity
import com.dac.gapp.andac.enums.Extra
import com.dac.gapp.andac.model.firebase.AdInfo
import org.jetbrains.anko.startActivity

class AdPagerAdapter(val activity: Activity, private val adInfoList: ArrayList<AdInfo>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(container.context)
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.setOnClickListener {
            if (adInfoList[position].eventId.isNotEmpty()) {
                activity.startActivity<EventDetailActivity>(Extra.OBJECT_KEY.name to adInfoList[position].eventId)
            }
        }
        container.addView(imageView)
        Glide.with(activity).load(adInfoList[position].photoUrl).into(imageView)
        return imageView
    }

    override fun getCount(): Int {
        return adInfoList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}