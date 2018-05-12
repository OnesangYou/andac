package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.CardItem
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.MyRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_board.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class BoardFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fabWriteBoard.setOnClickListener {
            val nextIntent = Intent(context, BoardWriteActivity::class.java)
            startActivity(nextIntent)
        }

        // layoutManger을 리니어레이아웃으로한것, 그리드로한것 나오는 모양이 다르다.
        val layoutManager = LinearLayoutManager(context) //이게 리스트뷰랑 똑같은거라고 생각하면됨
        recycler_view.setLayoutManager(layoutManager) //이거 안하면 안보여. 나오지가 않음.

        val datalist = ArrayList<CardItem>()
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))
        datalist.add(CardItem("안녕하세요", "하이하이\n하이하이하이"))

        val adapter = MyRecyclerAdapter(context, datalist)
        recycler_view.setAdapter(adapter)

        //외부에서 온클릭리스너만든것을 한번 사용해봅시다 adapter.setonclicklist(this)해준후 임플리먼트한다.
    }
}// Required empty public constructor


