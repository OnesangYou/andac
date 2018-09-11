package com.dac.gapp.andac

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityHospitalBinding
import com.dac.gapp.andac.databinding.ActivityRequestConsultBinding
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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

        intent.getBooleanExtra("isOpen", true).let {
            binding.isOpen = it
        }
        intent.getStringExtra("hospitalName")?.let {
            binding.regionText1.text = it
        }
    }

    fun createConsult(): ConsultInfo? {
        val id: Int = radiogroup_tag.checkedRadioButtonId
        val radio: RadioButton = findViewById(id)
        val tag = radio.text.toString()
        val visualacuity = visualacuityEdit.text.toString()
        val disease = diseaseEdit.text.toString()
        val name = nameEdit.text.toString()
        val phone = phoneEdit.text.toString()
        val text = insert_text_Edit.text.toString()
        val range = regionSpinner.selectedItem.toString()
        val surgery = ConsultInfo(tag, visualacuity, disease, name, phone, range, text)
        return surgery
    }

    fun onClickOpen(view: View) {
        Timber.d("Open")
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        getUid()?.let { uid ->
            val batch = db.batch()
            val dr = db.collection("openConsult").document(uid)

            FieldValue.serverTimestamp().let { batch.set(dr, mapOf("createdDate" to it)) }
            createConsult()?.let { batch.set(dr.collection("content").document(uid), it) }

            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "신청 성공", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnCanceledListener {
                Toast.makeText(this, "신청 실패 다시시도", Toast.LENGTH_SHORT).show()
            }
        } ?: goToLogin()
    }

    fun onClickSelecet(view: View) {
        Timber.d("Select")
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        getUid()?.let { uid ->
            val batch = db.batch()
            val dr = db.collection("selectConsult").document(intent.getStringExtra("documentId")).collection("users").document(uid)

            FieldValue.serverTimestamp().let { batch.set(dr, mapOf("createdDate" to it)) }
            createConsult()?.let { batch.set(dr.collection("content").document(uid), it) }

            batch.commit().addOnSuccessListener {
                Toast.makeText(this, "신청 성공", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnCanceledListener {
                Toast.makeText(this, "신청 실패 다시시도", Toast.LENGTH_SHORT).show()
            }
        } ?: goToLogin()
    }
}
