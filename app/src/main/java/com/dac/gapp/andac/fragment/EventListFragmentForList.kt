package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.EventInfoRecyclerviewAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.EventInfo
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
        datalist.add(EventInfo("택형!!", "너뮤!", "기여엉", "우아아앙", "80000원", "내가산다!"))
        for (i in 1..10) {
            datalist.add(EventInfo("국내 최저가!!", "엄청난 기술력!", "뭐라고써져있는거야", "특별가", "80000원", "1121구매"))
        }
    }
}