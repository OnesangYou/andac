package com.dac.gapp.andac.fragment


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.R
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.hospital.fragment_join_certi2.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
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
            if (cellPhone.isNotEmpty()) phoneEdit.setText(address2)
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
                if (passwordStr != passwordConfirmStr) {
                    toast("패스워드를 확인하세요")
                    return@setOnClickListener
                }

                //핸드폰번호 유효성
                if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phoneStr)) {
                    toast("올바른 핸드폰 번호가 아닙니다. $phoneStr")
                    return@setOnClickListener
                }

                hospitalInfo.apply {
                    email = emailStr
                    cellPhone = phoneStr
                }

                // 파베 회원가입
                // 회원가입 시도
                val mAuth = getAuth()
                showProgressDialog()
                mAuth?.createUserWithEmailAndPassword(emailStr, passwordStr)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("createU serWithEmail:success")
                        val user = mAuth.currentUser

                        // 사진 파일 한꺼번에 올리기
                        mutableListOf<Task<String>>().apply {
                            profilePicUri?.let { uploadPicFileTask("profilePic.jpg", it)?.addOnSuccessListener { url -> hospitalInfo.profilePicUrl = url }?.let { it1 -> add(it1) } }
                            bankAccountPicUri?.let { uploadPicFileTask("bankAccountPic.jpg", it)?.addOnSuccessListener { url -> hospitalInfo.bankAccountPicUrl = url }?.let { it1 -> add(it1) } }
                            busiRegiPicUri?.let { uploadPicFileTask("busiRegiPic.jpg", it)?.addOnSuccessListener { url -> hospitalInfo.busiRegiPicUrl = url }?.let { it1 -> add(it1) } }
                        }.let {
                            Tasks.whenAll(it).addOnSuccessListener {
                                insertDB(user).onSuccessTask { deleteDB() }
                            }
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        toast("Authentication failed." + task.exception as Any?)
                        updateUI(null)
                    }

                }
            }
        }

        // 메일 중복 확인
        busiRegiUploadBtn.setOnClickListener {
            context?.apply {

                val emailStr = emailEdit.text.toString()


                if (emailStr.isEmpty()) {
                    toast("이메일을 입력하세요")
                    return@setOnClickListener
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    toast("이메일 형식이 아닙니다")
                    return@setOnClickListener
                }

                showProgressDialog()
                Tasks.whenAllSuccess<QuerySnapshot>(
                        getHospitals().whereEqualTo("email", emailStr).get(),
                        getUsers().whereEqualTo("email", emailStr).get()
                ).addOnSuccessListener {
                    if (it.map { querySnapshot -> querySnapshot.isEmpty }.all { it }) {
                        toast("사용가능한 이메일입니다")
                    } else {
                        // 중복
                        toast("중복된 이메일이 존재합니다")
                        emailEdit.text.clear()
                    }

                }.addOnCompleteListener { hideProgressDialog() }

            }
        }

        // TODO : 인증번호 발송
        sendCertiCodeBtn.setOnClickListener {
            context?.apply {
                val TAG = "certiCodeBtn"
                var phoneStr = phoneEdit.text.toString()
                if(phoneStr.isEmpty()){
                    toast("전화번호를 입력하세요")
                    return@setOnClickListener
                }

                if(phoneStr.startsWith("010")){
                    phoneStr = phoneStr.replace("010", "+82 10")
                    Log.d(TAG, "phoneStr:$phoneStr")
                }

                var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.

                        Log.d(TAG, "onVerificationCompleted:$credential")
                        toast("onVerificationCompleted:$credential")

//                    // [START_EXCLUDE silent]
//                    mVerificationInProgress = false
//                    // [END_EXCLUDE]
//
//                    // [START_EXCLUDE silent]
//                    // Update the UI and attempt sign in with the phone credential
//                    updateUI(STATE_VERIFY_SUCCESS, credential)
//                    // [END_EXCLUDE]
//                    signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w(TAG, "onVerificationFailed", e)
                        toast("onVerificationFailed:$e")

//                    // [START_EXCLUDE silent]
//                    mVerificationInProgress = false
//                    // [END_EXCLUDE]
//
//                    if (e is FirebaseAuthInvalidCredentialsException) {
//                        // Invalid request
//                        // [START_EXCLUDE]
//                        mPhoneNumberField.setError("Invalid phone number.")
//                        // [END_EXCLUDE]
//                    } else if (e is FirebaseTooManyRequestsException) {
//                        // The SMS quota for the project has been exceeded
//                        // [START_EXCLUDE]
//                        Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
//                                Snackbar.LENGTH_SHORT).show()
//                        // [END_EXCLUDE]
//                    }
//
//                    // Show a message and update the UI
//                    // [START_EXCLUDE]
//                    updateUI(STATE_VERIFY_FAILED)
//                    // [END_EXCLUDE]
                    }

                    override fun onCodeSent(verificationId: String?,
                                            token: PhoneAuthProvider.ForceResendingToken?) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d(TAG, "onCodeSent:" + verificationId!!)
                        toast("onCodeSent:" + verificationId!!)

//                    // Save verification ID and resending token so we can use them later
//                    mVerificationId = verificationId
//                    mResendToken = token
//
//                    // [START_EXCLUDE]
//                    // Update UI
//                    updateUI(STATE_CODE_SENT)
//                    // [END_EXCLUDE]
                    }
                }


                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneStr,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        context!!,               // Activity (for callback binding)
                        mCallbacks)        // OnVerificationStateChangedCallbacks


            }
        }
    }

    private fun JoinActivity.uploadPicFileTask(fileName : String , picUri : Uri): Task<String>? {
        return getUid()?.let {
            getHospitalsStorageRef().child(it).child(fileName).putFile(picUri).continueWith {
                toast("uploadPicFile Complete")
                it.result.downloadUrl.toString()
            }
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
