package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseLoginActivity
import com.dac.gapp.andac.databinding.ActivityLoginBinding
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.extension.random
import com.dac.gapp.andac.model.firebase.AdInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.user.activity_login.*
import timber.log.Timber

open class LoginActivity : BaseLoginActivity() {
    private var mAuth: FirebaseAuth? = null

    private lateinit var binding: ActivityLoginBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = getBinding()

        // Initialize Firebase Auth
        mAuth = getAuth()

        prepareUi()

        goToJoin.setOnClickListener { _ ->
            startActivity(Intent(this@LoginActivity, JoinActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {
            val mAuth = getAuth()

            if (binding.emailEdit.text.isEmpty()) {
                toast("이메일을 입력하세요")
                return@setOnClickListener
            }

            if (binding.passwordLoginEdit.text.isEmpty()) {
                toast("패스워드를 입력하세요")
                return@setOnClickListener
            }

            // 유저인지 확인 (병원계정 접근 금지)
            showProgressDialog()
            onCheckNormalUser(binding.emailEdit.text.toString()) { isUser ->
                if (isUser) {
                    mAuth?.signInWithEmailAndPassword(binding.emailEdit.text.toString(), binding.passwordLoginEdit.text.toString())?.addOnCompleteListener { task ->
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

        binding.findPasswordBtn.setOnClickListener {
            findPassword()
        }
    }

    private fun prepareUi() {
        hideActionBar()
        getDb().collection(Ad.LOGIN_BANNER.collectionName)
                .whereEqualTo("showingUp", true)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.size() > 0) {
                        val adInfoList = ArrayList<AdInfo>()
                        for (document in task.result) {
                            val adInfo = document.toObject(AdInfo::class.java)
                            Timber.d("photoUrl: ${adInfo.photoUrl}")
                            adInfoList.add(adInfo)
                        }
                        val index = (0..adInfoList.lastIndex).random()
                        if (!isFinishing)
                            binding.imgviewLoginBannerAd.loadImageAny(adInfoList[index].photoUrl)
                    }
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
        currentUser?.let { _ ->
            // TODO : 문자인증 게시판 채우기 위해 한시적으로 막아놓음, 10월 초에 풀기
//            if (currentUser.phoneNumber.isNullOrEmpty()) {
//                startActivity<JoinPhoneActivity>()
//                return
//            }

            toast(getString(R.string.successLogin))
            if (intent.getBooleanExtra(GOTO_MYPAGE, false)) startActivity(Intent(this, MyPageActivity::class.java))
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun onCheckNormalUser(email: String, onSuccess: (Boolean) -> Unit) {
        getUsers().whereEqualTo("email", email).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(!task.result.isEmpty)
            } else {
                task.exception?.printStackTrace()
            }
        }
    }


}
