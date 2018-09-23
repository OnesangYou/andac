package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.ConsultBoardRecytclerViewAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentConsultBoardListBinding
import com.dac.gapp.andac.enums.ConsultStatus
import com.dac.gapp.andac.model.OpenConsultInfo
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Query
import java.util.*


class ConsultBoardFragmentForList : BaseFragment() {
    var title: String = ""
    var datalist: ArrayList<OpenConsultInfo> = ArrayList()
    private lateinit var binding: FragmentConsultBoardListBinding

    // static method
    companion object {
        fun create(title: String): ConsultBoardFragmentForList {
            val f = ConsultBoardFragmentForList()
            f.title = title
            val args = Bundle()
            f.arguments = args
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflate(inflater, R.layout.fragment_consult_board_list, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = getBinding()
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager

        val adapter = ConsultBoardRecytclerViewAdapter(context, datalist)
        binding.recyclerView.adapter = adapter
        when (title) {
            context!!.getString(R.string.consult_open_board) -> openData()
            context!!.getString(R.string.consult_seleted_board) -> selectData(ConsultStatus.APPLY.value)
            context!!.getString(R.string.consulting_board) -> selectData(ConsultStatus.CONSULTING.value)
            context!!.getString(R.string.consulted_board) -> selectData(ConsultStatus.COMPLETE.value)
        }
    }

    fun openData() {
        context?.apply {
            showProgressDialog()
            getOpenConsults().get().continueWithTask {
                Tasks.whenAll(
                    it.result.toObjects(ConsultInfo::class.java).mapNotNull{consultInfo ->
                        getUserInfo(consultInfo.userId)?.continueWith {
                            datalist.add(OpenConsultInfo(
                                    user = it.result,
                                    createdTime = consultInfo.writeDate,
                                    uUid = consultInfo.userId,
                                    isOpen = true
                            ))
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                )
            }
                    .addOnCompleteListener { hideProgressDialog() }
        }

    }

    fun selectData(status : String = ConsultStatus.APPLY.value) {
        val uid = getUid() ?: return

        context?.apply {
            showProgressDialog()
            getSelectConsults()
                    .whereEqualTo(if(isUser()) "userId" else "hospitalId", uid)
                    .whereEqualTo("status", status)
                    .orderBy("writeDate", Query.Direction.DESCENDING).get().continueWithTask {
                Tasks.whenAll(
                        it.result.toObjects(ConsultInfo::class.java).mapNotNull{consultInfo ->
                            getUserInfo(consultInfo.userId)?.continueWith {
                                datalist.add(OpenConsultInfo(
                                        user = it.result,
                                        createdTime = consultInfo.writeDate,
                                        uUid = consultInfo.userId,
                                        hUid = uid,
                                        isOpen = false
                                ))
                                binding.recyclerView.adapter.notifyDataSetChanged()
                            }
                        }
                )
            }
                    .addOnCompleteListener { hideProgressDialog() }
        }
    }

}