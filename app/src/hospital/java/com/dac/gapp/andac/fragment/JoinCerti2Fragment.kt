package com.dac.gapp.andac.fragment


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.R
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.hospital.fragment_join_certi2.*
import timber.log.Timber
import java.util.regex.Pattern

class JoinCerti2Fragment : JoinBaseFragment(){
    override fun onChangeFragment() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_certi2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 받아온 경우 Set
        getJoinActivity().hospitalInfo.apply {
            if(cellPhone.isNotEmpty()) phoneEdit.setText(address2)
        }

        nextBtn.setOnClickListener {
            getJoinActivity().run {

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
                        Timber.d("createU serWithEmail:success")
                        val user = mAuth.currentUser

                        // 사진 파일 한꺼번에 올리기
                        mutableListOf<Task<String>>().apply{
                            profilePicUri?.let{add(uploadPicFileTask("profilePic.jpg", it).addOnSuccessListener { url -> hospitalInfo.profilePicUrl = url})}
                            bankAccountPicUri?.let{add(uploadPicFileTask("bankAccountPic.jpg", it).addOnSuccessListener { url -> hospitalInfo.bankAccountPicUrl = url})}
                            busiRegiPicUri?.let{add(uploadPicFileTask("busiRegiPic.jpg", it).addOnSuccessListener { url -> hospitalInfo.busiRegiPicUrl = url})}
                        }.let { Tasks.whenAll(it).addOnSuccessListener {
                            insertDB(user).onSuccessTask { deleteDB() }
                        }}

                    } else {
                        // If sign in fails, display a message to the user.
                        toast("Authentication failed." + task.exception as Any?)
                        updateUI(null)
                    }

                }
            }
        }
    }

    private fun JoinActivity.uploadPicFileTask(fileName : String , picUri : Uri): Task<String> {
        return getHospitalsStorageRef().child(getUid()).child(fileName).putFile(picUri).continueWith {
                    toast("uploadPicFile Complete")
                    it.result.downloadUrl.toString()
                }
    }

    private fun JoinActivity.insertDB(user: FirebaseUser?): Task<Void> {
        return getHospitals().document(user?.uid!!).set(hospitalInfo, SetOptions.merge()).addOnCompleteListener { task2 ->
            if (task2.isSuccessful) {
                toast("KBJ, insertDB Complete")
                updateUI(user)
            } else {
                this.toast("회원가입 실패")
                updateUI(null)
            }
        }
    }

    private fun JoinActivity.deleteDB(): Task<Void> {
        return if(hospitalKey.isNotEmpty()) getHospitals().document(hospitalKey).delete()
        else TaskCompletionSource<Void>().task
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