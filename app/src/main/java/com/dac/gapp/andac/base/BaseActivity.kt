@file:Suppress("DEPRECATION")

package com.dac.gapp.andac.base

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.dac.gapp.andac.BuildConfig
import com.dac.gapp.andac.LoginActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.SplashActivity
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.dac.gapp.andac.util.UiUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumFile
import kotlinx.android.synthetic.main.layout_toolbar.*
import timber.log.Timber
import java.io.File


abstract class BaseActivity : AppCompatActivity() {


    val KBJ = "KBJ"
    val GOTO_MYPAGE = "goToMyPage"
    val OBJECT_KEY = "objectKey"

    val profilePicJpgStr = "profilePic.jpg"
    val bankAccountPicJpgStr = "bankAccountPic.jpg"
    val busiRegiPicJpgStr = "busiRegiPic.jpg"


    fun getUid(): String? {
        return getCurrentUser()?.uid
    }

    fun getDb(): FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getHospitals(): CollectionReference = getDb().collection("hospitals")

    fun getHospital(): DocumentReference? = getUid()?.let { getHospitals().document(it) }

    fun getHospital(key: String) = getHospitals().document(key)

    fun getAdRequests(): CollectionReference = getDb().collection("adRequests")

    fun getAdRequest(): DocumentReference? = getUid()?.let { getAdRequests().document(it) }

    fun getHospitalsStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("hospitals")

    fun getUsers(): CollectionReference = getDb().collection("users")

    fun getUser(): DocumentReference? = getUid()?.let { getUsers().document(it) }

    fun getUser(uuid: String): DocumentReference? = getUsers().document(uuid)

    fun getAuth(): FirebaseAuth? = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = getAuth()?.currentUser
            ?.also{it.phoneNumber?.isEmpty()?.let{ isEmpty ->
                if(isEmpty) goToLogin()
            }} // 폰등록 안되 있는 경우는 login 이동

    private var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        mProgressDialog?.let {
            if (it.isShowing) it.dismiss()
        }
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

    private var mToast: Toast? = null
    // Context 클래스에 toast 함수 추가
    fun toast(message: CharSequence) {
        // this는 Context 인스턴스!
        Toast.makeText(this, message, Toast.LENGTH_SHORT).let {
            mToast = it
            it.show()
        }
        Timber.d(message.toString())
    }

    private var backKeyPressedTime: Long = 0

    fun backPressed() {

        val time = 2000
        if (System.currentTimeMillis() <= backKeyPressedTime + time) {
            mToast?.cancel()
            ActivityCompat.finishAffinity(this@BaseActivity)
        } else {
            showGuide()
            backKeyPressedTime = System.currentTimeMillis()
        }

    }

    private fun showGuide() {
        toast("한번 더 누르면 종료됩니다.")
    }

    fun getUserInfo(): Task<UserInfo>? {
        return getUser()?.get()?.continueWith { it.result.toObject(UserInfo::class.java) }
    }

    fun isHospital(): Boolean = BuildConfig.FLAVOR == "hospital"

    fun isUser(): Boolean = BuildConfig.FLAVOR == "user"

    private fun startAlbumMultiImage(limitCnt: Int): Task<MutableCollection<AlbumFile>> =
            TaskCompletionSource<MutableCollection<AlbumFile>>().run {
                kotlin.run {
                    if (limitCnt == 1) Album.image(this@BaseActivity).singleChoice()
                    else Album.image(this@BaseActivity).multipleChoice().selectCount(limitCnt)
                }
                        .onResult { setResult(it) }
                        .start()
                task
            }


    fun startAlbumImageUri(): Task<Uri> = startAlbumMultiImage(1).continueWith { Uri.fromFile(File(it.result.first().path)) }

    fun startAlbumImageUri(limitCnt: Int): Task<List<Uri>> =
            startAlbumMultiImage(limitCnt).continueWith { task ->
                task.result.map { Uri.fromFile(File(it.path)) }
            }

    // Ads
    fun getAdStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("ads")

    // Boards
    fun getBoardStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("boards")

    fun getBoards(): CollectionReference = getDb().collection("boards")
    fun getBoard(key: String): DocumentReference? = if (key.isEmpty()) null else getBoards().document(key)
    private fun getUserContents(uid: String? = getUid()) = uid?.let { getDb().collection("userContents").document(it) }
    fun getUserBoards() = getUserContents()?.collection("boards")
    fun getViewedColumn() = getUserContents()?.collection("viewedColumns")


