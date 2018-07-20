package com.dac.gapp.andac

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.hospital.activity_column_write.*

class ColumnWriteActivity : BaseActivity() {

    private var pictureUri: Uri? = null
    private val columnInfo = ColumnInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column_write)
        back.setOnClickListener { finish() }

        // Picture
        pictureBtn.setOnClickListener {
            startAlbumImageUri()
                    // save
                    .addOnSuccessListener { pictureUri = it }
                    // load image view
                    .addOnSuccessListener { Glide.with(this@ColumnWriteActivity).load(it).into(pictureBtn) }
        }

        // Upload
        uploadBtn.setOnClickListener {
            // 유효성검사
            if(!validate()) return@setOnClickListener

            columnInfo.apply {
                writerUid = getUid().toString()
                title = titleEdit.text.toString()
                contents = contentsEdit.text.toString()
            }

            val columnInfoRef = intent.getStringExtra(OBJECT_KEY)?.let{
                getColumns().document(it)
            }?:let{
                getColumns().document()
            }

            // picture 있을 경우 업로드 후 uri 받아오기, 데이터 업로드
            showProgressDialog()

            pictureUri.let{
                it?.let { uri ->
                    getColumnStorageRef().child(columnInfoRef.id).child("picture0.jpg").putFile(uri)
                            .continueWith { it.result.downloadUrl.toString() }
                            .addOnSuccessListener { columnInfo.pictureUrl = it }
                            .onSuccessTask {
                                columnInfoRef.set(columnInfo, SetOptions.merge())
                            }
                }?:let {
                    columnInfoRef.set(columnInfo, SetOptions.merge())
                }
            }
                    .addOnSuccessListener{ toast("칼럼 업로드 완료"); finish() }
                    .addOnCompleteListener{hideProgressDialog()}
        }

    }

    private fun validate() : Boolean {
        if(titleEdit.text.isBlank()) {
            toast("제목을 입력하세요")
            return false
        }

        if(contentsEdit.text.isBlank()) {
            toast("내용을 입력하세요")
            return false
        }

        return true
    }
}
