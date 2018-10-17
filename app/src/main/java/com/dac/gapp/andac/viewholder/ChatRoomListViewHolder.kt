package com.dac.gapp.andac.viewholder

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.ChatActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ChatRoomRowBinding
import com.dac.gapp.andac.enums.ConsultStatus
import com.dac.gapp.andac.model.firebase.ConsultInfo
import org.jetbrains.anko.toast

class ChatRoomListViewHolder(var context: Context, view: View) : RecyclerView.ViewHolder(view) {
    var binding: ChatRoomRowBinding = DataBindingUtil.bind(view)!!


    fun onClickStartChat(uUid: String?, hUid: String?, roomId: String) {
        val uUid: String = uUid ?: return
        val hUid: String = hUid ?: return

        goChat(hUid, uUid, roomId)
    }

    private fun goChat(hUid: String, uUid: String, roomId: String) {
        context.startActivity(Intent(context, ChatActivity::class.java).putExtra("roomId", roomId).putExtra("uUid", uUid).putExtra("hUid", hUid))
    }

    fun openMenu(view: View, hUid: String, uUid: String) {
        PopupMenu(context, view).apply {
            menuInflater.inflate(R.menu.chat_room_menu, this.menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.viewApplication -> (context as BaseActivity).selectConsultDialog(hUid, uUid)
                    R.id.completeConsult -> completeConsult(hUid, uUid)
                    R.id.getOutRoom -> getOutRoom()
                }
                false
            }
        }.show()
    }

    fun completeConsult(hUid: String, uUid: String) {
        (context as BaseActivity).apply {
            showProgressDialog()
            // 상담신청 데이터 존재 유무 확인
            getSelectConsult(hUid, uUid).get().continueWith { task ->
                task.result.toObjects(ConsultInfo::class.java).let {
                    if (it.isEmpty()) return@let null
                    else return@let it[0]
                }?.let { consultInfo ->
                    // 상담신청 -> 상담중
                    val objectId = task.result.documents[0].id
                    consultInfo.status = ConsultStatus.COMPLETE.value
                    getSelectConsults().document(objectId).set(consultInfo)
                } ?: throw Exception()
            }
                    .addOnSuccessListener {
                        toast("상담 완료하였습니다")
                        getOutRoom()
                    }
                    .addOnCompleteListener { hideProgressDialog() }
        }
    }

    fun getOutRoom() {
        // TODO : "채팅방 나가기 구현해야함 to 주열"
        context.toast("채팅방 나가기 구현 예정입니다")
    }

}