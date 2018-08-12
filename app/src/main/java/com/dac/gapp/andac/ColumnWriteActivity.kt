package com.dac.gapp.andac

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_column_write.*
import android.view.View
import org.jetbrains.anko.alert

class ColumnWriteActivity : BaseActivity() {

    private var pictureUri: Uri? = null
    private var columnInfo = ColumnInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column_write)
        back.setOnClickListener { finish() }

        // 수정 시 컬럼 데이터 받아서 초기화
        intent.getStringExtra(OBJECT_KEY)?.let{ key ->
            showProgressDialog()
            getColumn(key)?.get()?.continueWith { it.result.toObject(ColumnInfo::class.java) }
                    ?.addOnSuccessListener { it?.apply {
                        columnInfo = this
                titleText.setText(title)
                contentsText.setText(contents)
                Glide.with(this@ColumnWriteActivity).load(pictureUrl).into(pictureImage)

            }}?.addOnCompleteListener { hideProgressDialog() }

            deleteBtn.apply{
                setOnClickListener { showDeleteColumnDialog(key) }
                deleteBtn.visibility = View.VISIBLE

            }

        }

        // Picture
        pictureImage.setOnClickListener { it ->
            startAlbumImageUri()
                    // save
                    .addOnSuccessListener { pictureUri = it }
                    // load image view
                    .addOnSuccessListener { Glide.with(this@ColumnWriteActivity).load(it).into(pictureImage) }
        }

        // Upload
        uploadBtn.setOnClickListener { it ->
            // 유효성검사
            if(!validate()) return@setOnClickListener

            columnInfo.apply {
                writerUid = getUid().toString()
                title = titleText.text.toString()
                contents = contentsText.text.toString()
            }

            val columnInfoRef = intent.getStringExtra(OBJECT_KEY)?.let{
                getColumns().document(it)
            }?:let{
                getColumns().document()
            }

            columnInfo.objectId = columnInfoRef.id

            // picture 있을 경우 업로드 후 uri 받아오기, 데이터 업로드
            showProgressDialog()
            pictureUri.let{ pictureUri ->
                pictureUri?.let { uri ->
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
                    .addOnCompleteListener{hideProgressDialog()}
                    .addOnSuccessListener{ toast("칼럼 업로드 완료"); finish() }

        }

    }

    private fun validate() : Boolean {
        if(titleText.text.isBlank()) {
            toast("제목을 입력하세요")
            return false
        }

        if(contentsText.text.isBlank()) {
            toast("내용을 입력하세요")
            return false
        }

        return true
    }

    private fun showDeleteColumnDialog(objectId : String) = getColumn(objectId)?.let{ showDeleteObjectDialog("게시물", it) }
}
