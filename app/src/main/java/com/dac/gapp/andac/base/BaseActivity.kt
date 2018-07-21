@file:Suppress("DEPRECATION")

package com.dac.gapp.andac.base
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dac.gapp.andac.BuildConfig
import com.dac.gapp.andac.LoginActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.SplashActivity
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumFile
import timber.log.Timber
import java.io.File


abstract class BaseActivity : AppCompatActivity() {


    val KBJ = "KBJ"
    val GOTO_MYPAGE = "goToMyPage"
    val OBJECT_KEY = "objectKey"

    fun getUid() : String? {
        return getCurrentUser()?.uid
    }

    fun getDb() : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getHospitals(): CollectionReference = getDb().collection("hospitals")

    fun getHospital(): DocumentReference? = getUid()?.let { getHospitals().document(it) }

    fun getHospital(key : String) = getHospitals().document(key)


    fun getHospitalsStorageRef() : StorageReference = FirebaseStorage.getInstance().reference.child("hospitals")

    fun getUsers() : CollectionReference = getDb().collection("users")

    fun getUser(): DocumentReference? = getUid()?.let { getUsers().document(it) }

    fun getUser(uuid : String): DocumentReference? = getUsers().document(uuid)

    fun getAuth(): FirebaseAuth? = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = getAuth()?.currentUser

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
        mProgressDialog?.let{
            if(it.isShowing) it.dismiss()
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

    private fun startAlbumMultiImage(limitCnt : Int) : Task<MutableCollection<AlbumFile>> =
        TaskCompletionSource<MutableCollection<AlbumFile>>().run{
            kotlin.run {
                if(limitCnt == 1) Album.image(this@BaseActivity).singleChoice()
                else Album.image(this@BaseActivity).multipleChoice().selectCount(limitCnt)
            }
                    .onResult { setResult(it) }
                    .start()
            task
        }


    fun startAlbumImageUri(): Task<Uri> = startAlbumMultiImage(1).continueWith { Uri.fromFile(File(it.result.first().path)) }

    fun startAlbumImageUri(limitCnt : Int): Task<List<Uri>> =
            startAlbumMultiImage(limitCnt).continueWith {task ->
                task.result.map { Uri.fromFile(File(it.path)) }
            }

    // Boards
    fun getBoardStorageRef() : StorageReference = FirebaseStorage.getInstance().reference.child("boards")
    fun getBoards(): CollectionReference = getDb().collection("boards")
    fun getBoard(key : String): DocumentReference? = if(key.isEmpty()) null else getBoards().document(key)

    // Column
    fun getColumnStorageRef() : StorageReference = FirebaseStorage.getInstance().reference.child("columns")
    fun getColumns(): CollectionReference = getDb().collection("columns")
    fun getColumn(key : String): DocumentReference? = if(key.isEmpty()) null else getColumns().document(key)


    // My Contents
    private fun getUserContents(uid: String? = getUid()) = uid?.let { getDb().collection("userContents").document(it) }
    fun getUserBoards() = getUserContents()?.collection("boards")

    private fun restartApp() {
        val mStartActivity = Intent(this, SplashActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        System.exit(0)
    }

    fun goToLogin(gotoMyPage : Boolean = false){
        Intent(this, LoginActivity::class.java).let {
            if(gotoMyPage) it.putExtra(GOTO_MYPAGE, true)
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

}