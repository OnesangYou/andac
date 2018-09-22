package com.dac.gapp.andac.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.ConsultBoardRecytclerViewAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.OpenConsultInfo
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_consult_board_list.*
import timber.log.Timber
import java.util.*


class ConsultBoardFragmentForList : BaseFragment() {
    var title: String = ""
    var datalist: ArrayList<OpenConsultInfo> = ArrayList()

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
        return inflater.inflate(R.layout.fragment_consult_board_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val layoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = layoutManager

        val adapter = ConsultBoardRecytclerViewAdapter(context, datalist)
        recycler_view.adapter = adapter
        val test = context!!.getString(R.string.consult_open_board)
        val test2 = ConsultBoardFragmentForList().title
        when (title) {
            context!!.getString(R.string.consult_open_board) -> openData()
            context!!.getString(R.string.consult_seleted_board) -> selectData()
            context!!.getString(R.string.consulting_board) -> selectData()
            context!!.getString(R.string.consulted_board) -> openData()
        }

    }

    fun openData() {
        val uid = getUid() ?: return

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
                            recycler_view?.adapter?.notifyDataSetChanged()
                        }
                    }
                )
            }
                    .addOnCompleteListener { hideProgressDialog() }
        }

    }

    fun selectData() {
        val uid = getUid() ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("selectConsult")
                .document(uid)
                .collection("users")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (data in querySnapshot.documents) {
                        db.collection("users")
                                .document(data.id)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val user = querySnapshot.toObject(UserInfo::class.java)
                                    datalist.add(OpenConsultInfo(user, data["createdDate"] as Date, querySnapshot.id, uid,false))
                                    recycler_view?.adapter?.notifyDataSetChanged()
                                    Timber.d(user.toString())
                                }
                    }
                }
    }

}