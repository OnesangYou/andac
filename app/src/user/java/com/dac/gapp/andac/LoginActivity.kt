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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.user.activity_login.*
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

        goToJoin.setOnClickListener { _ ->
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
                // Sign in success, update UI with the signed-in user's information
                Timber.tag(KBJ).d("signInWithEmail:success")
                val user = it.user
                updateUI(user)
            }.addOnFailureListener {
                toast("로그인 실패, ${it.localizedMessage}")
                updateUI(null)
            }.addOnCompleteListener { hideProgressDialog() }

        }

        binding.findPasswordBtn.setOnClickListener {
            findPassword()
        }
    }

    private fun prepareUi() {
        hideActionBar()
        getDb().collection(Ad.LOGIN_BANNER.collectionName)
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
        val currentUser = mAuth!!.currentUser
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
