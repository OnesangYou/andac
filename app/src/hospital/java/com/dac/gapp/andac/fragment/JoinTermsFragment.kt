package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.hospital.fragment_join_terms.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class JoinTermsFragment : JoinBaseFragment() {
    override fun onChangeFragment() {
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_terms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn.setOnClickListener {

            // 필수 체크 검사
            if(Arrays.asList(checkedTextView,checkedTextView3, checkedTextView4, checkedTextView5).map { it.isChecked }.all{ it }){
                joinActivity().goToNextView()
            } else {
                context!!.toast("필수 항목을 모두 체크해야 진행할 수 있습니다")
            }

        }

        // 선택 동의 버튼
        checkedAgreeAlarm.setOnCheckedChangeListener { _, b -> joinActivity().hospitalInfo.isAgreeAlarm = b }

        // 전체 선택 버튼
        checkedAll.setOnCheckedChangeListener{ _, b ->
            Arrays.asList(checkedTextView,checkedTextView3, checkedTextView4, checkedTextView5, checkedAgreeAlarm).forEach { it.isChecked = b } }

    }

    private fun joinActivity() = (activity as JoinActivity)


}// Required empty public constructor
