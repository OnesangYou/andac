package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentJoinFinishBinding


/**
 * A simple [Fragment] subclass.
 */
class JoinFinishFragment : BaseFragment() {

    lateinit var binding: FragmentJoinFinishBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflate(inflater, R.layout.fragment_join_finish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getBinding()

        binding.mainBtn.setOnClickListener {
            context?.finish()
        }

    }

}// Required empty public constructor
