package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import com.dac.gapp.andac.model.firebase.HospitalInfo
import kotlinx.android.synthetic.main.activity_board_write.*


class BoardWriteActivity : com.dac.gapp.andac.base.BaseActivity() {

    private val HOSPITAL_OBJECT_REQUEST = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_write)

        hospital_search.setOnClickListener {
            // 병원 검색
            Intent(this@BoardWriteActivity, HospitalTextSearchActivity::class.java).let {
                startActivityForResult(it, HOSPITAL_OBJECT_REQUEST)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == HOSPITAL_OBJECT_REQUEST) {
            data?.let{
                (it.getSerializableExtra("hospitalInfo") as HospitalInfo).let { hospitalInfo ->
                    hospital_search.setText(hospitalInfo.name)
                }
            }
        }

    }
}
