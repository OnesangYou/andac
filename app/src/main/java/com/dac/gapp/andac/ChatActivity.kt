package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.annotation.NonNull
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.dac.gapp.andac.adapter.ColumnChatListRecyclerViewAdapter
import com.dac.gapp.andac.adapter.ColumnTitleRecyclerViewAdapter
import com.dac.gapp.andac.model.ChatMessage
import com.dac.gapp.andac.model.columnTitle
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.nio.file.Files.exists
import com.google.firebase.firestore.DocumentChange




class ChatActivity : AppCompatActivity() {
    var columnchatList : MutableList<ChatMessage> = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        var db = FirebaseFirestore.getInstance()
        var message = ChatMessage("hi")


        prepareData()
        var layoutmanager : RecyclerView.LayoutManager = LinearLayoutManager(this)

        ChatListView.layoutManager = layoutmanager
        ChatListView.adapter = ColumnChatListRecyclerViewAdapter(applicationContext,columnchatList)

        btn_send.setOnClickListener { view -> db.collection("Chat")
                .add(ChatMessage(et_message.text.toString()))
             }


        db.collection("Chat").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    Log.d("test", "DocumentSnapshot data: " + document)
                } else {
                    Log.d("test", "No such document")
                }
            } else {
                Log.d("test", "get failed with ", task.exception)
            }
        }
    }


    fun prepareData(){
        for(i in 1..10){

            columnchatList.add(ChatMessage("412421"))
        }
    }


}
