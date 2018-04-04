package com.dac.gapp.andac.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.main.fragment_search_hospital.*
import java.util.*


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
        viewPager.adapter = TestPagerAdapter(activity, fragmentManager)
        layoutTab.setupWithViewPager(viewPager)
    }

    private fun setupEventsOnCreate() {
    }

    class TestPagerAdapter(context: Context?, fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        private var titles: List<String> = Arrays.asList(context!!.getString(R.string.nearby_hospital),
                context.getString(R.string.popularity), context.getString(R.string.seoul),
                context.getString(R.string.gyeonggi), context.getString(R.string.incheon))

        override fun getItem(position: Int): Fragment {
            // TODO fragment 계속 생성하지 않고 기존에 생성된거 유지하도록 변경
            return when (position) {
                0 -> SearchHospitalFragmentForMap()
                else -> SearchHospitalFragmentForList()
            }
        }

        override fun getCount(): Int {
            return titles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}


