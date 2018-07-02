package com.dac.gapp.andac

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.google.android.gms.tasks.Tasks
import kotlinx.android.synthetic.main.activity_board_write.*
import java.util.*
import kotlin.collections.ArrayList


class BoardWriteActivity : com.dac.gapp.andac.base.BaseActivity() {

    private val HOSPITAL_OBJECT_REQUEST = 0
    private val boardInfo = BoardInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_write)

        var pictureUris : List<Uri>? = null
        val imageViews = Arrays.asList(picture_1, picture_2, picture_3)

        hospital_search.setOnClickListener {
            // 병원 검색
            Intent(this@BoardWriteActivity, HospitalTextSearchActivity::class.java).let {
                it.putExtra("filterStr", "approval=1")  // 승인된 병원만 보이도록
                startActivityForResult(it, HOSPITAL_OBJECT_REQUEST)
            }
        }

        // Set User
        getUserInfo()?.addOnSuccessListener { userInfo ->
            nickName.text = userInfo.nickName
            Glide.with(this@BoardWriteActivity).load(userInfo.profilePicUrl).into(profilePic)
        }

        // Pick Pictures
        button_picture_upload.setOnClickListener {
            startAlbumImageUri(3)
                    // save
                    .addOnSuccessListener { pictureUris = it }
                    // load image view
                    .addOnSuccessListener {uris ->
                        imageViews.forEachIndexed { index, imageView -> Glide.with(this@BoardWriteActivity).load(
                                if(index < uris.size) uris[index] else R.drawable.profilepic).into(imageView) }
                    }

        }

        // Tag
        radioGroupTag.setOnCheckedChangeListener { _, id ->
            when(id) {
                R.id.imgBtnLasic -> boardInfo.tag = "라식/라섹"
                R.id.imgBtnOldeye -> boardInfo.tag = "노안수술"
                R.id.imgBtnInsertLense -> boardInfo.tag = "렌즈삽입술"
                R.id.imgBtnWhiteeye -> boardInfo.tag = "백내장수술"
                R.id.imgBtnEyesick -> boardInfo.tag = "안구질환"
            }
        }

        // Type
        radioGroupType.setOnCheckedChangeListener { _, id ->
            when(id) {
                R.id.free_board -> boardInfo.type = getString(R.string.free_board)
                R.id.review_board -> boardInfo.type = getString(R.string.review_board)
                R.id.question_board -> boardInfo.type = getString(R.string.question_board)
            }
        }

        // Upload
        uploadBtn.setOnClickListener {

            // set boardInfo
            boardInfo.apply {
                writerUid = getUid()
                title = edit_text_title.text.toString()
                contents = edit_text_contents.text.toString()
            }

            // 유효성검사
            if(!validate()) {
                return@setOnClickListener
            }

            val boardInfoRef = getBoards().document()

            // picture 업로드 후 uri 받아오기
            showProgressDialog()

            pictureUris?.let { uris ->
                uris.mapIndexed { index, uri  ->
                    getBoardStorageRef().child(boardInfoRef.id).child("picture$index.jpg").putFile(uri)
                            .continueWith{ it.result.downloadUrl.toString() }
                }
                .let { Tasks.whenAllSuccess<String>(it) }
                        .addOnSuccessListener { boardInfo.pictureUrls = ArrayList(it) }
                        .onSuccessTask { boardInfoRef.set(boardInfo) }
            } // 사진이 없을 경우
                    .let { boardInfoRef.set(boardInfo) }
                    .addOnSuccessListener{toast("게시물 업로드 완료")}
                    .addOnCompleteListener{hideProgressDialog()}

        }

    }

    private fun validate() : Boolean {
        // 태그
        if(boardInfo.tag.isBlank()) {
            toast("태그를 선택하세요")
            return false
        }

        // 타입
        if(boardInfo.type.isBlank()) {
            toast("타입을 선택하세요")
            return false
        }

        // 병원명
        if(hospital_search.text.isBlank()) {
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
