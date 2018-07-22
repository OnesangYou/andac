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

//    private var registration: ListenerRegistration? = null

//    private fun setAdapter() {
//        showProgressDialog()
//        registration = getColumns()
//                .orderBy("writeDate", Query.Direction.DESCENDING)   // order
//                .addSnapshotListener{ querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
//                    querySnapshot?.let {
//                        it.toObjects(ColumnInfo::class.java).let { columnInfos ->
//                            columnInfos.groupBy { it.writerUid }
//                                    .map { getHospital(it.key).get() }
//                                    .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
//                                    .addOnSuccessListener { it
//                                            .filter { it != null }
//                                            .map { it.id to it.toObject(HospitalInfo::class.java) }
//                                            .toMap().also { hospitalInfoMap ->
//                                                columnList.adapter = ColumnRecyclerViewAdapter(this, columnInfos, hospitalInfoMap)
//                                                columnList.adapter.notifyDataSetChanged()
//                                            }
//                                    }
//                        }.addOnCompleteListener{hideProgressDialog()}
//                    }?:let{ hideProgressDialog() }
//                }
//    }

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

    override fun onStop() {
        super.onStop()
//        registration?.remove()
    }
}
