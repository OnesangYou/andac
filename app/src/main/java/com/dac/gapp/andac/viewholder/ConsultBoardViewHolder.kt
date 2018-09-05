package com.dac.gapp.andac.viewholder

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dac.gapp.andac.databinding.ConsultBoardRowBinding
import com.dac.gapp.andac.dialog.ConsultContentDialog
import com.dac.gapp.andac.model.firebase.ConsultInfo
import com.google.firebase.firestore.FirebaseFirestore

class ConsultBoardViewHolder(var context: Context?, var view: View) : RecyclerView.ViewHolder(view) {

    var binding: ConsultBoardRowBinding = DataBindingUtil.bind(view)!!

    fun onClickShowConsult(uUid: String?, hUid: String?) {
        val db = FirebaseFirestore.getInstance()



        hUid?.let { hUid ->
            uUid?.let { uUid ->
                db.collection("selectConsult")
                        .document(hUid)
                        .collection("users")
                        .document(uUid)
                        .collection("content")
                        .document(uUid)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val consultInfo = querySnapshot.toObject(ConsultInfo::class.java)
                            val dialog = context?.let { ConsultContentDialog(it,consultInfo) }
                            dialog?.show()
                        }
            }
        } ?: uUid?.let {
            db.collection("openConsult")
                    .document(it)
                    .collection("content")
                    .document(it)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val consultInfo = querySnapshot.toObject(ConsultInfo::class.java)
                        val dialog = context?.let { ConsultContentDialog(it,consultInfo) }
                        dialog?.show()
                    }
        }



    }
}