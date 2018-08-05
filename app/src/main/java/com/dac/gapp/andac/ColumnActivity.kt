package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.dac.gapp.andac.adapter.ColumnRecyclerViewAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_column.*

class ColumnActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column)
        columnList.layoutManager = GridLayoutManager(this,2)
        setAdapter()
        back.setOnClickListener { finish() }

    }

    private var registration: ListenerRegistration? = null

    private fun setAdapter() {
        showProgressDialog()
        registration = getColumns()
                .orderBy("writeDate", Query.Direction.DESCENDING)   // order
                .addSnapshotListener{ querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                    querySnapshot?.let { it ->
                        it.toObjects(ColumnInfo::class.java).let { columnInfos ->
                            columnInfos.groupBy { it.writerUid }
                                    .map { getHospital(it.key).get() }
                                    .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                                    .addOnSuccessListener { it ->
                                        it
                                        .filter { it != null }
                                        .map { it.id to it.toObject(HospitalInfo::class.java) }
                                        .toMap().also { hospitalInfoMap ->
                                                columnList.adapter = ColumnRecyclerViewAdapter(this@ColumnActivity, columnInfos, hospitalInfoMap)
                                                columnList.adapter.notifyDataSetChanged()
                                        }
                                    }
                        }.addOnCompleteListener{hideProgressDialog()}
                    }?: hideProgressDialog()
                }


    }

    override fun onStop() {
        super.onStop()
        registration?.remove()
    }
}
