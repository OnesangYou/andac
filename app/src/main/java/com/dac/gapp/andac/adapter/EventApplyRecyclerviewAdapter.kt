package com.dac.gapp.andac.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.EventApplyInfo
import com.dac.gapp.andac.util.getDateFormat
import com.dac.gapp.andac.util.getHour
import com.dac.gapp.andac.util.getHourAndMin
import com.dac.gapp.andac.util.getMin
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.event_apply_row.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.util.*

class EventApplyRecyclerviewAdapter
(private val mDataList: List<EventApplyInfo>) : RecyclerView.Adapter<EventApplyRecyclerviewAdapter.EventApplyHolder>() {
    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventApplyHolder {
        return EventApplyHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EventApplyHolder, position: Int) {
        val item = mDataList[position]

        holder.apply{
            writeDateText.text = item.writeDate?.getDateFormat('.')
            nameText.text = item.name

            item.possibleTimeStart.getHourAndMin { hour, min -> possibleStartTime.text = "${"%02d".format(hour)}:${"%02d".format(min)}" }
            item.possibleTimeEnd.getHourAndMin { hour, min -> possibleEndTime.text = "${"%02d".format(hour)}:${"%02d".format(min)}" }

            phoneBtn.text = item.phone
            phoneBtn.setOnClickListener { view ->
                // 가능 시간 판단
                val calendar = Calendar.getInstance()
                val cHour = calendar.get(Calendar.HOUR_OF_DAY)
                val cMin = calendar.get(Calendar.MINUTE)

                val sHour = item.possibleTimeStart.getHour()
                val sMin = item.possibleTimeStart.getMin()

                val eHour = item.possibleTimeEnd.getHour()
                val eMin = item.possibleTimeEnd.getMin()

                if(cHour < sHour
                    || (cHour == sHour && cMin < sMin)
                    || cHour > eHour
                    || (cHour == eHour && cMin > eMin)
                ) {
                    view.context.alert(title = "통화", message = "통화 가능한 시간이 아닙니다 \n그래도 통화하시겠습니까?") {
                        positiveButton("YES") { _ ->
                            view.context.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.phone)))
                        }
                        negativeButton("NO") {}
                    }.show()
                } else {
                    // 통화
                    view.context.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.phone)))
                }
            }

            confirmBtn.isChecked = item.confirmDate != null
            confirmBtn.setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.context.toast("isChecked : $isChecked")
                (buttonView.context as BaseActivity).getEventApplicant(item.eventKey, item.writerUid)
                        ?.set(mapOf("confirmDate" to if(isChecked) FieldValue.serverTimestamp() else null), SetOptions.merge())
            }
        }
    }


    class EventApplyHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.event_apply_row, parent, false)) {

        val writeDateText: TextView = itemView.writeDateText
        val nameText: TextView = itemView.nameText
        val possibleStartTime: TextView = itemView.possibleStartTime
        val possibleEndTime: TextView = itemView.possibleEndTime

        val phoneBtn: Button = itemView.phoneBtn
        val confirmBtn: ToggleButton = itemView.confirmBtn
    }
}