package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.hospital.fragment_join_certi2.*
import timber.log.Timber
import java.util.regex.Pattern

class JoinCerti2Fragment : JoinBaseFragment(){
    override fun onChangeFragment() {
    }

    private lateinit var joinActivity: JoinActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_certi2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        joinActivity =  activity as JoinActivity
        // 받아온 경우 Set
        joinActivity.hospitalInfo.apply {
            if(cellPhone.isNotEmpty()) phoneEdit.setText(address2)
        }

        nextBtn.setOnClickListener {
            joinActivity.run {

                val emailStr = emailEdit.text.toString()
                val passwordStr = passwordEdit.text.toString()
                val passwordConfirmStr = passwordConfirmEdit.text.toString()
                val phoneStr = phoneEdit.text.toString()

                /* 유효성 검사 */

                //이메일형식체크
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    toast("이메일 형식이 아닙니다")
                    return@setOnClickListener
                }

                //비밀번호 유효성
                if (passwordStr.length < 6) {
                    toast("비밀번호는 6자리 이상입니다")
                    return@setOnClickListener
                }

                // 비밀번호 일치
                if(passwordStr != passwordConfirmStr) {
                    toast("패스워드를 확인하세요")
                    return@setOnClickListener
                }

                //핸드폰번호 유효성
                if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phoneStr)) {
                    toast("올바른 핸드폰 번호가 아닙니다. $phoneStr")
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

                        // uploadPicFile
                        uploadPicFile{insertDB(user, { deleteDB() })}

                    } else {
                        // If sign in fails, display a message to the user.
                        this.toast("Authentication failed." + task.exception)
                        updateUI(null)
                    }

                }
            }
        }
    }

    private fun JoinActivity.uploadPicFile(next: () -> Unit) {
        if(profilePicUri != null) {
            getHospitalsStorageRef().child(getUid()).child("profilePic.jpg").putFile(profilePicUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        hospitalInfo.profilePicUrl = taskSnapshot.downloadUrl.toString()
                        toast("KBJ, uploadPicFile Complete")
                        next.invoke()
                    }
        } else {
            next.invoke()
        }
    }

    private fun JoinActivity.insertDB(user: FirebaseUser?, function: () -> Unit) {
        getHospitals().document(user?.uid!!).set(hospitalInfo, SetOptions.merge()).addOnCompleteListener { task2 ->
            if (task2.isSuccessful) {
                toast("KBJ, insertDB Complete")
                function.invoke()
                updateUI(user)
            } else {
                this.toast("회원가입 실패")
                updateUI(null)
            }
        }
    }

    private fun JoinActivity.deleteDB() {
        toast("KBJ, deleteDB Complete")
        if (hospitalKey.isNotEmpty()) {
            getHospitals().document(hospitalKey).delete()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        (activity as JoinActivity).run {
            hideProgressDialog()
            user?.let{
                this.toast("Authentication Success.")
                finish()
            }
        }
    }

}// Required empty public constructor
