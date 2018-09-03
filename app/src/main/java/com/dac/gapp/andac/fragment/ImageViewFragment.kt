package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dac.gapp.andac.HospitalActivity
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.main.fragment_image_view.*
import kotlinx.android.synthetic.main.fragment_search_hospital_for_map.*

class ImageViewFragment : Fragment() {

    var imageUrl: String? = null
    var imageResId: Int? = null

    // static method
    companion object {
        fun createForUrl(image: String): ImageViewFragment {
            val f = ImageViewFragment()
            f.imageUrl = image
            val args = Bundle()
            f.arguments = args
            return f
        }

        fun createForResId(image: Int): ImageViewFragment {
            val f = ImageViewFragment()
            f.imageResId = image
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_image_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageUrl?.let {
            Glide.with(this).load(it).into(imageView)
        }
        imageResId?.let {
            imageView.setBackgroundResource(it)
        }
    }
}