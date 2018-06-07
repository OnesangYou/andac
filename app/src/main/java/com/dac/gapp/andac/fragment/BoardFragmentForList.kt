package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.MyRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.CardItem
import kotlinx.android.synthetic.main.fragment_search_hospital_for_list.*
import java.util.ArrayList


class BoardFragmentForList : BaseFragment() {

    // static method
    companion object {

        fun create(title: String): BoardFragmentForList {
            val f = BoardFragmentForList()
            f.title = title
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    var title: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_board_for_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // layoutManger을 리니어레이아웃으로한것, 그리드로한것 나오는 모양이 다르다.
        val layoutManager = LinearLayoutManager(context) //이게 리스트뷰랑 똑같은거라고 생각하면됨
        recyclerView.setLayoutManager(layoutManager) //이거 안하면 안보여. 나오지가 않음.

        val datalist = ArrayList<CardItem>()
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))

        val adapter = MyRecyclerAdapter(context, datalist)
        recyclerView.setAdapter(adapter)

    }
}