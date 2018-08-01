package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        goToJoin.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }

        loginBtn.setOnClickListener {
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
            onCheckHospitalUser(emailEdit.text.toString()) { isHospitalUser ->
                if(isHospitalUser){
                    // toast("회원가입 되있음")
                    mAuth?.signInWithEmailAndPassword(emailEdit.text.toString(), passwordLoginEdit.text.toString())?.addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.tag(KBJ).d("signInWithEmail:success")
                            val user = mAuth.currentUser
                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            toast("Authentication failed." + task.exception)
                            updateUI(null)
                        }
                    }
                } else {
                    // 회원가입 안되있음
                    toast("병원 유저 회원가입이 안된 Email 입니다")
                    updateUI(null)
                }
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
            // TODO : 폰번호 인증
            if(currentUser.phoneNumber.isNullOrBlank()){
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

    private fun onCheckHospitalUser(email : String, onSuccess: (Boolean) -> Unit){
        getHospitals().whereEqualTo("email", email).get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                onSuccess(!task.result.isEmpty)
            } else {
                Timber.d(task.exception.toString())
            }
        }
    }
}
