package com.dac.gapp.andac

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.Toolbar
import android.view.View
import com.dac.gapp.andac.adapter.ConsultBoardFragmentPagerAdapter
import com.dac.gapp.andac.base.BaseActivity
import kotlinx.android.synthetic.hospital.activity_consult_board.*

class ConsultBoardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consult_board)
        prepareUi()
    }

    private fun prepareUi() {
        viewPager.adapter = ConsultBoardFragmentPagerAdapter(this, supportFragmentManager)
        viewPager.offscreenPageLimit = 4
        boardTab.setupWithViewPager(viewPager)
    }

}

