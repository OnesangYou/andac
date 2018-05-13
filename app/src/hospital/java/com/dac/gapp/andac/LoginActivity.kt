package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.dac.gapp.andac.base.BaseHospitalActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.hospital.activity_login.*
import timber.log.Timber

class LoginActivity : BaseHospitalActivity() {
    private var mAuth: FirebaseAuth? = null

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

            showProgressDialog()
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
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        showProgressDialog()
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null) {
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
    }
}
