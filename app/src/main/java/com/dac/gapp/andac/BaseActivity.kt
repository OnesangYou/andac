package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    val KBJ = "KBJ"
    fun getDb() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun getUsers() : CollectionReference {
        return getDb().collection("users")
    }

    fun getUsers(uuid : String): DocumentReference? {
        return getUsers().document(uuid)
    }

    fun getAuth(): FirebaseAuth? {
        return FirebaseAuth.getInstance()
    }

    fun getCurrentUser(): FirebaseUser? {
        return getAuth()?.currentUser
    }

    @VisibleForTesting
    var mProgressDialog: ProgressDialog? = null

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

}