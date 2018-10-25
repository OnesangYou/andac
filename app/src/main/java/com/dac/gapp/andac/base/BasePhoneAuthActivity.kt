package com.dac.gapp.andac.base

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.EditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.SetOptions
import java.util.concurrent.TimeUnit

@SuppressLint("Registered")
open class BasePhoneAuthActivity : BaseActivity() {

    fun sendCertiCode(phoneStr: String, codeEditText: EditText, confirmCertiCodeBtn : Button, callback: (PhoneAuthCredential) -> Unit) {
        if (phoneStr.isEmpty()) {
            toast("전화번호를 입력하세요")
            return
        }

        val modifyPhoneStr = if (phoneStr.startsWith("010")) {
            phoneStr.replace("010", "+82 10")    // 폰번호 인식 가능하도록 변경
        } else phoneStr

        val mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // 인증 코드 완료
                toast("onVerificationCompleted:$credential")
                callback(credential)
//                linkWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                toast("onVerificationFailed:$e")
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken?) {
                toast("인증코드 전송 하였습니다")

                confirmCertiCodeBtn.setOnClickListener {
                    val code = codeEditText.text.toString()
                    if(code.isEmpty()) {
                        toast( codeEditText.hint.toString() )
                        return@setOnClickListener
                    }
                    val credential = PhoneAuthProvider.getCredential(verificationId?:return@setOnClickListener, code)
                    callback(credential)
//                    linkWithCredential(credential)
                }
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                modifyPhoneStr,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this@BasePhoneAuthActivity,               // Activity (for callback binding)
                mCallbacks)
    }

    fun linkWithCredential(credential : PhoneAuthCredential) {
        showProgressDialog()
        getCurrentUser()?.linkWithCredential(credential)?.addOnCompleteListener { task ->
            hideProgressDialog()
            if (task.isSuccessful) {
                val user = task.result.user

                //  전화번호 DB 업데이트
                getUser()?.set(mapOf("cellPhone" to user.phoneNumber), SetOptions.merge())
                        ?.addOnSuccessListener {
                            toast("인증 완료되었습니다")
                            finish()
                        }
            } else {
//                toast(task.exception?.message.toString())
                toast("인증번호가 틀렸습니다")
            }

        }
    }

    fun getEmailByCredential(credential : PhoneAuthCredential) =
            FirebaseAuth.getInstance().signInWithCredential(credential).continueWith {
                it.result.user.email
            }


    override fun onBackPressed() {
        getAuth()?.let{
            it.signOut()
            super.onBackPressed()
        }
    }
}
