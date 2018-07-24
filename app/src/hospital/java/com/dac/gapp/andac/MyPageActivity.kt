package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseHospitalActivity
import com.dac.gapp.andac.model.firebase.HospitalInfo
import kotlinx.android.synthetic.hospital.activity_my_page.*
import kotlinx.android.synthetic.main.row.*


class MyPageActivity : BaseHospitalActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // Check Approval
        showProgressDialog()
        onCheckApproval { isApproval ->
            if(isApproval){
                approvalView.visibility = View.VISIBLE
            } else {
                approvalView.visibility = View.GONE
            }
            hideProgressDialog()
        }

        back.setOnClickListener { finish() }

        logoutBtn.setOnClickListener {

            getAuth()!!.signOut()

            Intent(this@MyPageActivity, LoginActivity::class.java).let{
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(it)

            }

        }

        // Set Profile
        getHospital()?.get()?.addOnSuccessListener { documentSnapshot ->
            val hospitalInfo = documentSnapshot.toObject(HospitalInfo::class.java)
            if (hospitalInfo != null) {
                Glide.with(this@MyPageActivity).load(hospitalInfo.profilePicUrl).into(imgview_thumbnail)
                txtview_title.text = hospitalInfo.name
                txtview_address.text = hospitalInfo.address2
                txtview_phone.text = hospitalInfo.phone
            }
        }

        profile_change.setOnClickListener { startActivity(Intent(this, ChangeProfileActivity::class.java)) }
    }
}
