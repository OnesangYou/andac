package com.dac.gapp.andac

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.dac.gapp.andac.adapter.HospitalActivityPagerAdapter
import com.dac.gapp.andac.fragment.GoogleMapCallback
import com.google.android.gms.maps.MapFragment
import kotlinx.android.synthetic.main.activity_hospital.*


const val EXTRA_HOSPITAL_ID = "EXTRA_HOSPITAL_ID"

class HospitalActivity : AppCompatActivity() {

    // static method
    companion object {
        fun createIntent(context: Context, hospitalId: Int): Intent {
            var intent = Intent(context, HospitalActivity::class.java)
            intent.putExtra(EXTRA_HOSPITAL_ID, hospitalId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital)
        prepareUi()
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

        val hospitalId = intent.getIntExtra(EXTRA_HOSPITAL_ID, 0)
        txtviewTitle.text = "title $hospitalId"

        val images = ArrayList<Int>()
        images.addAll(arrayOf(R.drawable.heart, R.drawable.heart_fill, R.drawable.heart, R.drawable.heart_fill))
        viewPager.adapter = HospitalActivityPagerAdapter(this, supportFragmentManager, images)

        val fragmentManager = fragmentManager
        val mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(GoogleMapCallback())
    }
}
