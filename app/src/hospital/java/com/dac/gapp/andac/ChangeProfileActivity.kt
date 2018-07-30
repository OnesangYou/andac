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
            hospitalText.text = name
            phoneText.text = phone
            addressText.text = address2
            openHourText.text = openDate
            descriptionText.text = description
        }
                .apply { hospitalInfo = this@apply }}
                ?.addOnCompleteListener {
                    hideProgressDialog()
                    setOnClickListener(hospitalInfo)
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
