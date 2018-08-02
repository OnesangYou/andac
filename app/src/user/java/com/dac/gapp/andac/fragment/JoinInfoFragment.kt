package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.dac.gapp.andac.R
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.base.BaseFragment
import kotlinx.android.synthetic.user.fragment_join_info.*


class JoinInfoFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        joinBtn.setOnClickListener {
            if(emailEdit.tag != true) {
                context?.toast("Email 중복검사 하세요")
                return@setOnClickListener
            }

            if(nickNameEdit.tag != true) {
                context?.toast("NickName 중복검사 하세요")
                return@setOnClickListener
            }

            (activity as JoinActivity).join(emailEdit.text.toString(), nickNameEdit.text.toString())
        }

        // 텍스트가 변하면 tag reset
        val addTextChangedListener = {editText : EditText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // 입력되는 텍스트에 변화가 있을 때
                    editText.tag = false
                }
                override fun afterTextChanged(arg0: Editable) {
                    // 입력이 끝났을 때
                }
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    // 입력하기 전에
                }
            })
        }

        addTextChangedListener(emailEdit)
        checkEmailBtn.setOnClickListener {
            context?.apply {
                val emailStr = emailEdit.text.toString()
                if (emailStr.isEmpty()) {
                    toast("이메일을 입력하세요")
                    return@setOnClickListener
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    toast("이메일 형식이 아닙니다")
                    emailEdit.text.clear()
                    return@setOnClickListener
                }

                checkDuplicatedEmail(emailStr)?.addOnSuccessListener {isPossible ->
                    if (isPossible) {
                        toast("사용가능한 이메일입니다")
                        emailEdit.tag = true
                    } else {
                        // 중복
                        toast("중복된 이메일이 존재합니다")
                        emailEdit.text.clear()
                    }
                }
            }
        }

        addTextChangedListener(nickNameEdit)
        checkNickNameBtn.setOnClickListener {
            context?.apply {
                val nickNameStr = nickNameEdit.text.toString()
                if (nickNameStr.isEmpty()) {
                    toast("닉네임을 입력하세요")
                    return@setOnClickListener
                }

                checkDuplicatedNickName(nickNameStr)?.addOnSuccessListener { isPossible ->
                    if (isPossible) {
                        toast("사용가능한 닉네임입니다")
                        nickNameEdit.tag = true
                    } else {
                        // 중복
                        toast("중복된 닉네임이 존재합니다")
                        nickNameEdit.text.clear()
                    }
                }
            }
        }
    }

}// Required empty public constructor
