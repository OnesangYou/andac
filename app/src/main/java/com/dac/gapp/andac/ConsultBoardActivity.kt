package com.dac.gapp.andac

import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.adapter.ConsultBoardFragmentPagerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.Algolia
import kotlinx.android.synthetic.main.activity_consult_board.*
import org.jetbrains.anko.startActivity

class ConsultBoardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consult_board)
        prepareUi()
    }

    private fun prepareUi() {
        setActionBarLeftImage(R.drawable.back)
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
        viewPager.adapter = ConsultBoardFragmentPagerAdapter(this, supportFragmentManager)
        viewPager.offscreenPageLimit = 4
        boardTab.setupWithViewPager(viewPager)

        if(isHospital()){
            setActionBarRightImage(R.drawable.finder)
            setOnActionBarRightClickListener(View.OnClickListener {
                // 게시판 검색
                val fragmentTitle = (viewPager.adapter as ConsultBoardFragmentPagerAdapter).getPageTitle(viewPager.currentItem)
                val index = if(fragmentTitle == getString(R.string.consult_open_board)) Algolia.INDEX_NAME_OPEN_CONSULT.value else Algolia.INDEX_NAME_SELECT_CONSULT.value
                startActivity<ConsultTextSearchActivity>("index" to index)
            })
        } else {
            hideActionBarRight()
        }
    }

}

