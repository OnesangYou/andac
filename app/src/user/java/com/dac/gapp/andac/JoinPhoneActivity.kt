package com.dac.gapp.andac

import android.os.Bundle
import com.dac.gapp.andac.base.BasePhoneAuthActivity
import com.dac.gapp.andac.databinding.ActivityJoinPhoneBinding
import org.jetbrains.anko.AnkoLogger

class JoinPhoneActivity : BasePhoneAuthActivity(), AnkoLogger {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_phone)

        val binding = getBinding<ActivityJoinPhoneBinding>()

        // 인증번호 발송
        binding.sendCertiCodeBtn.setOnClickListener { _ ->
            sendCertiCode(binding.phoneEdit.text.toString(),
                    binding.certificationEdit,
                    binding.ConfirmCertiCodeBtn){
                linkWithCredential(it)
            }
        }
    }

}