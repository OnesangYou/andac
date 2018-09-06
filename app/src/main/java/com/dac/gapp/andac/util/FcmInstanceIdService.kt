package com.dac.gapp.andac.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FcmInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        FirebaseInstanceId.getInstance().token?.let { token ->
            FirebaseAuth.getInstance().uid?.let { uid ->
                FirebaseFirestore.getInstance().collection("token").document(uid).set(token)
            }
        }
    }
}