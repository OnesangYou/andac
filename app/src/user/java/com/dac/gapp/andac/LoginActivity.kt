package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.dac.gapp.andac.base.BaseLoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.user.activity_login.*
import org.jetbrains.anko.startActivity
import timber.log.Timber

open class LoginActivity : BaseLoginActivity() {
    private var mAuth: FirebaseAuth? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        mAuth = getAuth()

        goToJoin.setOnClickListener{
            Intent(this@LoginActivity, JoinActivity::class.java).let {
                startActivity(it)
            }
        }

        loginBtn.setOnClickListener {
            val mAuth = getAuth()

            if(emailEdit.text.isEmpty()) {
                toast("이메일을 입력하세요")
                return@setOnClickListener
            }

            if(passwordLoginEdit.text.isEmpty()) {
                toast("패스워드를 입력하세요")
                return@setOnClickListener
            }

            // 유저인지 확인 (병원계정 접근 금지)
            showProgressDialog()
            onCheckNormalUser(emailEdit.text.toString()) { isUser ->
                if(isUser){
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
                    toast("유저 회원가입이 안된 Email 입니다")
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
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        hideProgressDialog()
        currentUser?.let{user ->
            if(currentUser.phoneNumber.isNullOrEmpty()){
                startActivity<JoinPhoneActivity>()
                return
            }

            toast(getString(R.string.successLogin))
            if(intent.getBooleanExtra(GOTO_MYPAGE, false)) startActivity(Intent(this, MyPageActivity::class.java))
            finish()
        }
    }

    private fun onCheckNormalUser(email : String, onSuccess: (Boolean) -> Unit){
        getUsers().whereEqualTo("email", email).get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                onSuccess(!task.result.isEmpty)
            } else {
                task.exception?.printStackTrace()
            }
        }
    }


}
