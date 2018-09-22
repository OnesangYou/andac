package com.dac.gapp.andac

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityRequestConsultBinding
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_request_consult.*
import timber.log.Timber

class RequestSurgeryActivity : BaseActivity() {
    private lateinit var binding: ActivityRequestConsultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_consult)
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("1:1 상담신청서")
        setActionBarRightImage(R.drawable.delete)
        setOnActionBarLeftClickListener(View.OnClickListener { onBackPressed() })
        binding = getBinding()
        binding.activity = this

        intent.getBooleanExtra("isOpen", true).let { binding.isOpen = it }

        // 이미 신청했으면 수정 모드
        if(binding.isOpen?:return){
            // Open 상담
            showProgressDialog()
            getOpenConsult().get()
                    .addOnSuccessListener {
                        it.toObject(ConsultInfo::class.java)?.apply {
                            arrayOf(tag_1, tag_2, tag_3, tag_4).forEach {
                                it.isChecked = it.tag == tag
                            }
                            visualacuityEdit.setText(visualacuity)
                            diseaseEdit.setText(disease)
                            nameEdit.setText(name)
                            phoneEdit.setText(phone)
                            insert_text_Edit.setText(text)
                            oldEdit.setText(age.toString())
                            resources.getStringArray(R.array.region_list).forEachIndexed { index, s ->
                                if(s == range) {
                                    regionSpinner.setSelection(index)
                                    return@forEachIndexed
                                }
                            }
                        }
                    }
                    .addOnCompleteListener { hideProgressDialog() }
        } else {
            // 지정 상담
            val hospitalId = intent.getStringExtra("hospitalId")?:return
            showProgressDialog()
            val userId = getUid()?:return
            getSelectConsult(hospitalId, userId).get()
                    .addOnSuccessListener {
                        if(it.isEmpty) return@addOnSuccessListener
                        it.toObjects(ConsultInfo::class.java).also { if(it.isEmpty()) return@addOnSuccessListener }.let { it[0] }.apply {
                            arrayOf(tag_1, tag_2, tag_3, tag_4).forEach {
                                it.isChecked = it.tag == tag
                            }
                            visualacuityEdit.setText(visualacuity)
                            diseaseEdit.setText(disease)
                            nameEdit.setText(name)
                            phoneEdit.setText(phone)
                            insert_text_Edit.setText(text)
                            oldEdit.setText(age.toString())

                        }
                        intent.putExtra("objectId", it.documentChanges[0].document.id)  // 수정모드
                    }
                    .addOnCompleteListener { hideProgressDialog() }
        }

        intent.getStringExtra("hospitalName")?.let {
            binding.regionText1.text = it
        }
    }

    private fun createConsult(): ConsultInfo?
    {
        val id: Int = radiogroup_tag.checkedRadioButtonId
        val radio: RadioButton = findViewById(id)
        return ConsultInfo(
                tag = radio.tag.toString(),
                visualacuity = visualacuityEdit.text.toString(),
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

        // 유효성 검사
        arrayOf(visualacuityEdit, diseaseEdit, nameEdit, phoneEdit, insert_text_Edit, oldEdit).forEach {
            if(it.text.isBlank()) return toast(it.hint)
        }

        showProgressDialog()
        getOpenConsult().set(createConsult()?:return, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "신청 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnCompleteListener { hideProgressDialog() }
    }

    fun onClickSelecet(view: View) {

        // 유효성 검사
        arrayOf(visualacuityEdit, diseaseEdit, nameEdit, phoneEdit, insert_text_Edit, oldEdit).forEach {
            if(it.text.isBlank()) return toast(it.hint)
        }

        val hospitalId = intent.getStringExtra("hospitalId")?:return
        val consultInfo = createConsult().also { it?.hospitalId = hospitalId }?:return

        // 수정모드 인지 검사
        val objectId = intent.getStringExtra("objectId")?:null

        showProgressDialog()
        getSelectConsults()
                .let { objectId?.run { it.document(this) }?:it.document() }
                .set(consultInfo, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "신청 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnCompleteListener { hideProgressDialog() }
    }
}
