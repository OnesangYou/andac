package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.dac.gapp.andac.base.BaseHospitalActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.hospital.activity_login.*
import org.jetbrains.anko.startActivity
import timber.log.Timber

class LoginActivity : BaseHospitalActivity() {
    private var mAuth: FirebaseAuth? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        hideActionBar()

        // Initialize Firebase Auth
        mAuth = getAuth()

        goToJoin.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }

        loginBtn.setOnClickListener { _ ->
            val mAuth = getAuth()

            if(emailEdit.text.isEmpty()) {
                this@LoginActivity.toast("이메일을 입력하세요")
                return@setOnClickListener
            }

            if(passwordLoginEdit.text.isEmpty()) {
                this@LoginActivity.toast("패스워드를 입력하세요")
                return@setOnClickListener
            }

            // 병원 유저인지 체크
            showProgressDialog()
            onCheckHospitalUser(emailEdit.text.toString()).continueWithTask {
                if(!it.result) throw IllegalStateException("병원 회원가입이 안된 Email 입니다")
                mAuth?.signInWithEmailAndPassword(emailEdit.text.toString(), passwordLoginEdit.text.toString())
            }.addOnSuccessListener {
                Timber.tag(KBJ).d("signInWithEmail:success")
                val user = it.user
                updateUI(user)
            }.addOnFailureListener {
                // 회원가입 안되있음
                toast("회원가입 실패, ${it.localizedMessage}")
                updateUI(null)
            }.addOnCompleteListener {
                hideProgressDialog()
            }
        }

        findPasswordBtn.setOnClickListener {
            findPassword()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        showProgressDialog()
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            // 폰번호 인증
            if(!isExistPhoneNumber()){
                startActivity<JoinPhoneActivity>()
                return
            }

            // 관리자 승인 확인
            onCheckApproval { isCheck ->
                if (isCheck) {
                    // 승인 완료
                    toast(getString(R.string.successLogin))
                } else {
                    toast(getString(R.string.waitApproval))
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                hideProgressDialog()
            }
        }
        else {
            hideProgressDialog()
        }
    }

    private fun onCheckHospitalUser(email : String) =
            getHospitals().whereEqualTo("email", email).get().continueWith { !it.result.isEmpty }
}
