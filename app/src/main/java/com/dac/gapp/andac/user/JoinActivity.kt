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
import com.dac.gapp.andac.fragment.JoinInfoFragment
import com.dac.gapp.andac.fragment.JoinPhoneFragment
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_join_info.*


class JoinActivity : BaseActivity() {

    var MAX_PAGE = 2
    var cur_fragment = Fragment()
    var viewPager : SwipeViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        viewPager = findViewById<View>(R.id.viewpager) as SwipeViewPager
        viewPager!!.adapter = Adapter(supportFragmentManager)

    }

    fun goToNextView() {
        viewPager!!.apply { if(currentItem < MAX_PAGE) currentItem++ }
    }

    fun join() {
        (cur_fragment as JoinInfoFragment).apply {

            if(passwordEdit.text.toString() != passwordEdit.text.toString()) {
                "패스워드를 확인하세요".let {
                    Toast.makeText(this@JoinActivity,it, Toast.LENGTH_SHORT).show()
                    Log.d(KBJ, it)
                }
                return
            }

            Log.d(KBJ, "emailEdit : " + emailEdit.text)
            val mAuth = getAuth()
            mAuth?.createUserWithEmailAndPassword(emailEdit.text.toString(), passwordEdit.text.toString())?.addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(KBJ, "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(KBJ, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this@JoinActivity, "Authentication failed." + task.exception,
                            Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            })
        }

    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
            Toast.makeText(this@JoinActivity, "Authentication Success.",
                    Toast.LENGTH_SHORT).show()
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
