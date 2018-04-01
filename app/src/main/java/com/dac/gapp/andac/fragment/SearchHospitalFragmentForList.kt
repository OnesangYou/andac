package com.dac.gapp.andac.fragment

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.dac.gapp.andac.R

class SearchHospitalFragmentForList : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search_hospital_for_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    class MyAdapter(itemList : List<MyItem>) : RecyclerView.Adapter<MyViewHoder>() {
        var itemList: List<MyItem> =  itemList

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHoder {
            return MyViewHoder(LayoutInflater.from(parent!!.context).inflate(R.layout.row, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHoder?, position: Int) {
            var myItem : MyItem = itemList.get(position)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

    }

    class MyViewHoder(view : View) : RecyclerView.ViewHolder(view) {
        var thumbnail : ImageView
        var title : TextView
        var address : TextView
        var description : TextView
        init {
            thumbnail = view.findViewById(R.id.imgview_thumbnail)
            title = view.findViewById(R.id.txtview_title)
            address = view.findViewById(R.id.txtview_address)
            description = view.findViewById(R.id.txtview_description)

        }
    }

    class MyItem(tumbnail : String, address : String, description : String) {
        var tumbnail = tumbnail
        var address = address
        var description = description
    }
}