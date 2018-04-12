package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.user.UserJoinActivity
import kotlinx.android.synthetic.main.fragment_join_info.*


/**
 * A simple [Fragment] subclass.
 */
class JoinInfoFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_join_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        joinBtn.setOnClickListener { (activity as UserJoinActivity).join(emailEdit.text.toString(), nickNameEdit.text.toString()) }
    }

}// Required empty public constructor
