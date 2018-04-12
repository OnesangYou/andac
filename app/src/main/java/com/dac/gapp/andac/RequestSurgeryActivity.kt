package com.dac.gapp.andac

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.dac.gapp.andac.adapter.SurgeryTypeSelectPagerAdapter
import kotlinx.android.synthetic.main.activity_request_surgery.*

class RequestSurgeryActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_surgery)

        val mViewPager: ViewPager = tabViwePager
        mViewPager.adapter = SurgeryTypeSelectPagerAdapter(supportFragmentManager)

        val mTabLayout: TabLayout = tablayout
        mTabLayout.setupWithViewPager(mViewPager)

        mTabLayout.getTabAt(0)!!.setText("오픈형")
        mTabLayout.getTabAt(1)!!.setText("지정형")
    }
}
