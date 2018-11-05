package com.dac.gapp.andac.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import timber.log.Timber

class FcmInstanceIdService : FirebaseInstanceIdService() {

    lateinit var newToken :String
    override fun onTokenRefresh() {
        Timber.e("진입")
        super.onTokenRefresh()

        FirebaseInstanceId.getInstance().token?.apply {
            newToken =this
        }
    }

    fun getToken() : String{
        return newToken
    }
}