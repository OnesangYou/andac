package com.dac.gapp.andac.fragment


import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.EventInfoRecyclerviewAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.firebase.EventInfo
import kotlinx.android.synthetic.main.fragment_event_list_for_list.*

class EventListFragmentForList : BaseFragment(){

    val datalist: ArrayList<EventInfo> = ArrayList()

    var title: String = ""

    // static method
    companion object {
        fun create(title: String): EventListFragmentForList {
            val f = EventListFragmentForList()
            f.title = title
            val args = Bundle()
            f.arguments = args
            return f
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_event_list_for_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val layoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = layoutManager

        val adapter = EventInfoRecyclerviewAdapter(context, datalist)
        recycler_view.setAdapter(adapter)

        dataset()
    }

    fun dataset() {
//        datalist.add(EventInfo("아이리움 스마일라식", "아이리움안과의원[서울/청담]", "2018.06.30 ~ 2018.07.30", "특별가", "800,000원", "1122"))
//        for (i in 1..10) {
//            datalist.add(EventInfo("안정성인증 노안수술", "서울노안안과[서울/강남]", "2018.06.30 ~ 2018.07.25", "특별가", "80000원", "1121"))
//        }
    }
}