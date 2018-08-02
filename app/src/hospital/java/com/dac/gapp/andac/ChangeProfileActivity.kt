package com.dac.gapp.andac

import android.os.Bundle
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.hospital.activity_change_profile.*

class ChangeProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile)

        var hospitalInfo: HospitalInfo? = null

        // Hospital Info 출력
        showProgressDialog()
        getHospitalInfo()?.addOnSuccessListener {it?.apply {
            Glide.with(this@ChangeProfileActivity).load(profilePicUrl).into(pictureImage)
            hospitalText.setText(name)
            phoneText.setText(phone)
            addressText.setText(address2)
            openHourText.setText(openDate)
            descriptionText.setText(description)
        }
                .apply { hospitalInfo = this@apply }}
                ?.addOnCompleteListener {
                    hideProgressDialog()
                    setOnClickListener(hospitalInfo)
                }

        completeBtn.setOnClickListener {
            hospitalInfo?.apply {
                name = hospitalText.text.toString()
                phone = phoneText.text.toString()
                address2 = addressText.text.toString()
                openDate = openHourText.text.toString()
                description = descriptionText.text.toString()
            }
                    ?.let {
                        showProgressDialog()
                        getHospital()?.set(it)
                    }
                    ?.addOnCompleteListener { hideProgressDialog() }
                    ?.addOnSuccessListener { toast("프로필 변경 완료되었습니다"); finish() }
        }
    }

    private fun setOnClickListener(hospitalInfo: HospitalInfo?) {
        // 사진버튼
        pictureImage.setOnClickListener {
            startAlbumImageUri().addOnSuccessListener { picUri ->
                getUid()?.let {
                    showProgressDialog()
                    getHospitalsStorageRef().child(it).child(profilePicJpgStr).putFile(picUri).continueWith {
                        toast("uploadPicFile Complete")
                        it.result.downloadUrl.toString()
                    }
                            .addOnSuccessListener { Glide.with(this@ChangeProfileActivity).load(it).into(pictureImage) }
                            .continueWithTask {
                                hospitalInfo
                                        ?.apply { profilePicUrl = it.result.toString() }
                                        ?.let { it1 -> getHospital()?.set(it1, SetOptions.merge()) }
                            }
                            .addOnCompleteListener { hideProgressDialog() }
                }
            }
        }
    }
}
