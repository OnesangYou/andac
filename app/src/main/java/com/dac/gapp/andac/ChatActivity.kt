package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.dac.gapp.andac.adapter.ColumnChatListRecyclerViewAdapter
import com.dac.gapp.andac.model.ChatMessage
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {
    var columnchatList: MutableList<ChatMessage> = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        var db = FirebaseFirestore.getInstance()

        var layoutmanager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        ChatListView.layoutManager = layoutmanager
        ChatListView.adapter = ColumnChatListRecyclerViewAdapter(applicationContext, columnchatList)

        btn_send.setOnClickListener { view ->

            db.collection("Chat")
                    .add(ChatMessage(et_message.text.toString()))
            et_message.text = null
        }


        db.collection("Chat").addSnapshotListener(this, { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->

            for (dc in querySnapshot!!.getDocumentChanges()) {
                when (dc.getType()) {
                    DocumentChange.Type.ADDED -> columnchatList.add(dc.document.toObject(ChatMessage::class.java))
                }
                ChatListView.adapter.notifyDataSetChanged()
            }
        })
    }

}
