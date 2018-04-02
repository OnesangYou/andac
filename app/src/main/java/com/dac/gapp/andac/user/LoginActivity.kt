package com.dac.gapp.andac.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dac.gapp.andac.BaseActivity
import com.dac.gapp.andac.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


open class LoginActivity : BaseActivity() {

    // static method
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        Toast.makeText(this@LoginActivity, currentUser.toString(), Toast.LENGTH_SHORT).show()

        if(currentUser != null){
            // 디비에 uuid가 있는지 검사
            val uuid = currentUser.uid
            getUsers(uuid)!!.get().addOnCompleteListener { task ->

                if(!task.isSuccessful) {
                    "Fail Get User Info".let {
                        Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                        Log.d(KBJ, it)
                    }
                }
                if(task.result.exists()) { // 있으면, 로그인 완료
                    "Login Success".let {
                        Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                        Log.d(KBJ, it)
                    }
                } else { // 없으면, 회원가입

                }
            }
        }

    }

}
