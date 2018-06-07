package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.BoardFragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_board.*


/**
 * A simple [Fragment] subclass.
 */
class BoardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()

        fabWriteBoard.setOnClickListener {
            val nextIntent = Intent(context, BoardWriteActivity::class.java)
            startActivity(nextIntent)
        }
    }

    private fun prepareUi() {
        viewPager.adapter = BoardFragmentPagerAdapter(context, childFragmentManager)
        viewPager.offscreenPageLimit = 4
        layoutTab.setupWithViewPager(viewPager)
    }

}


