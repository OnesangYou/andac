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
import com.dac.gapp.andac.databinding.FragmentJoinInfoBinding
import com.dac.gapp.andac.util.getDateFormat
import com.dac.gapp.andac.util.toast
import org.jetbrains.anko.debug
import java.util.*


class JoinInfoFragment : BaseFragment() {

    lateinit var binding : FragmentJoinInfoBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflate(inflater,R.layout.fragment_join_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getBinding()
        context?.apply { resetTagEditTextChanged(binding.emailEdit) }
        binding.checkEmailBtn.setOnClickListener {
            context?.apply {
                val emailStr = binding.emailEdit.text.toString()
                if (emailStr.isEmpty()) {
                    toast("이메일을 입력하세요")
                    return@setOnClickListener
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    toast("이메일 형식이 아닙니다")
                    binding.emailEdit.text.clear()
                    return@setOnClickListener
                }

                checkDuplicatedEmail(emailStr.trim())?.addOnSuccessListener {isPossible ->
                    if (isPossible) {
                        toast("사용가능한 이메일입니다")
                        binding.emailEdit.tag = true
                    } else {
                        // 중복
                        toast("중복된 이메일이 존재합니다")
                        binding.emailEdit.text.clear()
                    }
                }
            }
        }

        context?.apply { resetTagEditTextChanged(binding.nickNameEdit) }
        binding.checkNickNameBtn.setOnClickListener {
            context?.apply {
                val nickNameStr = binding.nickNameEdit.text.toString()
                if (nickNameStr.isEmpty()) {
                    toast("닉네임을 입력하세요")
                    return@setOnClickListener
                }

                checkDuplicatedNickName(nickNameStr.trim())?.addOnSuccessListener { isPossible ->
                    if (isPossible) {
                        toast("사용가능한 닉네임입니다")
                        binding.nickNameEdit.tag = true
                    } else {
                        // 중복
                        toast("중복된 닉네임이 존재합니다")
                        binding.nickNameEdit.text.clear()
                    }
                }
            }
        }

        // 생년월일
        binding.birthEdit.setOnClickListener {
            val calendar = Calendar.getInstance().apply { set(1990, 1,1) }
            DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        Calendar.getInstance().apply {set(year,month,dayOfMonth)}.let{Date(it.timeInMillis)}.let {
                            binding.birthEdit.setText(it.getDateFormat("yyMMdd"))
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
        binding.joinBtn.setOnClickListener {
            if(binding.emailEdit.tag != true) {
                toast("Email 중복검사 하세요")
                return@setOnClickListener
            }

            if(binding.nickNameEdit.tag != true) {
                toast("NickName 중복검사 하세요")
                return@setOnClickListener
            }

            // 생년월일 검사 : 에뮬에서 가입이 안된다고 해제
//            if(birthEdit.text.isEmpty()) {
//                toast(birthEdit.hint.toString())
//                return@setOnClickListener
//            }

            (activity as JoinActivity).apply {
                // 성별
                mUserInfo.sex = view.findViewById<RadioButton>(binding.radiogroupSex.checkedRadioButtonId).text.toString()

                // 회원가입
                join(binding.emailEdit.text.toString(), binding.nickNameEdit.text.toString())
            }

        }
    }

    fun JoinActivity.join(email : String, nickName : String) {

// 닉네임 공백체크
        if(nickName == ""){
            toast("Nick Name이 공백입니다")
            return
        }

        //이메일형식체크
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("이메일 형식이 아닙니다")
            return
        }


        //비밀번호 유효성
        if (binding.passwordEdit.text.toString().length < 6) {
            toast("비밀번호는 6자리 이상입니다")
            return
        }

        // 비밀번호 일치
        if(binding.passwordEdit.text.toString() != binding.passwordConfirmEdit.text.toString()) {
            toast("패스워드를 확인하세요")
            return
        }

        mUserInfo.email = email
        mUserInfo.nickName = nickName

        // 회원가입 시도
        debug("emailEdit : " + binding.emailEdit.text)
        val mAuth = getAuth()
        showProgressDialog()
        mAuth?.createUserWithEmailAndPassword(mUserInfo.email, binding.passwordEdit.text.toString())
                ?.onSuccessTask { _ ->
                    val user = mAuth.currentUser?:throw IllegalStateException()
                    getUsers().document(user.uid).set(mUserInfo).continueWith { user }
                }
                ?.addOnSuccessListener { updateUI(it) }
                ?.addOnFailureListener{
                    // If sign in fails, display a message to the user.
                    toast("회원가입에 실패 하였습니다. ${it.localizedMessage}")
                    updateUI(null)
                }
                ?.addOnCompleteListener { hideProgressDialog() }

    }

}// Required empty public constructor
