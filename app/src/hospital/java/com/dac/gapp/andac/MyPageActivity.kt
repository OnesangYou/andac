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
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText(R.string.mypage)
        setActionBarRightText(R.string.logout)
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
        setOnActionBarRightClickListener(View.OnClickListener {
            getAuth()!!.signOut()
            Intent(this@MyPageActivity, LoginActivity::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(it)

            }
        })

        // Set Profile
        getHospital()?.get()?.addOnSuccessListener { documentSnapshot ->
            val hospitalInfo = documentSnapshot.toObject(HospitalInfo::class.java)
            if (hospitalInfo != null) {
                Glide.with(this@MyPageActivity).load(hospitalInfo.profilePicUrl).into(imgview_thumbnail)
                txtview_title.text = hospitalInfo.name
                txtview_address.text = hospitalInfo.address2
                txtview_phone.text = hospitalInfo.phone

                // check approval
                if(!hospitalInfo.isApproval){
                    hospitalInfo.requestStr?.let {
                        toast(it)
                    }
                }
            }
        }

        profile_change.setOnClickListener { startActivity(Intent(this, ChangeProfileActivity::class.java)) }
    }
}
