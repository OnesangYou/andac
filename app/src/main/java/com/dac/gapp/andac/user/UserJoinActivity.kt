package com.dac.gapp.andac.user

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dac.gapp.andac.BaseActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.custom.SwipeViewPager
import com.dac.gapp.andac.model.UserInfo
import com.dac.gapp.andac.fragment.JoinInfoFragment
import com.dac.gapp.andac.fragment.JoinPhoneFragment
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_join_info.*
import java.util.regex.Pattern


@Suppress("NAME_SHADOWING")
class UserJoinActivity : BaseActivity() {

    var MAX_PAGE = 2
    var cur_fragment = Fragment()
    var viewPager : SwipeViewPager? = null
    val mUserInfo = UserInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        viewPager = findViewById<View>(R.id.viewpager) as SwipeViewPager
        viewPager!!.adapter = Adapter(supportFragmentManager)

    }

    fun goToNextView(phoneNumber : String, isAgreeAlarm : Boolean){
        //핸드폰번호 유효성
        if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phoneNumber)) {
            Toast.makeText(this@UserJoinActivity, "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show()
            return
        }

        mUserInfo.phoneNumber = phoneNumber
        mUserInfo.isAgreeAlarm = isAgreeAlarm
        viewPager!!.apply { if(currentItem < MAX_PAGE) currentItem++ }
    }

    fun join(email : String, nickName : String) {

        // 닉네임 공백체크
        if(nickName == ""){
            Toast.makeText(this@UserJoinActivity, "Nick Name이 공백입니다", Toast.LENGTH_SHORT).show()
            return
        }

        //이메일형식체크
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this@UserJoinActivity, "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show()
            return
        }

        //비밀번호 유효성
        if (passwordEdit.text.toString().length < 6) {
            Toast.makeText(this@UserJoinActivity, "비밀번호는 6자리 이상입니다", Toast.LENGTH_SHORT).show()
            return
        }

        // 비밀번호 일치
        if(passwordEdit.text.toString() != passwordEdit.text.toString()) {
            "패스워드를 확인하세요".let {
                Toast.makeText(this@UserJoinActivity,it, Toast.LENGTH_SHORT).show()
                Log.d(KBJ, it)
            }
            return
        }

        mUserInfo.email = email
        mUserInfo.nickName = nickName

        // 회원가입 시도
        Log.d(KBJ, "emailEdit : " + emailEdit.text)
        val mAuth = getAuth()
        showProgressDialog()
        mAuth?.createUserWithEmailAndPassword(mUserInfo.email, passwordEdit.text.toString())?.addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(KBJ, "createUserWithEmail:success")
                val user = mAuth.currentUser

                // DB insert
                getUsers().document(user?.uid!!).set(mUserInfo).addOnCompleteListener{ task2 ->
                    if(task2.isSuccessful){
                        updateUI(user)
                    } else {
                        "회원가입 실패".let {
                            Log.d(KBJ, it)
                            Toast.makeText(this@UserJoinActivity,it,Toast.LENGTH_SHORT).show()
                            updateUI(null)
                        }
                    }
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w(KBJ, "createUserWithEmail:failure", task.exception)
                Toast.makeText(this@UserJoinActivity, "Authentication failed." + task.exception,
                        Toast.LENGTH_SHORT).show()
                updateUI(null)
            }

        }

    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if(user != null){
            Toast.makeText(this@UserJoinActivity, "Authentication Success.",
                    Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private inner class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            if (position < 0 || MAX_PAGE <= position)
                return null
            when (position) {
                0 -> cur_fragment = JoinPhoneFragment()
                1 -> cur_fragment = JoinInfoFragment()
            }
            return cur_fragment
        }

        override fun getCount(): Int {
            return MAX_PAGE
        }
    }

}
