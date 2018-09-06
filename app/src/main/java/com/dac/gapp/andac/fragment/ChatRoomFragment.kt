package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.ChatActivity
import com.dac.gapp.andac.MyPageActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.util.MyToast
import kotlinx.android.synthetic.main.fragment_chat_room.*


/**
 * A simple [Fragment] subclass.
 */
class ChatRoomFragment : BaseFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()
        chat.setOnClickListener({
            val nextIntent = Intent(context, ChatActivity::class.java)
            startActivity(nextIntent)
        })
    }

    private fun prepareUi() {
        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.mypage)
            context.setActionBarCenterImage(R.drawable.andac_font)
            context.setActionBarRightImage(R.drawable.bell)
            context.setOnActionBarLeftClickListener(View.OnClickListener {
                // 로그인 상태 체크
                if (getCurrentUser() == null) {
                    goToLogin(true)
                } else {
                    startActivity(Intent(context, MyPageActivity::class.java))
                }
            })
            context.setOnActionBarRightClickListener(View.OnClickListener {
                MyToast.showShort(context, "TODO: 알림 설정")
            })
        }
    }

}// Required empty public constructor
