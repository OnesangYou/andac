package com.dac.gapp.andac

import android.os.Bundle
import com.dac.gapp.andac.base.BaseActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.hospital.activity_join_phone.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.warn
import java.util.concurrent.TimeUnit


class JoinPhoneActivity : BaseActivity(), AnkoLogger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_phone)

        // 인증번호 발송
        sendCertiCodeBtn.setOnClickListener {
                var phoneStr = phoneEdit.text.toString()
                if(phoneStr.isEmpty()){
                    toast("전화번호를 입력하세요")
                    return@setOnClickListener
                }

                if(phoneStr.startsWith("010")){
                    phoneStr = phoneStr.replace("010", "+82 10")    // 폰번호 인식 가능하도록 변경
                    debug("phoneStr:$phoneStr")
                }

                val mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // 인증 코드 완료
                        debug("onVerificationCompleted:$credential")
                        toast("onVerificationCompleted:$credential")
                        linkWithCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        warn( "onVerificationFailed", e)
                        toast("onVerificationFailed:$e")
                    }

                    override fun onCodeSent(verificationId: String?,
                                            token: PhoneAuthProvider.ForceResendingToken?) {
                        debug("onCodeSent:" + verificationId!!)
                        toast("인증코드 전송 하였습니다")

                        ConfirmCertiCodeBtn.setOnClickListener {
                            val code = certificationEdit.text.toString()
                            val credential = PhoneAuthProvider.getCredential(verificationId, code)
//                            toast("credential : $credential")

                            linkWithCredential(credential)

                        }
                    }
                }

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneStr,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this@JoinPhoneActivity,               // Activity (for callback binding)
                        mCallbacks)        // OnVerificationStateChangedCallbacks


        }
    }

    fun linkWithCredential(credential : PhoneAuthCredential) {
        showProgressDialog()
        getCurrentUser()?.linkWithCredential(credential)?.addOnCompleteListener { task ->
            hideProgressDialog()
            if (task.isSuccessful) {
                debug("linkWithCredential:success")
                val user = task.result.user

                //  전화번호 DB 업데이트
                getHospital()?.set(mapOf("cellPhone" to user.phoneNumber), SetOptions.merge())
                        ?.addOnSuccessListener {
                            toast("인증 완료되었습니다")
                            finish()
                        }
            } else {
                warn("linkWithCredential:failure", task.exception)
                toast("인증번호가 틀렸습니다")
            }

        }
    }

    override fun onBackPressed() {
        getAuth()?.let{
            it.signOut()
            super.onBackPressed()
        }
    }
}
