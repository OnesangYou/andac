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
import com.dac.gapp.andac.enums.ConsultStatus
import com.dac.gapp.andac.model.firebase.ChatListInfo
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.doAsync

class ConsultBoardViewHolder(var context: Context?, var view: View) : RecyclerView.ViewHolder(view) {

    var binding: ConsultBoardRowBinding = DataBindingUtil.bind(view)!!
    var isHospital = (context as BaseActivity).isHospital()

    fun onClickShowConsult(uUid: String?, hUid: String?,isOpen: Boolean?) {
        isOpen?: return
        uUid?: return

        if(isOpen){
            // 오픈 상담 신청서
            val baseActivity = context as BaseActivity
            baseActivity.getOpenConsult(uUid)?.get()?.addOnSuccessListener { querySnapshot ->
                val consultInfo = querySnapshot.toObject(ConsultInfo::class.java)
                val dialog = context?.let { ConsultContentDialog(it, consultInfo) }
                dialog?.show()
            }

        }else{
            // 지정 상담 신청서
            (context as BaseActivity).selectConsultDialog(hUid, uUid)
        }
    }

    fun onClickStartChat(uUid: String?, hUid: String?,isOpen: Boolean?) {
        val uUid: String = uUid ?: return

        // 상담하기
        if(isOpen?:return){
            // 오픈 상담 신청
            (context as BaseActivity).apply {
                val hUid: String = getUid() ?: return
                showProgressDialog()
                getSelectConsultInfo(hUid, uUid).continueWithTask { task -> task.result
                        ?.let {// 신청한적 있는 병원일 경우
                            toast("해당 병원에 상담신청이 이미 존재합니다")
                            goChat(hUid,uUid)
                            task as Task<Void>
                        }
                        ?:let { activity ->
                            // 신청한적 없는 병원일 경우
                            getOpenConsult(uUid)?.get()?.continueWithTask { querySnapshot ->
                                // 오픈 내용을 지정으로 옮기기
                                val consultInfo = querySnapshot.result.toObject(ConsultInfo::class.java)
                                        ?.apply {
                                            hospitalId = hUid
                                            status = ConsultStatus.CONSULTING.value
                                        }?:throw IllegalStateException()
                                getSelectConsults().document().set(consultInfo)
                                        .addOnSuccessListener {
                                            // TODO : 채팅방 생성
                                            toast("상담 신청 완료하였습니다")
                                            addCountCounselors(hUid)
                                            goChat(hUid,uUid)
                                        }
                            }
                        }
                }.addOnCompleteListener { hideProgressDialog() }
            }
        } else {
            // 지정 상담 신청
            (context as BaseActivity).apply {
                val hUid: String = hUid ?: return
                showProgressDialog()
                // 상담신청 데이터 존재 유무 확인
                getSelectConsult(hUid, uUid).get().continueWith {task ->
                    task.result.toObjects(ConsultInfo::class.java).let {
                        if(it.isEmpty()) return@let null
                        else return@let it[0]
                    }?.let {consultInfo ->
                        // 상담신청 -> 상담중
                        val objectId = task.result.documents[0].id
                        consultInfo.status = ConsultStatus.CONSULTING.value
                        getSelectConsults().document(objectId).set(consultInfo)
                    }?:throw Exception()
                }
                        .addOnSuccessListener {
                            // TODO : 채팅방 생성
                            toast("상담 신청 완료하였습니다")
                            addCountCounselors(hUid)
                            goChat(hUid,uUid)
                        }
                        .addOnCompleteListener { hideProgressDialog() }
            }

        }
    }

    private fun goChat(hUid: String, uUid: String){
        doAsync {
            val db = FirebaseFirestore.getInstance()
            val task: Task<DocumentSnapshot> = db
                    .collection("chatList")
                    .document(hUid)
                    .collection("attendants")
                    .document(uUid)
                    .get()

            val documentSnapshot = Tasks.await(task)
            val roomId: String?
            val chatlistinfo = documentSnapshot.toObject(ChatListInfo::class.java)
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