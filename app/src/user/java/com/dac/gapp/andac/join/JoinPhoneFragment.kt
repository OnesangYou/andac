package com.dac.gapp.andac.join


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.JoinActivity
import kotlinx.android.synthetic.main.fragment_join_phone.*

/**
 * A simple [Fragment] subclass.
 */
class JoinPhoneFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextBtn.setOnClickListener {
            (activity as JoinActivity).goToNextView(phoneEdit.text.toString(), checkedAgreeAlarm.isChecked)
        }
    }

}// Required empty public constructor
