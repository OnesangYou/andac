package com.dac.gapp.andac

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import kotlinx.android.synthetic.main.activity_column_detail.*
import org.jetbrains.anko.alert

class ColumnDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column_detail)

        back.setOnClickListener { finish() }

        // 병원 상세 정보 가져오기
        intent.getStringExtra(OBJECT_KEY)?.let{ objectId ->
            getColumn(objectId)?.get()?.continueWith { it.result.toObject(ColumnInfo::class.java) }?.addOnSuccessListener { it?.also { columnInfo ->
                titleText.text = columnInfo.title
                contentsText.text = columnInfo.contents
                Glide.with(this@ColumnDetailActivity).load(columnInfo.pictureUrl).into(pictureImage)

                if(columnInfo.writerUid == getUid()){
                    deleteBtn.visibility = View.VISIBLE
                    modifyBtn.visibility = View.VISIBLE

                    deleteBtn.setOnClickListener { showDeleteColumnDialog(columnInfo.objectId) }
                    modifyBtn.setOnClickListener { startActivity(Intent(this@ColumnDetailActivity, ColumnWriteActivity::class.java).putExtra(OBJECT_KEY, columnInfo.objectId)) }
                } else {
                    deleteBtn.visibility = View.INVISIBLE
                    modifyBtn.visibility = View.INVISIBLE
                }

            } }
        }

    }

    private fun showDeleteColumnDialog(objectId : String){
        showProgressDialog()
        alert(title = "게시물 삭제", message = "게시물을 삭제하시겠습니까?") {
            positiveButton("YES"){
                // 삭제 진행
                showProgressDialog()
                getColumn(objectId)?.delete()
                        ?.addOnCompleteListener { hideProgressDialog() }
                        ?.addOnSuccessListener { finish() }
            }

            negativeButton("NO"){}
        }.show()
    }
}
