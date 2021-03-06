package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.dac.gapp.andac.base.BaseLoginActivity
import com.dac.gapp.andac.databinding.ActivityLoginBinding
import com.dac.gapp.andac.enums.Ad
import com.dac.gapp.andac.enums.AdCountType
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.extension.random
import com.dac.gapp.andac.model.firebase.AdInfo
import com.dac.gapp.andac.util.FcmInstanceIdService
import com.dac.gapp.andac.util.Preference
import com.dac.gapp.andac.util.UiUtil.Companion.getMessageFromAuthException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import org.jetbrains.anko.startActivity
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


        val fids = FcmInstanceIdService()
        fids.onTokenRefresh()

        binding.goToJoin.setOnClickListener { _ ->
            startActivity(Intent(this@LoginActivity, JoinActivity::class.java))
        }

        binding.loginBtn.setOnClickListener { _ ->
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

            onCheckNormalUser(binding.emailEdit.text.toString()).continueWithTask {
                if (!it.result) throw IllegalStateException("유저 회원가입이 안된 Email 입니다")
                mAuth?.signInWithEmailAndPassword(binding.emailEdit.text.toString(), binding.passwordLoginEdit.text.toString())
            }.addOnSuccessListener {
                val fids = FcmInstanceIdService()
                fids.onTokenRefresh()
                // Sign in success, update UI with the signed-in user's information
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
            }.addOnCompleteListener { hideProgressDialog() }

        }

        binding.findPasswordBtn.setOnClickListener {
            findPassword()
        }

        // Auto Login
        binding.autoLoginCheck.isChecked = getSharedPreferences(Preference.FileName, 0).getBoolean(Preference.AutoLogin, false)
        binding.autoLoginCheck.setOnCheckedChangeListener { _, isChecked ->
            getSharedPreferences(Preference.FileName, 0).edit().putBoolean(Preference.AutoLogin, isChecked).apply()
        }

        binding.findIdBtn.setOnClickListener { startActivity<FindMailByPhoneActivity>() }
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
                        if (!isFinishing) {
                            binding.imgviewLoginBannerAd.loadImageAny(adInfoList[index].photoUrl)
                            binding.imgviewLoginBannerAd.setOnClickListener { addCountAdClick(adInfoList[index].hospitalId, AdCountType.LOGIN) }
                        }
                    }
                }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        // Auto Login
        val isAutoLogin = getSharedPreferences(Preference.FileName, 0).getBoolean(Preference.AutoLogin, false)
        val currentUser = if(isAutoLogin) mAuth?.currentUser else {mAuth?.signOut(); null}
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        currentUser?.let { _ ->
            if (!isExistPhoneNumber()) {
                startActivity<JoinPhoneActivity>()
                return
            }

            toast(getString(R.string.successLogin))
            if (intent.getBooleanExtra(GOTO_MYPAGE, false)) startActivity(Intent(this, MyPageActivity::class.java))
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun onCheckNormalUser(email: String) =
            getUsers().whereEqualTo("email", email).get().continueWith { !it.result.isEmpty }

}
