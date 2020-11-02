package com.dac.gapp.andac.fragment

import android.os.Bundle
import androidx.core.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.extension.loadImageAny
import kotlinx.android.synthetic.main.fragment_image_view.*

class ImageViewFragment : Fragment() {

    var image: Any? = null

    // static method
    companion object {
        fun create(image: Any): ImageViewFragment {
            val f = ImageViewFragment()
            f.image = image
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
        image?.let {
            imageView.loadImageAny(it)
        }
    }
}