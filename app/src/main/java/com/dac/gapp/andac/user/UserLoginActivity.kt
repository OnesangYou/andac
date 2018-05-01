package com.dac.gapp.andac.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.dac.gapp.andac.BaseActivity
import com.dac.gapp.andac.MyPageActivity
import com.dac.gapp.andac.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

open class UserLoginActivity : BaseActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        goToJoin.setOnClickListener{
            Intent(this@UserLoginActivity, UserJoinActivity::class.java).let {
                startActivity(it)
            }
        }

        loginBtn.setOnClickListener {
            val mAuth = getAuth()

            if(emailEdit.text.isEmpty()) {
                "이메일을 입력하세요".let {
                    Timber.tag(KBJ).d(it)
                    Toast.makeText(this@UserLoginActivity, it, Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            if(passwordLoginEdit.text.isEmpty()) {
                "패스워드를 입력하세요".let {
                    Timber.tag(KBJ).d(it)
                    Toast.makeText(this@UserLoginActivity, it, Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            showProgressDialog()
            mAuth?.signInWithEmailAndPassword(emailEdit.text.toString(), passwordLoginEdit.text.toString())?.addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.tag(KBJ).d("signInWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Timber.w(task.exception, "signInWithEmail:failure")
                    Toast.makeText(this@UserLoginActivity, "Authentication failed." + task.exception,
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
        hideProgressDialog()
        if(currentUser != null){
            Toast.makeText(this@UserLoginActivity, "Authentication Success.",
                    Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MyPageActivity::class.java))
            finish()
        }

    }

}
