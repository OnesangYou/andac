package com.dac.gapp.andac

import android.os.Bundle
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityChangeProfileBinding
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.firebase.firestore.SetOptions

class ChangeProfileActivity : BaseActivity() {

    lateinit var binding : ActivityChangeProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile)

        binding = getBinding()

        var hospitalInfo: HospitalInfo? = null

        // Hospital Info 출력
        showProgressDialog()
        getHospitalInfo()?.addOnSuccessListener {it?.apply {
            Glide.with(this@ChangeProfileActivity).load(profilePicUrl).into(binding.pictureImage)
            binding.hospitalText.setText(name)
            binding.phoneText.setText(phone)
            binding.addressText.setText(address2)
            binding.openHourText.setText(openDate)
            binding.descriptionText.setText(description)
        }
                .apply { hospitalInfo = this@apply }}
                ?.addOnCompleteListener {
                    hideProgressDialog()
                    setOnClickListener(hospitalInfo)
                }

        binding.completeBtn.setOnClickListener { _ ->
            hospitalInfo?.apply {
                name = binding.hospitalText.text.toString()
                phone = binding.phoneText.text.toString()
                address2 = binding.addressText.text.toString()
                openDate = binding.openHourText.text.toString()
                description = binding.descriptionText.text.toString()
            }
                    ?.let {
                        showProgressDialog()
                        getHospital()?.set(it)
                    }
                    ?.addOnCompleteListener { hideProgressDialog() }
                    ?.addOnSuccessListener { toast("프로필 변경 완료되었습니다"); finish() }
                    ?:hideProgressDialog()
        }
    }

    private fun setOnClickListener(hospitalInfo: HospitalInfo?) {
        // 사진버튼
        binding.pictureImage.setOnClickListener { _ ->
            getAlbumImage()?.subscribe {picUri ->
                getUid()?.let { s ->
                    showProgressDialog()
                    getHospitalsStorageRef().child(s).child(profilePicJpgStr).putFile(picUri).continueWith {
                        toast("uploadPicFile Complete")
                        it.result.downloadUrl.toString()
                    }
                            .addOnSuccessListener { Glide.with(this@ChangeProfileActivity).load(it).into(binding.pictureImage) }
                            .continueWithTask {
                                hospitalInfo
                                        ?.apply { profilePicUrl = it.result.toString() }
                                        ?.let { it1 -> getHospital()?.set(it1, SetOptions.merge()) }
                            }
                            .addOnCompleteListener { hideProgressDialog() }
                }
            }?.apply { disposables.add(this) }
        }
    }
}
