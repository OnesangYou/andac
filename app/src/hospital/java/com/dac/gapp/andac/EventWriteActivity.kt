package com.dac.gapp.andac

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.EventInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.hospital.activity_event_write.*


class EventWriteActivity : BaseActivity() {

    val objectTypeStr = "이벤트"

    private var pictureUri: Uri? = null
    private var detailPictureUri: Uri? = null
    private var eventInfo = EventInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_write)

        back.setOnClickListener { finish() }

        // 수정 시 데이터 받아서 초기화
        intent.getStringExtra(OBJECT_KEY)?.let{ key ->
            showProgressDialog()
            getEvent(key)?.get()?.continueWith { it.result.toObject(EventInfo::class.java) }?.addOnSuccessListener { it?.apply {
                eventInfo = this
                titleText.setText(title)
                bodyText.setText(body)
                Glide.with(this@EventWriteActivity).load(pictureUrl).into(topImage)
                Glide.with(this@EventWriteActivity).load(detailPictureUrl).into(bottomImage)

            }}?.addOnCompleteListener { hideProgressDialog() }
            deleteBtn.apply{
                setOnClickListener { showDeleteEventDialog(key) }
                deleteBtn.visibility = View.VISIBLE
            }

        }

        // Top Picture
        topImage.setOnClickListener { it ->
            startAlbumImageUri()
                    // save
                    .addOnSuccessListener { pictureUri = it }
                    // load image view
                    .addOnSuccessListener { Glide.with(this@EventWriteActivity).load(it).into(topImage) }
        }

        // Bottom Picture
        bottomImage.setOnClickListener { it ->
            startAlbumImageUri()
                    // save
                    .addOnSuccessListener { detailPictureUri = it }
                    // load image view
                    .addOnSuccessListener { Glide.with(this@EventWriteActivity).load(it).into(bottomImage) }
        }

        // Upload
        uploadBtn.setOnClickListener { it ->
            // 유효성검사
            if(!validate()) return@setOnClickListener

            eventInfo.apply {
                writerUid = getUid().toString()
                title = titleText.text.toString()
                body = bodyText.text.toString()
                price = priceText.text.toString().toInt()
            }

            val eventInfoRef = intent.getStringExtra(OBJECT_KEY)?.let{
                getEvents().document(it)
            }?:let{
                getEvents().document()
            }

            eventInfo.objectId = eventInfoRef.id

            // picture 있을 경우 업로드 후 url 받아오기, 데이터 업로드
            showProgressDialog()
            arrayListOf(
                pictureUri?.let{uri ->
                    getEventStorageRef().child(eventInfoRef.id).child("picture0.jpg").putFile(uri)
                            .continueWith { eventInfo.pictureUrl = it.result.downloadUrl.toString() }
                }
                ,
                detailPictureUri?.let{uri ->
                    getEventStorageRef().child(eventInfoRef.id).child("detailPictureUrl0.jpg").putFile(uri)
                            .continueWith { eventInfo.detailPictureUrl = it.result.downloadUrl.toString() }
                }
            ).filterNotNull().let {
                Tasks.whenAllSuccess<String>(it)
            }.let{ task ->
                task
                    .onSuccessTask { eventInfoRef.set(eventInfo, SetOptions.merge()) }
                    .addOnSuccessListener { toast("$objectTypeStr 업로드 완료"); finish() }
                    .addOnCompleteListener { hideProgressDialog() }
            }
        }

    }

    private fun showDeleteEventDialog(objectId : String) = getEvent(objectId)?.let{ showDeleteObjectDialog(objectTypeStr, it) }

    private fun validate() : Boolean {
        
        if(titleText.text.isBlank()) {
            toast("제목을 입력하세요")
            return false
        }

        if(bodyText.text.isBlank()) {
            toast("내용을 입력하세요")
            return false
        }

        if(priceText.text.isBlank()) {
            toast("가격을 입력하세요")
            return false
        }

        return true
    }

}
