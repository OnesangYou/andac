package com.dac.gapp.andac

import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.adapter.ConsultBoardFragmentPagerAdapter
import com.dac.gapp.andac.base.BaseActivity
import kotlinx.android.synthetic.main.activity_consult_board.*

class ConsultBoardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consult_board)
        prepareUi()
    }

    private fun prepareUi() {
        setActionBarLeftImage(R.drawable.back)
        hidActionBarRight()
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
        viewPager.adapter = ConsultBoardFragmentPagerAdapter(this, supportFragmentManager)
        viewPager.offscreenPageLimit = 4
        boardTab.setupWithViewPager(viewPager)
    }

}

