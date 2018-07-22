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

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Get the ActionBar here to configure the way it behaves.
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        viewPager.adapter = ConsultBoardFragmentPagerAdapter(this, supportFragmentManager)
        boardTab.setupWithViewPager(viewPager)
    }

}

