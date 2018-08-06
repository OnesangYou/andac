package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.*
import com.dac.gapp.andac.base.BaseFragment
import kotlinx.android.synthetic.hospital.fragment_main.*
import org.jetbrains.anko.startActivity

class MainFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnConsultingBoard.setOnClickListener { startActivity(Intent(context, RequestSurgeryActivity::class.java)) }
        btnHospitalEventManagement.setOnClickListener { context?.startActivity<HospitalEventListActivity>() }
        btnHospitalAdManagement.setOnClickListener { startActivity(HospitalAdApplicationActivity.createIntentForAdManagement(requireContext())) }
        btnHospitalAdApplication.setOnClickListener { startActivity(HospitalAdApplicationActivity.createIntentForAdApplication(requireContext())) }

        viewColumnsBtn.setOnClickListener { startActivity(Intent(context, ColumnActivity::class.java)) }
        myColumnsBtn.setOnClickListener { context?.startActivity<MyColumnListActivity>() }
    }

}

