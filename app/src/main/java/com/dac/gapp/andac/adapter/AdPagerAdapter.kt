package com.dac.gapp.andac.adapter

import android.app.Activity
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dac.gapp.andac.EventDetailActivity
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.enums.AdCountType
import com.dac.gapp.andac.enums.Extra
import com.dac.gapp.andac.model.firebase.AdInfo
import org.jetbrains.anko.startActivity

class AdPagerAdapter(val activity: BaseActivity, private val adInfoList: ArrayList<AdInfo>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(container.context)
        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        adInfoList[position].let { adInfo ->
            imageView.setOnClickListener {
//                if (adInfoList[position].eventId.isNotEmpty()) {
//                    activity.startActivity<EventDetailActivity>(Extra.OBJECT_KEY.name to adInfo.eventId)
//                    activity.addCountAdClick(adInfo.hospitalId, AdCountType.BANNER)
//                }
            }
            container.addView(imageView)
            Glide.with(activity).load(adInfo.photoUrl).into(imageView)
        }
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