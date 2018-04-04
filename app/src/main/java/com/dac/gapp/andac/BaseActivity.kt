package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
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

}