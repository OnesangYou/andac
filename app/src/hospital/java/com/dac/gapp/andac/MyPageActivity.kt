package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.base.BaseHospitalActivity
import com.dac.gapp.andac.databinding.ActivityMyPageBinding
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.model.firebase.HospitalInfo
import org.jetbrains.anko.startActivity


class MyPageActivity : BaseHospitalActivity() {

    private lateinit var binding : ActivityMyPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)
        binding = getBinding()

        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText(R.string.mypage)
        setActionBarRightText(R.string.logout)
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
        setOnActionBarRightClickListener(View.OnClickListener { _ ->
            getAuth()!!.signOut()
            Intent(this@MyPageActivity, LoginActivity::class.java).let {
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(it)

            }
        })

        // Set Profile
        getHospital()?.get()?.addOnSuccessListener { documentSnapshot ->
            val hospitalInfo = documentSnapshot.toObject(HospitalInfo::class.java)
            hospitalInfo?.let { hospitalInfo ->
                binding.layoutHospitalInfo.let { layoutHospitalInfo ->
                    layoutHospitalInfo.imgviewThumbnail.loadImageAny(hospitalInfo.run { if (profilePicUrl.isNotEmpty()) profilePicUrl else if (approval) R.drawable.hospital_profile_default_approval else R.drawable.hospital_profile_default_not_approval })
                    layoutHospitalInfo.txtviewTitle.text = hospitalInfo.name
                    layoutHospitalInfo.txtviewAddress.text = hospitalInfo.address2
                    layoutHospitalInfo.ratingBar.rating = hospitalInfo.rateAvg
                }

                // check approval
                if (!hospitalInfo.approval) {
                    toast(hospitalInfo.requestStr.toString())
                }
            }
        }

        binding.profileChange.setOnClickListener { startActivity(Intent(this, ChangeProfileActivity::class.java)) }

        binding.passwordChange.setOnClickListener { findPassword() }

        // 버전 정보
        binding.versionText.setOnClickListener { toastVersion() }

        // 공지
        binding.noticeBtn.setOnClickListener { startActivity<NoticeActivity>() }

        // 문의
        binding.questionText.setOnClickListener { sendMail() }
    }
}
