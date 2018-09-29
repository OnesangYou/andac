package com.dac.gapp.andac

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.extension.setPrice
import com.dac.gapp.andac.model.firebase.EventInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText("이벤트 등록/수정")
        setActionBarRightText("삭제")
        hideActionBarRight()
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })

        // 수정 시 데이터 받아서 초기화
        intent.getStringExtra(OBJECT_KEY)?.let{ key ->
            showProgressDialog()
            getEvent(key)?.get()?.continueWith { it.result.toObject(EventInfo::class.java) }?.addOnSuccessListener { it?.apply {
                eventInfo = this
                titleText.setText(title)
                bodyText.setText(body)
                priceText.setPrice(price)
                Glide.with(this@EventWriteActivity).load(pictureUrl).into(topImage)
                Glide.with(this@EventWriteActivity).load(detailPictureUrl).into(bottomImage)

            }}?.addOnCompleteListener { hideProgressDialog() }
            setOnActionBarRightClickListener(View.OnClickListener {
                showDeleteEventDialog(key)
            })
            showActionBarRight()
        }

        // Top Picture
        topImage.setOnClickListener { _ ->
            getAlbumImage()?.subscribe {
                pictureUri = it
                Glide.with(this@EventWriteActivity).load(it).into(topImage)
            }
        }

        // Bottom Picture
        bottomImage.setOnClickListener { _ ->
            getAlbumImage()?.subscribe {
                detailPictureUri = it
                Glide.with(this@EventWriteActivity).load(it).into(bottomImage)
            }
        }

        // Upload
        uploadBtn.setOnClickListener { _ ->
            // 유효성검사
            if(!validate()) return@setOnClickListener

            eventInfo.apply {
                writerUid = getUid()?:return@setOnClickListener
                title = titleText.text.toString()
                body = bodyText.text.toString()
                price = priceText.text.toString().toIntOrNull()?:0
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
                    getEventStorageRef().child(eventInfoRef.id).child("picture.jpg").putFile(uri)
                            .continueWith { eventInfo.pictureUrl = it.result.downloadUrl.toString() }
                }
                ,
                detailPictureUri?.let{uri ->
                    getEventStorageRef().child(eventInfoRef.id).child("detailPicture.jpg").putFile(uri)
                            .continueWith { eventInfo.detailPictureUrl = it.result.downloadUrl.toString() }
                }
            ).filterNotNull().let { list ->
                Tasks.whenAllSuccess<String>(list)
                .onSuccessTask { _ ->
                    FirebaseFirestore.getInstance().batch().run {
                        getHospitalEvent(eventInfoRef.id)
                                ?.let { set(it, mapOf(dateFieldStr() to FieldValue.serverTimestamp()), SetOptions.merge()) }
                        set(eventInfoRef, eventInfo, SetOptions.merge())
                        commit()
                    }
                }
                .addOnSuccessListener { toast("$objectTypeStr 업로드 완료"); setResult(Activity.RESULT_OK); finish() }
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
