package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.app.Activity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("Registered")
open class BaseActivity : Activity() {

    val KBJ = "KBJ"
    fun getDb() : FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun getUsers() : CollectionReference {
        return getDb().collection("Users")
    }

    fun getUsers(uuid : String): DocumentReference? {
        return getUsers().document(uuid)
    }

}