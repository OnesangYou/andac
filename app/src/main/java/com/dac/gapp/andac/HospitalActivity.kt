package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.adapter.HospitalActivityPagerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityHospitalBinding
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_hospital.*

const val EXTRA_HOSPITAL_INFO = "EXTRA_HOSPITAL_INFO"

class HospitalActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var hospitalInfo: HospitalInfo
    private lateinit var googleMap: GoogleMap
    private lateinit var binding : ActivityHospitalBinding
    // static method
    companion object {
        fun createIntent(context: Context, hospitalInfo: HospitalInfo?): Intent {
            val intent = Intent(context, HospitalActivity::class.java)
            intent.putExtra(EXTRA_HOSPITAL_INFO, hospitalInfo)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hospital)
        binding.activity = this

        hospitalInfo = intent.getSerializableExtra(EXTRA_HOSPITAL_INFO) as HospitalInfo
        prepareUi()
        setupEvents()
    }

    private fun prepareUi() {
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText(hospitalInfo.name)
        setActionBarRightImage(R.drawable.call)
        txtvieName.text = hospitalInfo.name
        txtviewAddress.text = hospitalInfo.address1
        txtviewBusinessHours.text = hospitalInfo.openDate
        txtviewDescription.text = hospitalInfo.description

        val profilePicUrls = ArrayList<String>().apply {
            if (hospitalInfo.profilePicUrl.isNotEmpty())
                add(hospitalInfo.profilePicUrl)
            else {
                viewPager.setBackgroundResource(R.drawable.defult_pic_1)
            }
        }
        viewPager.adapter = HospitalActivityPagerAdapter(this, supportFragmentManager, profilePicUrls)

        val fragmentManager = fragmentManager
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    private fun setupEvents() {
        setOnActionBarLeftClickListener(View.OnClickListener{
            finish()
        })
        setOnActionBarRightClickListener(View.OnClickListener {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + hospitalInfo.phone)))
        })
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            it.addMarker(
                    MarkerOptions().apply {
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_on))
                        position(hospitalInfo.getLatLng())
                    }
            )
            it.moveCamera(CameraUpdateFactory.newLatLng(hospitalInfo.getLatLng()))
            it.animateCamera(CameraUpdateFactory.zoomTo(15f))
            googleMap = it
        }
    }

    fun onClickConsult(view: View) {
        startActivity(Intent(applicationContext, RequestSurgeryActivity::class.java).putExtra("isOpen",false).putExtra("documentId",hospitalInfo.documentId))
    }
}
