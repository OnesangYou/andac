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
import kotlinx.android.synthetic.main.activity_login.*

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

        goToJoin.setOnClickListener{
            Intent(this@LoginActivity, JoinActivity::class.java).let {
                startActivity(it)
            }
        }

        loginBtn.setOnClickListener {
            val mAuth = getAuth()

            if(emailEdit.text.isEmpty()) {
                "이메일을 입력하세요".let {
                    Log.d(KBJ, it)
                    Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            if(passwordLoginEdit.text.isEmpty()) {
                "패스워드를 입력하세요".let {
                    Log.d(KBJ, it)
                    Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            mAuth?.signInWithEmailAndPassword(emailEdit.text.toString(), passwordLoginEdit.text.toString())?.addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(KBJ, "signInWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(KBJ, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this@LoginActivity, "Authentication failed." + task.exception,
                            Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        }



    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null){
            Toast.makeText(this@LoginActivity, "Authentication Success.",
                    Toast.LENGTH_SHORT).show()

            // 디비에 uuid가 있는지 검사
            /*
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
            */
        }

    }

}
