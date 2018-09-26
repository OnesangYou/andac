package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.dac.gapp.andac.adapter.ColumnChatListRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityChatBinding
import com.dac.gapp.andac.model.firebase.ChatItem
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber


class ChatActivity : BaseActivity() {
    var list: MutableList<ChatItem> = mutableListOf()
    private lateinit var binding : ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        binding = getBinding()
        var db = FirebaseFirestore.getInstance()

        binding.ChatListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ColumnChatListRecyclerViewAdapter(list)
        }

        val roomId = intent.getStringExtra("roomId")
        val uUid = intent.getStringExtra("uUid")
        val hUid = intent.getStringExtra("hUid")

        binding.btnSend.setOnClickListener { view ->
            db.collection("chat").document(roomId).collection("list")
                    .add(ChatItem(getUid(),binding.etMessage.text.toString()))
            binding.etMessage.text = null
        }


        db.collection("chat").document(roomId).collection("list")
                .addSnapshotListener { snapshots, e ->
                    for (dc in snapshots?.documentChanges!!) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> list.add(dc.document.toObject(ChatItem::class.java))
                            DocumentChange.Type.MODIFIED -> Timber.d(dc.document.data.toString())
                            DocumentChange.Type.REMOVED -> Timber.d(dc.document.data.toString())
                        }
                    }
                }

    }
}