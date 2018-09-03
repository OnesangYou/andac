package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.dac.gapp.andac.adapter.HospitalActivityPagerAdapter
import com.dac.gapp.andac.base.BaseActivity
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
        setContentView(R.layout.activity_hospital)
        hospitalInfo = intent.getSerializableExtra(EXTRA_HOSPITAL_INFO) as HospitalInfo
        prepareUi()
        setupEvents()
        back.setOnClickListener { finish() }
    }

    private fun prepareUi() {
        // toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Get the ActionBar here to configure the way it behaves.
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        txtviewTitle.text = hospitalInfo.name
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
        phoneCall.setOnClickListener { v ->
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + hospitalInfo.phone)))
        }
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
}
