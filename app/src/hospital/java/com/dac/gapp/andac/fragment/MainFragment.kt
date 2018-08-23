package com.dac.gapp.andac.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.*
import com.dac.gapp.andac.adapter.ColumnRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.hospital.fragment_main.*
import org.jetbrains.anko.startActivity

class MainFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnConsultingBoard.setOnClickListener { startActivity(Intent(context, RequestSurgeryActivity::class.java)) }
        btnHospitalEventManagement.setOnClickListener { context?.startActivity<HospitalEventListActivity>() }
        btnHospitalAdManagement.setOnClickListener { startActivity(HospitalAdApplicationActivity.createIntentForAdManagement(requireContext())) }
        btnHospitalAdApplication.setOnClickListener { startActivity(HospitalAdApplicationActivity.createIntentForAdApplication(requireContext())) }

        myColumnsBtn.setOnClickListener { context?.startActivity<HospitalColumnListActivity>() }

        columnList.layoutManager = GridLayoutManager(context, 2)
        setAdapter()

        more_calum.setOnClickListener {
            context?.startActivity<ColumnActivity>()
        }
    }

    private fun setAdapter() {
        (context as MainActivity).apply {

            showProgressDialog()
            getColumns().orderBy("writeDate", Query.Direction.DESCENDING).limit(4).get().addOnSuccessListener { querySnapshot ->
                querySnapshot
                        ?.let { it -> it.map { getColumn(it.id)?.get() } }
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                        .onSuccessTask { it ->
                            val columnInfos = it?.filterNotNull()?.map { it.toObject(ColumnInfo::class.java)!! }
                            columnInfos?.groupBy { it.writerUid }
                                    ?.map { getHospital(it.key).get() }
                                    .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                                    .addOnSuccessListener { mutableList ->
                                        mutableList
                                                .filterNotNull()
                                                .map { it.id to it.toObject(HospitalInfo::class.java) }
                                                .toMap().also { hospitalInfoMap ->
                                                    columnList.adapter = ColumnRecyclerAdapter(this, columnInfos!!, hospitalInfoMap)
                                                    columnList.adapter.notifyDataSetChanged()
                                                    columnList.addOnItemClickListener(object: OnItemClickListener {
                                                        override fun onItemClicked(position: Int, view: View) {
                                                            // 디테일
                                                            startActivity(Intent(this@apply, ColumnDetailActivity::class.java).putExtra(OBJECT_KEY,columnInfos[position].objectId))
                                                        }
                                                    })
                                                }
                                    }
                        }
                        .addOnCompleteListener { hideProgressDialog() }
            }
        }
    }

}

