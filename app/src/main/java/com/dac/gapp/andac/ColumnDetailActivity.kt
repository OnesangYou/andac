package com.dac.gapp.andac

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.util.getFullFormat
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.activity_column_detail.*

class ColumnDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column_detail)
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("컬럼 상세보기")
        hideActionBarRight()
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })

        // 병원 상세 정보 가져오기
        intent.getStringExtra(OBJECT_KEY)?.let{ objectId ->

            showProgressDialog()
            getColumn(objectId)?.get()?.continueWith { it.result.toObject(ColumnInfo::class.java) }?.addOnSuccessListener { it ->
                it?.also { columnInfo ->
                titleText.text = columnInfo.title
                contentsText.text = columnInfo.contents
                Glide.with(this@ColumnDetailActivity).load(columnInfo.pictureUrl).into(pictureImage)
                writeDateText.text = columnInfo.writeDate?.getFullFormat()
                viewCountText.text = columnInfo.viewCount.toString()

                // 병원명
                getHospitalInfo(columnInfo.writerUid)?.addOnSuccessListener {
                    hospitalNameText.text = it?.name
                }

            } }?.addOnCompleteListener { hideProgressDialog() }

            // 내가 본 컬럼 추가
            addViewedColumn(objectId)
        }

    }

    private fun addViewedColumn(columnId : String) {
        if(!isUser()) return    // 유저만 추가

        getViewedColumns()?.document(columnId)?.set(mapOf("createdDate" to FieldValue.serverTimestamp()))
    }


}
