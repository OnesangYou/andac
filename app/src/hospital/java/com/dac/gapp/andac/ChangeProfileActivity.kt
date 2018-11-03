package com.dac.gapp.andac

import android.os.Bundle
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityChangeProfileBinding
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import org.jetbrains.anko.alert

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

        // 사진 삭제
        binding.cancleBtn.setOnClickListener { _ ->
            if(hospitalInfo?.profilePicUrl.isNullOrEmpty()) return@setOnClickListener
            alert(title = "프로필 사진 지우기", message = "프로필 사진을 삭제하시겠습니까?") {

                positiveButton("YES") { _ ->
                    showProgressDialog()
                    FirebaseStorage.getInstance().getReferenceFromUrl(hospitalInfo?.profilePicUrl?:return@positiveButton).delete()
                            .continueWithTask {
                                hospitalInfo
                                        ?.apply { profilePicUrl = "" }
                                        ?.let { it1 -> getHospital()?.set(it1, SetOptions.merge()) }
                            }
                            .addOnSuccessListener {
                                binding.pictureImage.loadImageAny(android.R.drawable.ic_menu_camera)
                                toast("프로필 사진을 삭제 완료하였습니다.")
                            }
                            .addOnCompleteListener { hideProgressDialog() }
                            .addOnFailureListener {
                                it.printStackTrace()
                            }
                }
                negativeButton("NO") {}
            }.show()
        }
    }

    private fun setOnClickListener(hospitalInfo: HospitalInfo?) {
        // 사진버튼
        binding.pictureImage.setOnClickListener { _ ->
            getAlbumImage()?.subscribe {picUri ->
                getUid()?.let { s ->
                    showProgressDialog()
                    getHospitalsStorageRef().child(s).child(profilePicJpgStr).putFile(picUri).continueWith {
                        toast("프로필 사진 업로드 완료되었습니다")
                        it.result.downloadUrl.toString()
                    }
                            .addOnSuccessListener { binding.pictureImage.loadImageAny(it) }
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
