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

        val images = ArrayList<Int>()
        images.addAll(arrayOf(R.drawable.heart, R.drawable.heart_fill, R.drawable.heart, R.drawable.heart_fill))
        viewPager.adapter = HospitalActivityPagerAdapter(this, supportFragmentManager, images)

        val fragmentManager = fragmentManager
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    private fun setupEvents() {
        phoneCall.setOnClickListener { v ->
            if(isHospital()) {
                toast("병원계정은 사용할 수 없습니다")
                return@setOnClickListener
            }

            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + hospitalInfo.phone)))
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        val markerOptions = MarkerOptions()
        markerOptions.position(hospitalInfo.getLatLng())
        map!!.addMarker(markerOptions)

        map.moveCamera(CameraUpdateFactory.newLatLng(hospitalInfo.getLatLng()))
        map.animateCamera(CameraUpdateFactory.zoomTo(15f))

        googleMap = map
    }
}
