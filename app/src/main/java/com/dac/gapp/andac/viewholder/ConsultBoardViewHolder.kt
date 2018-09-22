package com.dac.gapp.andac.viewholder

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.dac.gapp.andac.ChatActivity
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ConsultBoardRowBinding
import com.dac.gapp.andac.dialog.ConsultContentDialog
import com.dac.gapp.andac.model.firebase.ChatListInfo
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync

class ConsultBoardViewHolder(var context: Context?, var view: View) : RecyclerView.ViewHolder(view) {

    var binding: ConsultBoardRowBinding = DataBindingUtil.bind(view)!!

    fun onClickShowConsult(uUid: String?, hUid: String?,isOpen: Boolean?) {
        val db = FirebaseFirestore.getInstance()
        isOpen?: return
        uUid?: return

        if(isOpen){
            // 오픈 상담 신청서
            val baseActivity = context as BaseActivity
            baseActivity.getOpenConsult(uUid).get().addOnSuccessListener { querySnapshot ->
                val consultInfo = querySnapshot.toObject(ConsultInfo::class.java)
                val dialog = context?.let { ConsultContentDialog(it, consultInfo) }
                dialog?.show()
            }

        }else{
            // 지정 상담 신청서
            hUid?: return
            val baseActivity = context as BaseActivity
            baseActivity.getSelectConsult(hUid, uUid)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if(querySnapshot.isEmpty) return@addOnSuccessListener
                        querySnapshot.toObjects(ConsultInfo::class.java).also { if(it.isEmpty()) return@addOnSuccessListener }.let { it[0] }.let {consultInfo ->
                            val dialog = context?.let { ConsultContentDialog(it, consultInfo) }
                            dialog?.show()
                        }
                    }.addOnCompleteListener {
                        val task = it
                    }
        }
    }

    fun onClickStartChat(uUid: String?, hUid: String?) {
        val uUid: String = uUid ?: return
        val hUid: String = hUid ?: return

        doAsync {
            val db = FirebaseFirestore.getInstance()
            val task: Task<DocumentSnapshot> = db
                    .collection("chatList")
                    .document(hUid)
                    .collection("attendants")
                    .document(uUid)
                    .get()

            var documentSnapshot = Tasks.await(task)
            var roomId: String?
            var chatlistinfo = documentSnapshot.toObject(ChatListInfo::class.java)
            roomId = chatlistinfo?.roomId ?: db.collection("chat").document().id.let {
                val batch = db.batch()
                val firstchatlistinfo = ChatListInfo(it)
                batch.set(db.collection("chatList").document(uUid).collection("attendants").document(hUid), firstchatlistinfo)
                batch.set(db.collection("chatList").document(hUid).collection("attendants").document(uUid), firstchatlistinfo)
                batch.commit().addOnSuccessListener { void ->
                    Toast.makeText(context, "채팅방 생성 성공", Toast.LENGTH_SHORT).show()
                }
                it
            }

            context?.startActivity(Intent(context, ChatActivity::class.java).putExtra("roomId", roomId).putExtra("uUid", uUid).putExtra("hUid", hUid))

        }
    }

}