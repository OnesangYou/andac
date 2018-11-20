package com.dac.gapp.andac.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import timber.log.Timber
import java.util.stream.Collectors.toMap

class FcmInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        Timber.e("진입")
        super.onTokenRefresh()

        FirebaseInstanceId.getInstance().token?.apply {
            sendToken(this)
        }

    }

    private fun sendToken(newToken:String) {
        val token = HashMap<String,Any>()
        FirebaseAuth.getInstance().uid?.apply {

            token["value"] = newToken
            FirebaseFirestore.getInstance().collection("token").document(this).set(token)
        }
        Timber.d("Token:${newToken}")
    }
}