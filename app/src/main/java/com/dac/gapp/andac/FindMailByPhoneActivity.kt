package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.os.Bundle
import com.dac.gapp.andac.base.BasePhoneAuthActivity
import com.dac.gapp.andac.databinding.ActivityFindMailByPhoneBinding
import com.dac.gapp.andac.util.UiUtil
import com.google.firebase.auth.FirebaseAuthException

class FindMailByPhoneActivity : BasePhoneAuthActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_mail_by_phone)

        val binding = getBinding<ActivityFindMailByPhoneBinding>()

        // 인증번호 발송
        binding.sendCertiCodeBtn.setOnClickListener { _ ->
            sendCertiCode(binding.phoneEdit.text.toString(),
                    binding.certificationEdit,
                    binding.ConfirmCertiCodeBtn){ credential ->
                getEmailByCredential(credential).addOnSuccessListener {
                    binding.resultText.text = "당신의 이메일은 $it 입니다"
                    getAuth()?.signOut()
                }.addOnFailureListener {
                    binding.resultText.text = UiUtil.getMessageFromAuthException(it as FirebaseAuthException)
                }
            }
        }
    }
}
