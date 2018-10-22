package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.MyPageActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.RequestConsultActivity
import com.dac.gapp.andac.adapter.ChatRoomListAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentChatRoomBinding
import com.dac.gapp.andac.model.firebase.ChatListInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class ChatRoomFragment : BaseFragment() {

    var list: MutableList<ChatListInfo> = mutableListOf()
    lateinit var binding: FragmentChatRoomBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflate(inflater, R.layout.fragment_chat_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
    }

    private fun prepareUi() {
        binding = getBinding()

        binding.chatRoomList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ChatRoomListAdapter(context, list)
        }

        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.mypage)
            context.setActionBarCenterImage(R.drawable.andac_font)
            context.setActionBarRightImage(R.drawable.bell)
            context.setOnActionBarLeftClickListener(View.OnClickListener {
                // 로그인 상태 체크
                if (getCurrentUser() == null) {
                    goToLogin(true)
                    setList()
                } else {
                    startActivity(Intent(context, MyPageActivity::class.java))
                }
            })
            context.setOnActionBarRightClickListener(View.OnClickListener {
                //                MyToast.showShort(context, "TODO: 알림 설정")
            })
        }
        setList()

        // 상담 신청 플로팅 버튼
        if((context?:return).isHospital()){
            binding.goToOpenConsult.visibility = View.GONE
        } else {
            binding.goToOpenConsult.setOnClickListener {
                context?.afterCheckLoginDo { startActivity(Intent(context, RequestConsultActivity::class.java).putExtra("isOpen", true)) }
            }
        }

    }

    private fun setList() {
        val db = FirebaseFirestore.getInstance()
        val uid = getUid() ?: return
        list.clear()

        db.collection("chatList").document(uid).collection("attendants")
                .addSnapshotListener { snapshots, _ ->
                    for (dc in snapshots?.documentChanges!!) {
                        when (dc.type) {
                            DocumentChange.Type.ADDED -> {
                                binding.isEmpty = false
                                binding.notifyChange()
                                val item = dc.document.toObject(ChatListInfo::class.java)
                                list.add(item)

                                db.collection("hospitals").document(dc.document.id).get().addOnCompleteListener {
                                    val hospitalinfo = it.result.toObject(HospitalInfo::class.java)
                                    item.apply {
                                        hospitalName = hospitalinfo?.name
                                        picUrl = hospitalinfo?.profilePicUrl

                                        context?.apply {
                                            if(isUser()){
                                                hUid = dc.document.id
                                                uUid = uid
                                            } else {
                                                hUid = uid
                                                uUid = dc.document.id
                                            }
                                        }
                                    }
                                    binding.chatRoomList.apply { adapter.notifyDataSetChanged() }
                                }
                            }
                            DocumentChange.Type.MODIFIED -> {

                            }
                            DocumentChange.Type.REMOVED -> Timber.d(dc.document.data.toString())
                        }
                    }
                }

    }

}