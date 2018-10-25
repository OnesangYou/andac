package com.dac.gapp.andac

import android.os.Bundle
import com.dac.gapp.andac.base.BasePhoneAuthActivity
import com.dac.gapp.andac.databinding.ActivityJoinPhoneBinding
import org.jetbrains.anko.AnkoLogger


class JoinPhoneActivity : BasePhoneAuthActivity(), AnkoLogger {
    lateinit var binding: ActivityJoinPhoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_phone)
        binding = getBinding()
        hideActionBar()
        // 인증번호 발송
        binding.sendCertiCodeBtn.setOnClickListener { _ ->
            sendCertiCode(binding.phoneEdit.text.toString(),
                    binding.certificationEdit,
                    binding.ConfirmCertiCodeBtn){
                linkWithCredential(it)
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
