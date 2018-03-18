package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.ChatActivity
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.main.fragment_chat_room.*


/**
 * A simple [Fragment] subclass.
 */
class ChatRoomFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_chat_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chat.setOnClickListener({
            val nextIntent = Intent(context, ChatActivity::class.java)
            startActivity(nextIntent)
        })
    }

}// Required empty public constructor
