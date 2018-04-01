package com.dac.gapp.andac.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dac.gapp.andac.BaseActivity
import com.dac.gapp.andac.R
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
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
    
    private var mCallbackManager : CallbackManager? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create()
        facebookLoginBtn.setReadPermissions("email", "public_profile")
        facebookLoginBtn.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                Log.d(KBJ, "facebook:onSuccess:" + loginResult)
                handleFacebookAccessToken(loginResult.accessToken)



            }

            override fun onCancel() {
                Log.d(KBJ, "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d(KBJ, "facebook:onError", error)
                // ...
            }
        })

    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        Log.d(KBJ, "handleFacebookAccessToken:" + token)

        // 페북 토큰, 파베에 넣음
        val credential = FacebookAuthProvider.getCredential(token.token)

        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    Log.d(KBJ, "complete!!!")
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(KBJ, "signInWithCredential:success")
                        val user = mAuth!!.currentUser
                        updateUI(user, token)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(KBJ, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        //updateUI(null)
                    }
                }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser, null)
    }

    private fun updateUI(currentUser: FirebaseUser?, accessToken: AccessToken?) {
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

                    // 페북 토큰이 없는 경우
                    if(accessToken == null){
                        // 바로 회원가입
                        startActivity(Intent(this@LoginActivity, JoinActivity::class.java))
                        return@addOnCompleteListener
                    }

                    // Get Email, Name
                    val request = GraphRequest.newMeRequest(
                            accessToken
                    ) { `object`, response ->

                        Log.v("LoginActivity", response.toString())

                        val email = `object`.getString("email")
                        val name = `object`.getString("name")


                        Toast.makeText(this@LoginActivity, "$email / $name ", Toast.LENGTH_SHORT).show()

                        Log.v(KBJ, "$email / $name ")

                        // 정보를 가지고 회원가입 액티비티 이동
                        val nextIntent = Intent(this@LoginActivity, JoinActivity::class.java)
                        nextIntent.putExtra("email", email)
                        nextIntent.putExtra("name", name)
                        startActivity(nextIntent)

                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender,birthday") // Gender, Birthday를 나중에 가져올수 있으면 가져오기
                    request.parameters = parameters
                    request.executeAsync()

                }
            }
        }

    }

}
