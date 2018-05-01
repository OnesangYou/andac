package com.dac.gapp.andac.join


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dac.gapp.andac.HospitalJoinActivity
import com.dac.gapp.andac.MainActivity
import com.dac.gapp.andac.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.hospital.fragment_hospital_join_certi2.*
import timber.log.Timber
import java.util.regex.Pattern

class HospitalJoinCerti2Fragment : HospitalJoinBaseFragment(){
    override fun onChangeFragment() {
    }

    private lateinit var hospitalJoinActivity: HospitalJoinActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hospital_join_certi2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hospitalJoinActivity =  activity as HospitalJoinActivity
        // 받아온 경우 Set
        hospitalJoinActivity.hospitalInfo.apply {
            if(cellPhone.isNotEmpty()) phoneEdit.setText(address2)
        }

        nextBtn.setOnClickListener {
            hospitalJoinActivity.run {

                val emailStr = emailEdit.text.toString()
                val passwordStr = passwordEdit.text.toString()
                val passwordConfirmStr = passwordConfirmEdit.text.toString()
                val phoneStr = phoneEdit.text.toString()

                /* 유효성 검사 */

                //이메일형식체크
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    Toast.makeText(context, "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                //비밀번호 유효성
                if (passwordStr.length < 6) {
                    Toast.makeText(context, "비밀번호는 6자리 이상입니다", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 비밀번호 일치
                if(passwordStr != passwordConfirmStr) {
                    "패스워드를 확인하세요".let {
                        Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
                        Timber.d(it)
                    }
                    return@setOnClickListener
                }

                //핸드폰번호 유효성
                if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phoneStr)) {
                    Toast.makeText(context, "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                hospitalInfo.apply{
                    email = emailStr
                    cellPhone = phoneStr
                }

                // 파베 회원가입
                // 회원가입 시도
                val mAuth = getAuth()
                showProgressDialog()
                mAuth?.createUserWithEmailAndPassword(emailStr, passwordStr)?.addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("createUserWithEmail:success")
                        val user = mAuth.currentUser

                        // DB insert
                        getHospitals().document(user?.uid!!).set(hospitalInfo, SetOptions.merge()).addOnCompleteListener{ task2 ->
                            if(task2.isSuccessful){
                                updateUI(user)
                            } else {
                                "회원가입 실패".let {
                                    Timber.d(it)
                                    Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
                                    updateUI(null)
                                }
                            }
                        }
                        if(hospitalKey.isNotEmpty()){
                            getHospitals().document(hospitalKey).delete()
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Timber.tag(KBJ).w(task.exception, "createUserWithEmail:failure")
                        Toast.makeText(this, "Authentication failed." + task.exception,
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                }
            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        (activity as HospitalJoinActivity).run {
            hideProgressDialog()
            if(user != null){
                Toast.makeText(this, "Authentication Success.",
                        Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}// Required empty public constructor
