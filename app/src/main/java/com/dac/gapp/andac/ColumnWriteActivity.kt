package com.dac.gapp.andac

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_column_write.*

class ColumnWriteActivity : BaseActivity() {

    private var pictureTaskUnit: ((columnInfoKey : String) -> Task<Unit>)? = null
    private var columnInfo = ColumnInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_column_write)
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText(R.string.column_upload)
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
        hideActionBarRight()

        // 수정 시 칼럼 데이터 받아서 초기화
        intent.getStringExtra(OBJECT_KEY)?.let{ key ->
            showProgressDialog()
            getColumn(key)?.get()?.continueWith { it.result.toObject(ColumnInfo::class.java) }
                    ?.addOnSuccessListener { it?.apply {
                        columnInfo = this
                titleText.setText(title)
                contentsText.setText(contents)
                Glide.with(this@ColumnWriteActivity).load(pictureUrl).into(pictureImage)

            }}?.addOnCompleteListener { hideProgressDialog() }

            setActionBarRightText(R.string.delete)
            setOnActionBarRightClickListener(View.OnClickListener {
                showDeleteColumnDialog(key)
            })
            showActionBarRight()

        }

        // Picture
        pictureImage.setOnClickListener { _ ->
            getAlbumImage()?.subscribe { uri ->
                pictureImage.loadImageAny(uri)
                pictureTaskUnit = { columnInfoKey ->
                    getColumnStorageRef()
                            .child(columnInfoKey)
                            .child("picture.jpg")
                            .putFile(uri)
                            .continueWith { columnInfo.pictureUrl = it.result.downloadUrl.toString() }
                }
            }?.apply { disposables.add(this) }
        }

        // 사진 삭제
        cancle_btn.setOnClickListener { _ ->
            pictureImage.loadImageAny(R.drawable.album_ic_image_camera_white)
            pictureTaskUnit = if(columnInfo.pictureUrl.isEmpty()) null else { _ ->
                FirebaseStorage.getInstance().getReferenceFromUrl(columnInfo.pictureUrl)
                        .delete().continueWith { columnInfo.pictureUrl = "" }
            }
        }

        // Upload
        uploadBtn.setOnClickListener { _ ->
            // 유효성검사
            if(!validate()) return@setOnClickListener

            columnInfo.apply {
                writerUid = getUid()?:return@setOnClickListener
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
            arrayListOf(pictureTaskUnit?.invoke(columnInfoRef.id))
                    .filterNotNull()
                    .let { Tasks.whenAllSuccess<String>(it) }
                    .onSuccessTask { _ ->
                        FirebaseFirestore.getInstance().batch().run {
                            set(columnInfoRef, columnInfo, SetOptions.merge())
                            getHospitalColumn(columnInfoRef.id)
                                    ?.let { set(it, mapOf(dateFieldStr() to FieldValue.serverTimestamp()), SetOptions.merge()) }
                            commit()
                        }
                    }
                    .addOnCompleteListener{ hideProgressDialog() }
                    .addOnSuccessListener{ toast("칼럼 업로드 완료"); setResult(Activity.RESULT_OK); finish() }
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
