package com.dac.gapp.andac

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.dac.gapp.andac.base.BaseJoinActivity
import com.dac.gapp.andac.custom.SwipeViewPager
import com.dac.gapp.andac.fragment.JoinInfoFragment
import com.dac.gapp.andac.fragment.JoinTermsFragment
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.user.fragment_join_info.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug


@Suppress("NAME_SHADOWING")
class JoinActivity : BaseJoinActivity(), AnkoLogger {

    var MAX_PAGE = 2
    var cur_fragment = Fragment()
    var viewPager : SwipeViewPager? = null
    val mUserInfo = UserInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        hideActionBar()
        viewPager = findViewById<View>(R.id.viewpager) as SwipeViewPager
        viewPager!!.adapter = Adapter(supportFragmentManager)

    }

    fun goToNextView(isAgreeAlarm : Boolean){
        mUserInfo.isAgreeAlarm = isAgreeAlarm
        viewPager!!.apply { if(currentItem < MAX_PAGE) currentItem++ }
    }

    fun join(email : String, nickName : String) {

        // 닉네임 공백체크
        if(nickName == ""){
            toast("Nick Name이 공백입니다")
            return
        }

        //이메일형식체크
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("이메일 형식이 아닙니다")
            return
        }

        //비밀번호 유효성
        if (passwordEdit.text.toString().length < 6) {
            toast("비밀번호는 6자리 이상입니다")
            return
        }

        // 비밀번호 일치
        if(passwordEdit.text.toString() != passwordEdit.text.toString()) {
            toast("패스워드를 확인하세요")
            return
        }

        mUserInfo.email = email
        mUserInfo.nickName = nickName

        // 회원가입 시도
        debug("emailEdit : " + emailEdit.text)
        val mAuth = getAuth()
        showProgressDialog()
        mAuth?.createUserWithEmailAndPassword(mUserInfo.email, passwordEdit.text.toString())
                ?.onSuccessTask { _ ->
                    val user = mAuth.currentUser?:throw IllegalStateException()
                    getUsers().document(user.uid).set(mUserInfo).continueWith { user }
                }
                ?.addOnSuccessListener { updateUI(it) }
                ?.addOnFailureListener{
                    // If sign in fails, display a message to the user.
                    toast("회원가입에 실패 하였습니다. ${it.localizedMessage}")
                    updateUI(null)
                }
                ?.addOnCompleteListener { hideProgressDialog() }
    }

    private fun updateUI(user: FirebaseUser?) {
        user?.let{
            toast("Authentication Success.")
            finish()
        }
    }

    private inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            if (position < 0 || MAX_PAGE <= position)
                return null
            when (position) {
                0 -> cur_fragment = JoinTermsFragment()
                1 -> cur_fragment = JoinInfoFragment()
            }
            return cur_fragment
        }

        override fun getCount(): Int {
            return MAX_PAGE
        }
    }

}
