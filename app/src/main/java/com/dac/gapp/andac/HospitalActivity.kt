package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import timber.log.Timber

const val EXTRA_HOSPITAL_INFO = "EXTRA_HOSPITAL_INFO"

class HospitalActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var hospitalInfo: HospitalInfo
    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityHospitalBinding

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
        binding = getBinding()
        binding.activity = this

        hospitalInfo = intent.getSerializableExtra(EXTRA_HOSPITAL_INFO) as HospitalInfo
        prepareUi()
        setupEvents()
    }

    private fun prepareUi() {
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText(hospitalInfo.name)
        setActionBarRightImage(R.drawable.call)
        binding.txtvieName.text = hospitalInfo.name
        binding.txtviewAddress.text = hospitalInfo.run { if (address2.isNotEmpty()) address2 else if (address1.isNotEmpty()) address1 else getString(R.string.no_hospital_addres_entered) }
        binding.txtviewBusinessHours.text = hospitalInfo.run { if (businessHours.isNotEmpty()) businessHours else getString(R.string.no_business_hours_entered) }
        binding.txtviewDescription.text = hospitalInfo.description

        binding.viewPager.adapter = HospitalActivityPagerAdapter(this, supportFragmentManager, ArrayList<Any>().also {
            it.add(hospitalInfo.run { if (profilePicUrl.isNotEmpty()) profilePicUrl else if (isApproval) R.drawable.hospital_profile_default_approval else R.drawable.hospital_profile_default_not_approval })
        })

        val fragmentManager = fragmentManager
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)

        // isLike
        binding.imgviewFavorite.isEnabled = false
        getLikeHospital(hospitalInfo.objectID)?.get()?.addOnSuccessListener {
            binding.imgviewFavorite.isChecked = it.exists()
            binding.imgviewFavorite.isEnabled = true

            // TODO : 병원 좋아요 클릭 리스너 이벤트 달기
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupEvents() {
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        setOnActionBarRightClickListener(View.OnClickListener {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + hospitalInfo.phone)))
        })
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.apply {
            addMarker(MarkerOptions().apply { icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_on)); position(hospitalInfo.getLatLng()) })
            animateCamera(CameraUpdateFactory.newLatLngZoom(hospitalInfo.getLatLng(), 15f), 1, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    Timber.d("animateCamera onFinish()")
                }

                override fun onCancel() {
                    Timber.d("animateCamera onCancel()")
                    animateCamera(CameraUpdateFactory.newLatLngZoom(hospitalInfo.getLatLng(), 15f), 1, this)
                }
            })
            googleMap = this
        }
    }

    fun onClickConsult(view: View) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(hospitalInfo.getLatLng()))
//        startActivity(Intent(applicationContext, RequestSurgeryActivity::class.java).putExtra("isOpen", false).putExtra("documentId", hospitalInfo.documentId).putExtra("hospitalName", hospitalInfo.name))
    }
}
