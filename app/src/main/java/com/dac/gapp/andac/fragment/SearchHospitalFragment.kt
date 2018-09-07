package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.HospitalActivity
import com.dac.gapp.andac.HospitalTextSearchActivity
import com.dac.gapp.andac.MyPageActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.SearchHospitalFragmentPagerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.firebase.HospitalInfo
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_search_hospital.*


/**
 * A simple [Fragment] subclass.
 */
class SearchHospitalFragment : BaseFragment() {

    companion object {
        private val mCurrentPositionBehaviorSubject: BehaviorSubject<Int> = BehaviorSubject.createDefault(0)
        fun observeCurrentPosition(): Observable<Int> {
            return mCurrentPositionBehaviorSubject.hide()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_hospital, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        setupEventsOnCreate()
    }

    private fun prepareUi() {
        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.mypage)
            context.setActionBarCenterImage(R.drawable.andac_font)
            context.setActionBarRightImage(R.drawable.finder)

            context.setOnActionBarLeftClickListener(View.OnClickListener {
                // 로그인 상태 체크
                if (getCurrentUser() == null) {
                    goToLogin(true)
                } else {
                    startActivity(Intent(context, MyPageActivity::class.java))
                }
            })
            context.setOnActionBarRightClickListener(View.OnClickListener {
                startActivityForResult(HospitalTextSearchActivity.createIntent(context, true), RequestCode.HOSPITAL_OBJECT_REQUEST.value)
            })
        }
        viewPager.adapter = SearchHospitalFragmentPagerAdapter(context, childFragmentManager)
        viewPager.offscreenPageLimit = 11
        layoutTab.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mCurrentPositionBehaviorSubject.onNext(position)
            }

        })
    }

    private fun setupEventsOnCreate() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == RequestCode.HOSPITAL_OBJECT_REQUEST.value) {
            data?.let {
                (it.getSerializableExtra("hospitalInfo") as HospitalInfo).let { hospitalInfo ->
                    context?.startActivity(HospitalActivity.createIntent(context!!, hospitalInfo))
                }
            }
        }

    }

}


