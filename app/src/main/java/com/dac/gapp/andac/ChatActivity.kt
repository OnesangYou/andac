package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.dac.gapp.andac.adapter.ColumnChatListRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityChatBinding
import com.dac.gapp.andac.model.firebase.ChatItem
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.alert
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*


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
        val mUid = getUid() ?: return
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
                                val chatItem = dc.document.toObject(ChatItem::class.java)
                                chatItem.mUid = mUid
                                list.add(chatItem)
                                if (isUser()) {
                                    if (chatItem.isMine) {
                                        binding.ChatListView.scrollToPosition(list.size - 1)
                                        binding.ChatListView.apply { adapter.notifyItemChanged(list.size - 1) }

                                    } else {
                                        db.collection("hospitals").document(chatItem.uid!!).get().addOnCompleteListener {
                                            val hospitalinfo = it.result.toObject(HospitalInfo::class.java)
                                            chatItem.pic = hospitalinfo!!.profilePicUrl

                                            binding.ChatListView.scrollToPosition(list.size - 1)
                                            binding.ChatListView.apply { adapter.notifyItemChanged(list.size - 1) }
                                        }
                                    }
                                } else {
                                    if (chatItem.isMine) {
                                        binding.ChatListView.scrollToPosition(list.size - 1)
                                        binding.ChatListView.apply { adapter.notifyItemChanged(list.size - 1) }
                                    } else {
                                        db.collection("users").document(chatItem.uid!!).get().addOnCompleteListener {
                                            val userInfo = it.result.toObject(UserInfo::class.java)
                                            chatItem.pic = userInfo!!.profilePicUrl

                                            binding.ChatListView.scrollToPosition(list.size - 1)
                                            binding.ChatListView.apply { adapter.notifyItemChanged(list.size - 1) }
                                        }
                                    }
                                }

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

        iv_image.setOnClickListener {
            getAlbumImage()?.subscribe { uri ->

                showProgressDialog()
                FirebaseStorage.getInstance().reference.child("chat").child(roomId).child(getUid()!!.plus(Calendar.getInstance().timeInMillis)).putFile(uri)
                        .continueWith {
                            val url = it.result.downloadUrl.toString()
                            if (!TextUtils.isEmpty(url)) {
                                db.collection("chat").document(roomId).collection("list").add(ChatItem(mUid, "", url))
                                db.collection("chatList").document(uUid).collection("attendants").document(hUid).update("lastchat", "사진")
                                db.collection("chatList").document(hUid).collection("attendants").document(uUid).update("lastchat", "사진")
                                binding.etMessage.text = null
                            }
                        }.addOnCompleteListener { hideProgressDialog() }
            }
        }
    }
}