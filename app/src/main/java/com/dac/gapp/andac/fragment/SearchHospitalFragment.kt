package com.dac.gapp.andac.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dac.gapp.andac.HospitalActivity
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.main.fragment_search_hospital.*


/**
 * A simple [Fragment] subclass.
 */
class SearchHospitalFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_hospital, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        setupEventsOnCreate()
    }

    private fun prepareUi() {
        viewPager.adapter = TestPagerAdapter(fragmentManager)
        layout_tab.setupWithViewPager(viewPager)
    }

    private fun setupEventsOnCreate() {
        btn_hospital.setOnClickListener({
            val nextIntent = Intent(context, HospitalActivity::class.java)
            startActivity(nextIntent)
        })
    }

    class TestPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
//            when (position) {
//                0 -> Toast.makeText(Activity(), "0", Toast.LENGTH_SHORT).show()
//                1 -> Toast.makeText(Activity(), "1", Toast.LENGTH_SHORT).show()
//                2 -> Toast.makeText(Activity(), "2", Toast.LENGTH_SHORT).show()
//            }
            return Fragment()
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "첫번째 탭"
                1 -> return "두번째 탭"
                2 -> return "세번째 탭"
            }
            return null
        }
    }

}// Required empty public constructor
