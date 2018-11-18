package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.dac.gapp.andac.base.BaseHospitalActivity
import com.dac.gapp.andac.databinding.ActivityLoginBinding
import com.dac.gapp.andac.util.FcmInstanceIdService
import com.dac.gapp.andac.util.Preference
import com.dac.gapp.andac.util.UiUtil.Companion.getMessageFromAuthException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.startActivity
import timber.log.Timber

class LoginActivity : BaseHospitalActivity() {
    private var mAuth: FirebaseAuth? = null

    lateinit var binding: ActivityLoginBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = getBinding()
        hideActionBar()

        // Initialize Firebase Auth
        mAuth = getAuth()

        binding.goToJoin.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }

        binding.loginBtn.setOnClickListener { _ ->
            val mAuth = getAuth()

            if(binding.emailEdit.text.isEmpty()) {
                this@LoginActivity.toast("이메일을 입력하세요")
                return@setOnClickListener
            }

            if(binding.passwordLoginEdit.text.isEmpty()) {
                this@LoginActivity.toast("패스워드를 입력하세요")
                return@setOnClickListener
            }

            // 병원 유저인지 체크
            showProgressDialog()
            onCheckHospitalUser(binding.emailEdit.text.toString()).continueWithTask {
                if(!it.result) throw IllegalStateException("병원 회원가입이 안된 Email 입니다")
                mAuth?.signInWithEmailAndPassword(binding.emailEdit.text.toString(), binding.passwordLoginEdit.text.toString())
            }.addOnSuccessListener {
                Timber.tag(KBJ).d("signInWithEmail:success")
                val user = it.user
                updateUI(user)
            }.addOnFailureListener { exception ->

                val exceptionMessage = if(exception is FirebaseAuthException) {
                    getMessageFromAuthException(exception)
                } else {
                    exception.localizedMessage
                }

                toast("로그인 실패, $exceptionMessage")
                updateUI(null)
            }.addOnCompleteListener {
                hideProgressDialog()
            }
        }

        binding.findPasswordBtn.setOnClickListener {
            findPassword()
        }

        // Auto Login
        binding.autoLoginCheck.isChecked = getSharedPreferences(Preference.FileName, 0).getBoolean(Preference.AutoLogin, false)
        binding.autoLoginCheck.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences(Preference.FileName, 0).edit().putBoolean(Preference.AutoLogin, isChecked).apply()
        }

        binding.findIdButton.setOnClickListener { startActivity<FindMailByPhoneActivity>() }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        // Auto Login
        val isAutoLogin = getSharedPreferences(Preference.FileName, 0).getBoolean(Preference.AutoLogin, false)
        val currentUser = if(isAutoLogin) mAuth?.currentUser else {mAuth?.signOut(); null}
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

                    val fids = FcmInstanceIdService()
                    fids.onTokenRefresh()

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
