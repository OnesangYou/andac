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
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import timber.log.Timber
import java.util.*


class ChatActivity : BaseActivity() {
    var list: MutableList<ChatItem> = mutableListOf()

    interface OnCompleteImage {
        fun onComplete(position: Int)
    }

    private lateinit var binding: ActivityChatBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        binding = getBinding()
        val db = FirebaseFirestore.getInstance()

        setActionBarLeftImage(R.drawable.back)
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        val onCompleteImage = object : OnCompleteImage {
            override fun onComplete(position: Int) {
                    binding.ChatListView.post{ binding.ChatListView.smoothScrollToPosition(View.FOCUS_DOWN) }
            }
        }

        binding.ChatListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ColumnChatListRecyclerViewAdapter(list, onCompleteImage)
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

        var hospitalInfo: HospitalInfo? = null
        var userInfo: UserInfo? = null
        Tasks.whenAllComplete(
                getHospitalInfo(hUid)?.addOnSuccessListener { hospitalInfo = it },
                getUserInfo(uUid)?.addOnSuccessListener {
                    userInfo = it
                }
        ).addOnSuccessListener {
            db.collection("chat").document(roomId).collection("list").orderBy("time", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshots, _ ->
                        for (dc in snapshots?.documentChanges!!) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    val chatItem = dc.document.toObject(ChatItem::class.java)
                                    chatItem.mUid = mUid
                                    chatItem.objectId = dc.document.id
                                    list.add(chatItem)
                                    if (isUser() && !chatItem.isMine) {
                                        chatItem.pic = hospitalInfo?.profilePicUrl
                                        chatItem.name = hospitalInfo?.name
                                    } else if (!chatItem.isMine) {
                                        chatItem.pic = userInfo?.profilePicUrl
                                        chatItem.name = userInfo?.nickName
                                    }
                                    binding.ChatListView.scrollToPosition(list.size - 1)
                                    binding.ChatListView.apply { adapter.notifyItemChanged(list.size - 1) }
                                }
                                DocumentChange.Type.MODIFIED -> Timber.d(dc.document.data.toString())
                                DocumentChange.Type.REMOVED -> Timber.d(dc.document.data.toString())
                            }
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

        iv_image.setOnClickListener { _ ->
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