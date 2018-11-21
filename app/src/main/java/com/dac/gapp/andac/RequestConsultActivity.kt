package com.dac.gapp.andac

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityRequestConsultBinding
import com.dac.gapp.andac.extension.loadImage
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.dac.gapp.andac.util.UiUtil.Companion.VisionArr
import com.dac.gapp.andac.util.UiUtil.Companion.getVisionIndex
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_request_consult.*
import org.jetbrains.anko.alert
import timber.log.Timber

class RequestConsultActivity : BaseActivity() {
    private lateinit var binding: ActivityRequestConsultBinding
    var pictureTaskUnit : ((refKey : String, consultInfo : ConsultInfo) -> Task<Unit>)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_consult)
        if(isHospital()) {
            toast("병원은 상담 요청할 수 없습니다")
            finish()
            return
        }
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("1:1 상담신청서")
        setActionBarRightImage(R.drawable.delete)
        setOnActionBarLeftClickListener(View.OnClickListener { onBackPressed() })
        setOnActionBarRightClickListener(View.OnClickListener { _ ->

            alert(title = "상담내역 리셋", message = "상담내역을 리셋 하시겠습니까?") {
                positiveButton("YES") { _ ->
                    // 에디트 텍스트 비우기
                    arrayOf(diseaseEdit, nameEdit, phoneEdit, insert_text_Edit, oldEdit).forEach { it.text.clear() }
                }
                negativeButton("NO") {}
            }.show()


        })
        binding = getBinding()
        binding.activity = this

        intent.getBooleanExtra("isOpen", true).let { binding.isOpen = it }

        // 시력
        arrayListOf(binding.leftVisionPicker, binding.rightVisionPicker).forEach {
            it.apply {
                minValue = 0
                maxValue = VisionArr.size - 1
                displayedValues = VisionArr
                value = 10
            }
        }

        // 이미 신청했으면 수정 모드
        if(binding.isOpen?:return){
            // Open 상담
            showProgressDialog()
            getOpenConsult()?.get()
                    ?.addOnSuccessListener { snapshot ->
                        snapshot.toObject(ConsultInfo::class.java)?.apply {
                            arrayOf(tag_1, tag_2, tag_3, tag_4).forEach {
                                it.isChecked = it.tag == tag
                            }

                            leftVisionPicker.value = getVisionIndex(leftVision)
                            rightVisionPicker.value = getVisionIndex(rightVision)

                            diseaseEdit.setText(disease)
                            nameEdit.setText(name)
                            phoneEdit.setText(phone)
                            insert_text_Edit.setText(text)
                            oldEdit.setText(age.toString())
                            insert_picture_img.loadImage(pictureUrl)
                            resources.getStringArray(R.array.region_list).forEachIndexed { index, s ->
                                if(s == range) {
                                    regionSpinner.setSelection(index)
                                    return@forEachIndexed
                                }
                            }

                            // 사진 삭제
                            setCancleBtn()
                        }
                    }
                    ?.addOnCompleteListener { hideProgressDialog() }
        } else {
            // 지정 상담
            val hospitalId = intent.getStringExtra("hospitalId")?:return
            showProgressDialog()
            val userId = getUid()?:return
            getSelectConsult(hospitalId, userId).get()
                    .addOnSuccessListener { snapshot ->
                        if(snapshot.isEmpty) return@addOnSuccessListener
                        snapshot.toObjects(ConsultInfo::class.java).also { if(it.isEmpty()) return@addOnSuccessListener }.let { it[0] }.apply {
                            arrayOf(tag_1, tag_2, tag_3, tag_4).forEach {
                                it.isChecked = it.tag == tag
                            }
                            diseaseEdit.setText(disease)
                            nameEdit.setText(name)
                            phoneEdit.setText(phone)
                            insert_text_Edit.setText(text)
                            oldEdit.setText(age.toString())
                            insert_picture_img.loadImage(pictureUrl)

                            // 사진 삭제
                            setCancleBtn()

                        }
                        intent.putExtra("objectId", snapshot.documentChanges[0].document.id)  // 수정모드
                    }
                    .addOnCompleteListener { hideProgressDialog() }
        }

        intent.getStringExtra("name")?.let {
            binding.regionText1.text = it
        }

        // 사진
        insert_picture_img.setOnClickListener { _ ->
            getAlbumImage()?.subscribe {uri ->
                insert_picture_img.loadImageAny(uri)
                pictureTaskUnit = { refKey, consultInfo ->
                    FirebaseStorage.getInstance().getReference(refKey).child("picture.jpg").putFile(uri).continueWith {
                        consultInfo.pictureUrl = it.result.downloadUrl.toString()
                        consultInfo.pictureRef = it.result.storage.path
                    }
                }
            }?.apply { disposables.add(this) }
        }

        // 사진 삭제
        cancle_btn.setOnClickListener { _ ->
            insert_picture_img.loadImageAny(R.drawable.album_ic_add_photo_white)
            pictureTaskUnit = null
        }

    }

    private fun ConsultInfo?.setCancleBtn() {
        cancle_btn.setOnClickListener { _ ->
            this?:return@setOnClickListener
            insert_picture_img.loadImageAny(R.drawable.album_ic_add_photo_white)
            pictureTaskUnit = if (pictureUrl.isNullOrEmpty()) null else { _, consultInfo ->
                FirebaseStorage.getInstance().getReferenceFromUrl(pictureUrl.toString())
                        .delete().continueWith { consultInfo.pictureUrl = "" }
            }
        }
    }

    private fun createConsult(): ConsultInfo?
    {
        val id: Int = radiogroup_tag.checkedRadioButtonId
        val radio: RadioButton = findViewById(id)
        return ConsultInfo(
                tag = radio.tag.toString(),
                leftVision = VisionArr[leftVisionPicker.value].toDouble(),
                rightVision = VisionArr[rightVisionPicker.value].toDouble(),
                disease = diseaseEdit.text.toString(),
                userId = getUid(),
                name = nameEdit.text.toString(),
                phone = phoneEdit.text.toString(),
                text = insert_text_Edit.text.toString(),
                range = regionSpinner.selectedItem.toString(),
                age = oldEdit.text?.toString()?.toIntOrNull()
        )
    }

    fun onClickOpen(view: View) {
        Timber.d("Open")

        check(radiogroup_tag.checkedRadioButtonId != -1) {
            toast("태그를 선택하세요")
            return
        }

        // 유효성 검사
        arrayOf(diseaseEdit, nameEdit, phoneEdit, insert_text_Edit, oldEdit).forEach {
            if(it.text.isBlank()) return toast(it.hint)
        }

        // 신청서 Ref
        val ref = getOpenConsult()?:return

        // 신청서 Info
        val consultInfo = createConsult()?:return

        // 데이터 업로드(사진있으면 사진도 업로드)
        showProgressDialog()
        arrayListOf(pictureTaskUnit?.invoke(ref.path, consultInfo)).forEach { task ->
            task.let { task -> Tasks.whenAllSuccess<Any>(arrayOf(task).filterNotNull()).continueWithTask { ref.set(consultInfo, SetOptions.merge()) } }
                .addOnSuccessListener {
                    Toast.makeText(this, "신청 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnCompleteListener { hideProgressDialog() }
        }
    }

    fun onClickSelecet(view: View) {

        // 유효성 검사
        arrayOf(diseaseEdit, nameEdit, phoneEdit, insert_text_Edit, oldEdit).forEach {
            if(it.text.isBlank()) return toast(it.hint)
        }

        // 병원 ID
        val hospitalId = intent.getStringExtra("hospitalId")?:return

        // 수정모드 인지 검사
        val objectId = intent.getStringExtra("objectId")?:null

        // 신청서 Ref
        val ref = getSelectConsults().let { objectId?.run { it.document(this) }?:it.document() }

        // 신청서 Info
        val consultInfo = createConsult().also { it?.hospitalId = hospitalId }?:return

        // 데이터 업로드(사진있으면 사진도 업로드)
        showProgressDialog()
        pictureTaskUnit?.invoke(ref.id,consultInfo)
            .let { task -> Tasks.whenAllSuccess<Any>(arrayOf(task).filterNotNull()).continueWithTask { ref.set(consultInfo, SetOptions.merge()) } }
                .addOnSuccessListener {
                    Toast.makeText(this, "신청 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnCompleteListener { hideProgressDialog() }
    }
}
