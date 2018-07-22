package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.*
import com.dac.gapp.andac.adapter.ColumnRecyclerViewAdapter
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.user.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requestSurgery.setOnClickListener {
            val nextIntent = Intent(context, RequestSurgeryActivity::class.java)
            startActivity(nextIntent)
        }

        column.setOnClickListener {
            val nextIntent = Intent(context, ColumnActivity::class.java)
            startActivity(nextIntent)
        }

        event.setOnClickListener {
            val nextIntent = Intent(context, EventActivity::class.java)
            startActivity(nextIntent)
        }

        findHospitalByText.setOnClickListener {
            startActivity(Intent(context, HospitalTextSearchActivity::class.java).putExtra("filterStr", "approval=1"))
        }

        more_calum.setOnClickListener {
            startActivity(Intent(context, ColumnActivity::class.java))
        }

        columnList.layoutManager = GridLayoutManager(context,2)
        setAdapter()
    }

    private fun setAdapter() {
        (context as MainActivity).apply{
            showProgressDialog()

            getColumns().orderBy("writeDate", Query.Direction.DESCENDING).get().addOnSuccessListener { querySnapshot ->
                val columnInfos = mutableListOf<ColumnInfo>()
                val hospitalInfoMap = mutableMapOf<String, HospitalInfo?>()

                val hospitalInfoMapTask = getHospital()?.get()?.addOnSuccessListener {
                    hospitalInfoMap[getUid().toString()] = it.toObject(HospitalInfo::class.java)
                }

                val columnInfosTask = querySnapshot
                        ?.let { it.map { getColumn(it.id)?.get() } }
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                        .addOnSuccessListener {
                            it.filter { it != null }.take(4).forEach { columnInfos.add(it.toObject(ColumnInfo::class.java)!!) }
                        }

                Tasks.whenAll(hospitalInfoMapTask, columnInfosTask)
                        .addOnSuccessListener {
                            columnList.adapter = ColumnRecyclerViewAdapter(this, columnInfos, hospitalInfoMap)
                            columnList.adapter.notifyDataSetChanged()
                        }
                        .addOnCompleteListener{hideProgressDialog()}
            }
        }
    }

}

