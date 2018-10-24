package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_join_terms.*
import java.util.*

class JoinTermsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_terms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextBtn.setOnClickListener {

            // 필수 체크 검사
            if(Arrays.asList(checkedTextView,checkedTextView3, checkedTextView4, checkedTextView5).map { it.isChecked }.all{ it }){
                (activity as JoinActivity).apply {
                    mUserInfo.isAgreeAlarm = checkedAgreeAlarm.isChecked
                    goToNextView()
                }

            } else {
                context!!.toast("필수 항목을 모두 체크해야 진행할 수 있습니다")
            }

        }

        // 선택 동의 버튼
        checkedAgreeAlarm.setOnCheckedChangeListener { _, b -> (activity as JoinActivity).mUserInfo.isAgreeAlarm = b }

        // 전체 선택 버튼
        checkedAll.setOnCheckedChangeListener{ _, b ->
            Arrays.asList(checkedTextView,checkedTextView3, checkedTextView4, checkedTextView5, checkedAgreeAlarm).forEach { it.isChecked = b } }
    }

}
