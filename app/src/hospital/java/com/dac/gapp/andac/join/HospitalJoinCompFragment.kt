package com.dac.gapp.andac.join


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.MainActivity
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.hospital.fragment_hospital_join_comp.*


/**
 * A simple [Fragment] subclass.
 */
class HospitalJoinCompFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hospital_join_comp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn.setOnClickListener { startActivity(Intent(context, MainActivity::class.java)) }
    }

}// Required empty public constructor
