@file:Suppress("DEPRECATION")

package com.dac.gapp.andac.base

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dac.gapp.andac.BuildConfig
import com.dac.gapp.andac.R
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


@Suppress("LABEL_NAME_CLASH")
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    val KBJ = "KBJ"

    fun getUid() : String {
        return getCurrentUser()!!.uid
    }

    fun getDb() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun getHospitals(): CollectionReference {
        return getDb().collection("hospitals")
    }

    fun getHospital(): DocumentReference {
        return getHospitals().document(getUid())
    }


    fun getHospitalsStorageRef() : StorageReference {
        return FirebaseStorage.getInstance().reference.child("hospitals")
    }

    fun getUsers() : CollectionReference {
        return getDb().collection("users")
    }

    fun getUser(): DocumentReference? {
        return getUsers().document(getUid())
    }

    fun getUser(uuid : String): DocumentReference? {
        return getUsers().document(uuid)
    }

    fun getAuth(): FirebaseAuth? {
        return FirebaseAuth.getInstance()
    }

    fun getCurrentUser(): FirebaseUser? {
        return getAuth()?.currentUser
    }

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
            if(it.isShowing){
                it.dismiss()
            }
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

    fun isHospital(): Boolean {
        return BuildConfig.FLAVOR == "hospital"
    }

    fun isUser(): Boolean {
        return BuildConfig.FLAVOR == "user"
    }

    private fun startAlbumMultiImage(limitCnt : Int) : Task<MutableCollection<AlbumFile>> {
        return TaskCompletionSource<MutableCollection<AlbumFile>>().run{
            kotlin.run {
                if(limitCnt == 1) return@run Album.image(this@BaseActivity).singleChoice()
                return@run Album.image(this@BaseActivity).multipleChoice().selectCount(limitCnt)
            }
                    .onResult { setResult(it) }
                    .start()
            task
        }
    }


    fun startAlbumImageUri(): Task<Uri> {
        return startAlbumMultiImage(1).continueWith { Uri.fromFile(File(it.result.first().path)) }
    }

    fun startAlbumMultiImageUri(limitCnt : Int): Task<List<Uri>> {
        return startAlbumMultiImage(limitCnt).continueWith {task ->
            task.result.map { Uri.fromFile(File(it.path)) }
        }
    }

    fun getBoardStorageRef() : StorageReference {
        return FirebaseStorage.getInstance().reference.child("boards")
    }

    fun getBoards(): CollectionReference {
        return getDb().collection("boards")
    }

}