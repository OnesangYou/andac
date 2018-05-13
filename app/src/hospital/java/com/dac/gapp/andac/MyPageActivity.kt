package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.base.BaseHospitalActivity
import com.dac.gapp.andac.base.BaseMyPageActivity
import kotlinx.android.synthetic.hospital.activity_my_page.*

class MyPageActivity : BaseHospitalActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // Check Approval
        showProgressDialog()
        onCheckApproval { isApproval ->
            if(isApproval){
                approvalView.visibility = View.VISIBLE
                waitApprovalView.visibility = View.INVISIBLE
            } else {
                approvalView.visibility = View.INVISIBLE
                waitApprovalView.visibility = View.VISIBLE
            }
            hideProgressDialog()
        }

        back.setOnClickListener({ finish() })

        logoutBtn.setOnClickListener {

            getAuth()!!.signOut()

            Intent(this@MyPageActivity, LoginActivity::class.java).let{
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(it)

            }

        }
    }
}
