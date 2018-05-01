package com.dac.gapp.andac.join


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dac.gapp.andac.HospitalJoinActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.hospital.SearchAddressActivity
import kotlinx.android.synthetic.hospital.fragment_hospital_join_info.*

/**
 * A simple [Fragment] subclass.
 */
class HospitalJoinInfoFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hospital_join_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn.setOnClickListener {
            val hospitalJoinActivity = activity as HospitalJoinActivity

            // 유효성 검사
            val hospitalStr = hospitalName.text.toString()
            val addressStr = addressEdit.text.toString()

            // 병원명 공백체크
            if(hospitalStr == ""){
                Toast.makeText(context, "병원명이 공백입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 주소 공백체크
            if(addressStr == ""){
                Toast.makeText(context, "병원주소가 공백입니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            hospitalJoinActivity.hospitalInfo.apply{
                title = hospitalStr
                address2 = addressStr
            }

            hospitalJoinActivity.goToNextView()
        }

        // 주소검색
        addressEdit.setOnClickListener {
            Intent(context, SearchAddressActivity::class.java).let {
                startActivity(it)
            }
        }

    }

}// Required empty public constructor
