package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.OnTabSelectedListener
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.LoginActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.BoardFragmentPagerAdapter
import com.dac.gapp.andac.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_board.*
import com.dac.gapp.andac.R.id.viewPager
import kotlinx.coroutines.experimental.selects.select


/**
 * A simple [Fragment] subclass.
 */
class BoardFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()

        context?.apply {
            if(isUser()) fabWriteBoard.setOnClickListener { _ ->
                getCurrentUser()?.let{ goToLogin() }
                ?:startActivity(Intent(context, BoardWriteActivity::class.java))
            }
            else {
                fabWriteBoard.visibility = View.INVISIBLE
            }
        }

    }

    private fun prepareUi() {


        viewPager.adapter = BoardFragmentPagerAdapter(context, childFragmentManager)
        viewPager.offscreenPageLimit = 4
        layoutTab.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.d("ITPANGPANG", "onPageScrolled : $position")
            }

            override fun onPageSelected(position: Int) {
                (viewPager.adapter as BoardFragmentPagerAdapter).getItem(position).getData()

                Log.d("ITPANGPANG", "onPageSelected : $position")
            }

            override fun onPageScrollStateChanged(state: Int) {
                Log.d("ITPANGPANG", "onPageScrollStateChanged : $state")
            }
        })

    }

}


