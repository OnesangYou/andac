package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.dac.gapp.andac.adapter.ColumnChatListRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.ChatMessage
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*
import timber.log.Timber


class ChatActivity : BaseActivity() {
    var list: MutableList<ChatMessage> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        var db = FirebaseFirestore.getInstance()

        ChatListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ColumnChatListRecyclerViewAdapter(applicationContext, list)
        }

        val roomId = intent.getStringExtra("roomId")
        val uUid = intent.getStringExtra("uUid")
        val hUid = intent.getStringExtra("hUid")

        btn_send.setOnClickListener { view ->
            db.collection("chat").document(roomId).collection("list")
                    .add(ChatMessage(et_message.text.toString(),hUid))
            et_message.text = null
        }


        db.collection("chat").document(roomId).collection("list")
                .addSnapshotListener { snapshots, e ->
                    for (dc in snapshots?.documentChanges!!) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> list.add(dc.document.toObject(ChatMessage::class.java))
                            DocumentChange.Type.MODIFIED -> Timber.d(dc.document.data.toString())
                            DocumentChange.Type.REMOVED -> Timber.d(dc.document.data.toString())
                        }
                    }
                }

    }
}