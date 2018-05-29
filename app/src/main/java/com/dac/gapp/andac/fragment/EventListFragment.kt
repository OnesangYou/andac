package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.EventInfoRecyclerviewAdapter
import com.dac.gapp.andac.model.EventInfo
import kotlinx.android.synthetic.main.fragment_event_list.*


/**
 * A simple [Fragment] subclass.
 */
class EventListFragment : Fragment() {
    val datalist: ArrayList<EventInfo> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val layoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = layoutManager

        val adapter = EventInfoRecyclerviewAdapter(context, datalist)
        recycler_view.setAdapter(adapter)

        dataset()
    }

    fun dataset() {
        for (i in 1..10) {
            datalist.add(EventInfo("국내 최저가!!", "엄청난 기술력!", "뭐라고써져있는거야", "특별가", "80000원", "1121구매"))
        }
    }


}// Required empty public constructor
