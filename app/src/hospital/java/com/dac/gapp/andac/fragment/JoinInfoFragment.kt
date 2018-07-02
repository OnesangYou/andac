package com.dac.gapp.andac.fragment


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.SearchAddressActivity
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.hospital.fragment_join_info.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class JoinInfoFragment : JoinBaseFragment() {

    private val PICK_ADDRESS_REQUEST: Int = 0

    override fun onChangeFragment() {
        // 받아온 경우 Set
        getJoinActivity().hospitalInfo.apply {
            if(name.isNotEmpty()) hospitalName.setText(name)
            if(address2.isNotEmpty()) addressEdit.setText(address2)
            if(phone.isNotEmpty()) phoneEdit.setText(phone)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadProfilePicBtn.setOnClickListener{
            // Image File 가져옴
            context?.startAlbumImageUri()?.addOnSuccessListener{albumFile ->
                getJoinActivity().profilePicUri = Uri.fromFile(File(albumFile.path))
            }
        }


        nextBtn.setOnClickListener {
            getJoinActivity().run{
                // 유효성 검사
                val hospitalStr = hospitalName.text.toString()
                val addressStr = addressEdit.text.toString()

                // 병원명 공백체크
                if(hospitalStr == ""){
                    context!!.toast("병원명이 공백입니다")
                    return@setOnClickListener
                }
                // 주소 공백체크
                if(addressStr == ""){
                    context!!.toast("병원주소가 공백입니다")
                    return@setOnClickListener
                }

                hospitalInfo.apply{
                    name = hospitalStr
                    address2 = addressStr
                }

                goToNextView()
            }
        }

        // 주소 검색
        View.OnClickListener {
            Intent(context, SearchAddressActivity::class.java).let {
                startActivityForResult(it, PICK_ADDRESS_REQUEST)
            }
        }.let {
            addressEdit.setOnClickListener(it)
            hospitalName.setOnClickListener(it)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check which request it is that we're responding to
        if (requestCode == PICK_ADDRESS_REQUEST && resultCode == RESULT_OK) {
            // Get the URI that points to the selected contact
            data?.let {
                val latLng = data.getParcelableExtra<LatLng>("latLng")
                val name = data.getStringExtra("name")
                val address = data.getStringExtra("address")
                val phoneNumber = data.getStringExtra("phoneNumber")

//                context!!.toast("latLng : $latLng, name : $name")

                getJoinActivity().apply{
                    hospitalName.setText(name)
                    addressEdit.setText(address)
                    phoneEdit.setText(phoneNumber)
                    hospitalInfo._geoloc.apply {
                        lat = latLng.latitude
                        lng = latLng.longitude
                    }
                }
            }
        }
    }

}// Required empty public constructor
