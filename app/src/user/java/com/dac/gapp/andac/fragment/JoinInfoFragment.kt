package com.dac.gapp.andac.fragment


import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.util.getDateFormat
import com.dac.gapp.andac.util.toast
import kotlinx.android.synthetic.user.fragment_join_info.*
import java.util.*


class JoinInfoFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.apply { resetTagEditTextChanged(emailEdit) }
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

        context?.apply { resetTagEditTextChanged(nickNameEdit) }
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

        // 생년월일
        birthEdit.setOnClickListener {
            val calendar = Calendar.getInstance().apply { set(1990, 1,1) }
            DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        Calendar.getInstance().apply {set(year,month,dayOfMonth)}.let{Date(it.timeInMillis)}.let {
                            birthEdit.setText(it.getDateFormat("YYMMdd"))
                            (activity as JoinActivity).mUserInfo.birthDate = it
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = Date().time
                show()
            }
        }

        // 회원가입
        joinBtn.setOnClickListener {
            if(emailEdit.tag != true) {
                toast("Email 중복검사 하세요")
                return@setOnClickListener
            }

            if(nickNameEdit.tag != true) {
                toast("NickName 중복검사 하세요")
                return@setOnClickListener
            }

            if(birthEdit.text.isEmpty()) {
                toast(birthEdit.hint.toString())
                return@setOnClickListener
            }

            (activity as JoinActivity).apply {
                // 성별
                mUserInfo.sex = view.findViewById<RadioButton>(radiogroup_sex.checkedRadioButtonId).text.toString()

                // 회원가입
                join(emailEdit.text.toString(), nickNameEdit.text.toString())
            }

        }
    }

}// Required empty public constructor
