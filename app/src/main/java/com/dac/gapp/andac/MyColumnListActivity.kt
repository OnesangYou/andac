package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.dac.gapp.andac.adapter.ColumnRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_my_column_list.*

class MyColumnListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_column_list)
        columnList.layoutManager = GridLayoutManager(this,2)
        setColumnRecyclerAdapter()
        back.setOnClickListener { finish() }
    }

    private fun setColumnRecyclerAdapter() {
        showProgressDialog()
        getHospitalColumns()?.orderBy("createdDate", Query.Direction.DESCENDING)?.get()?.addOnSuccessListener { querySnapshot ->
            val columnInfos = mutableListOf<ColumnInfo>()
            val hospitalInfoMap = mutableMapOf<String, HospitalInfo?>()

            val hospitalInfoMapTask = getHospital()?.get()?.addOnSuccessListener {
                hospitalInfoMap[getUid().toString()] = it.toObject(HospitalInfo::class.java)
            }

            val columnInfosTask = querySnapshot
                    ?.let { it.map { getColumn(it.id)?.get() } }
                    .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                    .addOnSuccessListener {
                        it.filter { it != null }.forEach { columnInfos.add(it.toObject(ColumnInfo::class.java)!!) }
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
