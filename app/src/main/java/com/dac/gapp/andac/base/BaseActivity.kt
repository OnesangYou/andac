package com.dac.gapp.andac.base

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dac.gapp.andac.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber


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
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
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

    // Context 클래스에 toast 함수 추가
    fun Context.toast(message: CharSequence) {
        // this는 Context 인스턴스!
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Timber.d(message.toString())
    }

}