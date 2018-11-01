package com.dac.gapp.andac

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dac.gapp.andac.extension.loadImageAny
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_board_write.*
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList


class BoardWriteActivity : com.dac.gapp.andac.base.BaseActivity() {

    private val HOSPITAL_OBJECT_REQUEST = 0
    private var boardInfo = BoardInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_write)
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText(R.string.board_upload)
        setActionBarRightText(R.string.upload)

        val imageViews = Arrays.asList(picture_1, picture_2, picture_3)
        val mapImageViewUnit = mutableMapOf<ImageView, ((DocumentReference) -> Task<String>)?>(picture_1 to null, picture_2 to null, picture_3 to null)

        // 수정 시 게시글 데이터 받아서 초기화
        val boardSetTask = intent.getStringExtra(OBJECT_KEY)?.let { key ->

            getBoard(key)?.get()?.continueWith { it.result.toObject(BoardInfo::class.java) }?.addOnSuccessListener {
                it?.apply {
                    boardInfo = it
                    edit_text_title.setText(title)
                    edit_text_contents.setText(contents)

                    pictureUrls?.forEachIndexed { index, url ->
                        Glide.with(this@BoardWriteActivity).load(url).into(imageViews[index])
                    }

                    pictureUrls?.let {
                        for (i in 1..(3 - it.size)) {
                            it.add(null)
                        }
                    }

                    radioGroupType.check(when(type) {
                        getString(R.string.review_board) -> R.id.review_board
                        getString(R.string.question_board) -> R.id.question_board
                        getString(R.string.hot_board) -> R.id.hot_board
                        getString(R.string.certification_board) -> R.id.certification_board
                        else -> R.id.free_board
                    })
                }
            }?.continueWithTask { info -> info.result?.let{ getHospital(it.hospitalUid).get() }}?.addOnSuccessListener { hospital_search.setText(it.toObject(HospitalInfo::class.java)?.name) }
        }

        // Set User
        val userSetTask = getUserInfo()?.addOnSuccessListener { userInfo ->
            nickName.text = userInfo.nickName
            Glide.with(this@BoardWriteActivity).load(userInfo.profilePicUrl).into(profilePic)
        }

        // Go Task
        showProgressDialog()
        Tasks.whenAll(arrayListOf(boardSetTask, userSetTask).filterNotNull()).addOnCompleteListener{hideProgressDialog()}

        // 병원 검색 버튼
        hospital_search.setOnClickListener { _ ->
            Intent(this@BoardWriteActivity, HospitalTextSearchActivity::class.java).let {
                it.putExtra("filterStr", "approval=1")  // 승인된 병원만 보이도록
                startActivityForResult(it, HOSPITAL_OBJECT_REQUEST)
            }
        }

        // Pick Pictures
//        mapImageViewUri.map {
//            val imageView = it.key
//            imageView.setOnClickListener { _ ->
//                getAlbumImage()?.subscribe {uri ->
//                    Glide.with(this@BoardWriteActivity).load(uri).into(imageView)
//                    mapImageViewUri[imageView] = uri
//                }
//            }
//        }

        mapImageViewUnit.map { entry ->
            val imageView = entry.key
            imageView.setOnClickListener { _ ->
                getAlbumImage()?.subscribe {uri ->
                    Glide.with(this@BoardWriteActivity).load(uri).into(imageView)
                    mapImageViewUnit[imageView] = { boardInfoRef : DocumentReference ->
                        getBoardStorageRef()
                                .child(boardInfoRef.id).child("picture${imageViews.indexOf(imageView)}.jpg")
                                .putFile(uri)
                                .continueWith { task ->
                                    task.result.downloadUrl.toString().also{
                                        boardInfo.pictureUrls?.set(imageViews.indexOf(imageView), it)
                                    } // 새로운 사진으로 넣기
                                }
                    }
                }
            }
        }


        // Type
        radioGroupType.setOnCheckedChangeListener { _, id ->
            hospital_search.visibility = View.GONE
            when(id) {
                R.id.free_board -> boardInfo.type = getString(R.string.free_board)
                R.id.review_board -> {
                    boardInfo.type = getString(R.string.review_board)
                    hospital_search.visibility = View.VISIBLE
                }
                R.id.question_board -> boardInfo.type = getString(R.string.question_board)
                R.id.hot_board -> boardInfo.type = getString(R.string.hot_board)
                R.id.certification_board -> boardInfo.type = getString(R.string.certification_board)
            }
        }

        // Upload
        setOnActionBarRightClickListener(View.OnClickListener { _ ->

            // set boardInfo
            getUid()?.let {
                boardInfo.apply {
                    writerUid = it
                    title = edit_text_title.text.toString()
                    contents = edit_text_contents.text.toString()
                }
            }?:let {
                goToLogin()
                return@OnClickListener
            }

            // 유효성검사
            if(!validate()) {
                return@OnClickListener
            }


            val boardInfoRef = intent.getStringExtra(OBJECT_KEY)?.let{
                getBoards().document(it)
            }?:let{
                getBoards().document()
            }

            boardInfo.objectId = boardInfoRef.id

            // picture 있을 경우 업로드 후 uri 받아오기, 데이터 업로드
            showProgressDialog()

            mapImageViewUnit.values.filterNotNull().map{
                it.invoke(boardInfoRef)
            }.let { Tasks.whenAllSuccess<String>(it) }
                    .onSuccessTask {
                        boardInfoRef.set(boardInfo, SetOptions.merge())
                    }


//            mapImageViewUri.values.filterNotNull().let{ uris ->
//                uris.mapIndexed { index, uri ->
//                    getBoardStorageRef()
//                            .child(boardInfoRef.id).child("picture$index.jpg")
//                            .putFile(uri)
//                            .continueWith { it.result.downloadUrl.toString() }
//                }
//                        .let { Tasks.whenAllSuccess<String>(it) }
//                        .onSuccessTask {
//                            boardInfo.pictureUrls = ArrayList(it)
//                            boardInfoRef.set(boardInfo, SetOptions.merge())
//                        }
//            }

                    .addOnSuccessListener{
                        toast("게시물 업로드 완료")
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    .addOnCompleteListener{hideProgressDialog()}
                    .addOnFailureListener {
                        it.printStackTrace()
                    }

        })

        setOnActionBarLeftClickListener(View.OnClickListener { finish() })

        // 사진 지우기
        listOf(cancle_btn1, cancle_btn2, cancle_btn3).forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                imageViews[index].let {
//                    check(mapImageViewUri[it] != null) {return@let}
//                    mapImageViewUri[it] = null
                    it.loadImageAny(android.R.drawable.ic_menu_camera)

                }
            }
        }
    }

    private fun validate() : Boolean {
        // 태그
//        if(boardInfo.tag.isBlank()) {
//            toast("태그를 선택하세요")
//            return false
//        }

        // 타입
        if(boardInfo.type.isBlank()) {
            toast("타입을 선택하세요")
            return false
        }

        // 병원명
        if(hospital_search.visibility == View.VISIBLE && hospital_search.text.isBlank()) {
            toast("병원을 선택하세요")
            return false
        }

        // 제목
        if(boardInfo.title.isBlank()){
            toast("제목을 입력하세요")
            return false
        }

        // 내용
        if(boardInfo.contents.isBlank()){
            toast("내용을 입력하세요")
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == HOSPITAL_OBJECT_REQUEST) {
            data?.let{
                (it.getSerializableExtra("hospitalInfo") as HospitalInfo).let { hospitalInfo ->
                    hospital_search.setText(hospitalInfo.name)
                    boardInfo.hospitalUid = hospitalInfo.objectID
                }
            }
        }

    }
}
