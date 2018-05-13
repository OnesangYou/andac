package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.hospital.fragment_join_certi2.*


/**
 * A simple [Fragment] subclass.
 */
class JoinCertiFragment : JoinBaseFragment() {
    override fun onChangeFragment() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_certi, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn.setOnClickListener { (activity as JoinActivity).goToNextView() }
    }

}// Required empty public constructor