    // Column
    fun getColumnStorageRef(): StorageReference = FirebaseStorage.getInstance().reference.child("columns")

    fun getColumns(): CollectionReference = getDb().collection("columns")
    fun getColumn(key: String): DocumentReference? = if (key.isEmpty()) null else getColumns().document(key)
    private fun getHospitalContents(uid: String? = getUid()) = uid?.let { getDb().collection("hospitalContents").document(it) }
    fun getHospitalColumns() = getHospitalContents()?.collection("columns")


    private fun restartApp() {
        val mStartActivity = Intent(this, SplashActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        System.exit(0)
    }

    fun goToLogin(gotoMyPage: Boolean = false) {
        Intent(this, LoginActivity::class.java).let {
            if (gotoMyPage) it.putExtra(GOTO_MYPAGE, true)
            startActivity(it)
        }
    }

    fun getHospitalInfo(uid: String? = getUid()) = uid?.let { getHospital(it).get().continueWith { it.result.toObject(HospitalInfo::class.java) } }


    fun getToolBar(): ToolBar {
        return ToolBar(imgviewTitle, txtviewTitle, imgviewLeft, imgviewRight)
    }

    class ToolBar(val imgviewTitle: ImageView, val txtviewTitle: TextView, val imgviewLeft: ImageView, val imgviewRight: ImageView) {

        fun setTitle(resId: Int) {
            if (imgviewTitle != null) {
                imgviewTitle.setBackgroundResource(resId)
                UiUtil.visibleOrGone(false, txtviewTitle)
                UiUtil.visibleOrGone(true, imgviewTitle)
            }
        }

        fun setTitle(title: String) {
            if (txtviewTitle != null) {
                txtviewTitle.text = title
                UiUtil.visibleOrGone(true, txtviewTitle)
                UiUtil.visibleOrGone(false, imgviewTitle)
            }
        }

        fun setRight(resId: Int) {
            if (imgviewRight != null) {
                imgviewRight.setBackgroundResource(resId)
            }
        }
    }

    public fun changeFragment(newFragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.layoutFragmentContainer, newFragment)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }

    @SuppressLint("SetTextI18n")
    fun findPassword(){
        val email = AutoCompleteTextView(this@BaseActivity)
        email.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        val builder = AlertDialog.Builder(this@BaseActivity)


        val title = TextView(this@BaseActivity)
        title.text = "[비밀번호 찾기]\n\n가입하신 메일을 알려주세요.\n비밀번호 변경 페이지를 메일로 보내드립니다."
        title.gravity = Gravity.CENTER
        title.setPadding(0, 90, 0, 40)
        title.textSize = 15f

        builder.setView(email)
                .setCustomTitle(title)
                .setPositiveButton("확인") { _, _ ->
                    val emailAddress = email.text.toString()

                    if (emailAddress != "") {
                        val auth = getAuth()
                        auth?.sendPasswordResetEmail(emailAddress)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        toast( "메일 전송 완료")
                                    } else {
                                        toast("메일 주소가 올바르지 않습니다")
                                    }

                                }
                    } else {
                        toast("다시 시도해주세요")
                    }

                }.show()
    }

    // 중복 메일 검사
    fun checkDuplicatedEmail(emailStr : String): Task<Boolean>? {
        showProgressDialog()
        return Tasks.whenAllSuccess<QuerySnapshot>(
                getHospitals().whereEqualTo("email", emailStr).get(),
                getUsers().whereEqualTo("email", emailStr).get()
        )
                .addOnCompleteListener { hideProgressDialog() }
                .continueWith { it.result.map { querySnapshot -> querySnapshot.isEmpty }.all { it } }
    }

    // 중복 닉네임 검사
    fun checkDuplicatedNickName(NickNameStr : String): Task<Boolean>? {
        showProgressDialog()
        return Tasks.whenAllSuccess<QuerySnapshot>(
                getUsers().whereEqualTo("nickName", NickNameStr).get()
        )
                .addOnCompleteListener { hideProgressDialog() }
                .continueWith { it.result.map { querySnapshot -> querySnapshot.isEmpty }.all { it } }
    }

    // 텍스트가 변하면 tag reset
    fun resetTagEditTextChanged(editText : EditText) {
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
}