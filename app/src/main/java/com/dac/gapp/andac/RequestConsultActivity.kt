package com.dac.gapp.andac

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.dac.gapp.andac.base.BaseActivity
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_consult)
        binding.activity = this

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        intent.getBooleanExtra("isOpen", true).let {
            binding.isOpen = it
        }
        // Get the ActionBar here to configure the way it behaves.
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        toolbar.setNavigationIcon(R.drawable.back2)

        toolbar.setNavigationOnClickListener() { view ->
            onBackPressed()
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
