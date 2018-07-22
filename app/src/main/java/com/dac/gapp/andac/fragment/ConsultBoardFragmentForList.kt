package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.ConsultBoardRecytclerViewAdapter
import com.dac.gapp.andac.model.EventInfo
import kotlinx.android.synthetic.main.fragment_consult_board_list.*

class ConsultBoardFragmentForList : Fragment(){
    var title: String = ""
    val datalist: ArrayList<String> = ArrayList()
    // static method
    companion object {
        fun create(title: String): ConsultBoardFragmentForList {
            val f = ConsultBoardFragmentForList()
            f.title = title
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_consult_board_list,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val layoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = layoutManager

        val adapter = ConsultBoardRecytclerViewAdapter(context, datalist)
        recycler_view.adapter = adapter

        dataset()
    }

    fun dataset() {
        datalist.add("테스트")
        for (i in 1..10) {
            datalist.add("테스트2")
        }
    }
}