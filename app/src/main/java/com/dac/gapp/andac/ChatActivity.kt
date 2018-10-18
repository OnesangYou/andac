package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.dac.gapp.andac.adapter.ColumnChatListRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityChatBinding
import com.dac.gapp.andac.model.firebase.ChatItem
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import timber.log.Timber


class ChatActivity : BaseActivity() {
    var list: MutableList<ChatItem> = mutableListOf()
    private lateinit var binding: ActivityChatBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        binding = getBinding()
        var db = FirebaseFirestore.getInstance()

        setActionBarLeftImage(R.drawable.back)
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        binding.ChatListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ColumnChatListRecyclerViewAdapter(list)
        }

        val roomId = intent.getStringExtra("roomId")
        val uUid = intent.getStringExtra("uUid")
        val hUid = intent.getStringExtra("hUid")
        val mUid  = getUid()?:return
        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString()
            if (!TextUtils.isEmpty(msg)) {
                db.collection("chat").document(roomId).collection("list").add(ChatItem(mUid, msg))
                db.collection("chatList").document(uUid).collection("attendants").document(hUid).update("lastchat", msg)
                db.collection("chatList").document(hUid).collection("attendants").document(uUid).update("lastchat", msg)
                binding.etMessage.text = null
            }
        }

        db.collection("chat").document(roomId).collection("list").orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, _ ->
                    for (dc in snapshots?.documentChanges!!) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                Timber.e("들어옴")
                                val chatItem  = dc.document.toObject(ChatItem::class.java)
                                chatItem.mUid = mUid
                                list.add(chatItem)
                                binding.ChatListView.scrollToPosition(list.size - 1)
                                binding.ChatListView.apply { adapter.notifyItemChanged(list.size - 1) }
                            }
                            DocumentChange.Type.MODIFIED -> Timber.d(dc.document.data.toString())
                            DocumentChange.Type.REMOVED -> Timber.d(dc.document.data.toString())
                        }
                    }
                }

        binding.ChatListView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                binding.ChatListView.post { binding.ChatListView.scrollToPosition(list.size - 1) }
            }
        }

        binding.ChatListView.setOnTouchListener { v, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
            false
        }
    }
}