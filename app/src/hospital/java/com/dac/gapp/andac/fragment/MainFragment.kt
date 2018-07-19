package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.ColumnActivity
import com.dac.gapp.andac.ColumnWriteActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.RequestSurgeryActivity
import kotlinx.android.synthetic.hospital.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        main_borad_view.setOnClickListener { startActivity(Intent(context, RequestSurgeryActivity::class.java)) }
        viewColumnsBtn.setOnClickListener { startActivity(Intent(context, ColumnActivity::class.java)) }
        writeColumnBtn.setOnClickListener { startActivity(Intent(context, ColumnWriteActivity::class.java)) }
    }

}

