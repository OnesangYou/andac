package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.dac.gapp.andac.adapter.ReplyRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.extension.loadImage
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.ReplyInfo
import com.dac.gapp.andac.model.firebase.SomebodyInfo
import com.dac.gapp.andac.util.getFullFormat
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_board_detail.*
import kotlinx.android.synthetic.main.base_item_card.*


class BoardDetailActivity : BaseActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)
        prepareUi()
        intent.getStringExtra(OBJECT_KEY)?.also { boardKey ->
            getBoard(boardKey)?.addSnapshotListener { documentSnapshot, _ ->
                val boardInfo = documentSnapshot?.toObject(BoardInfo::class.java)?:return@addSnapshotListener
                title_text.text = title
                contents_text.text = boardInfo.contents
                val pictureList = arrayListOf(picture_1, picture_2, picture_3)
                boardInfo.pictureUrls?.forEachIndexed { index, url ->
                    pictureList[index].loadImage(url)
                }
                date.text = boardInfo.writeDate?.getFullFormat() ?: ""
                replyText.text = "댓글 ${boardInfo.replyCount} 개"
                likeText.text = "좋아요 ${boardInfo.likeCount} 개"

                // Set Writer Profile
                getUserInfo(boardInfo.writerUid)?.continueWith { task1 ->
                    text_nickname.text = task1.result.nickName
                    imageView.loadImage(task1.result.profilePicUrl)
                }

                // Set Hospital Name
                val hospitalUid = boardInfo.hospitalUid.also { if(it.isEmpty()) return@addSnapshotListener }
                getHospital(hospitalUid).get()
                .continueWith { hospital_hashtag.text = it.result.toObject(HospitalInfo::class.java)?.name }
            }?.let { addListenerRegistrations(it) }

            // 댓글 프사
            if(isUser()) getUserInfo()?.continueWith { it.result.profilePicUrl } else getHospitalInfo()?.continueWith { it.result?.profilePicUrl }
                    ?.continueWith {
                        val url = it.result?:return@continueWith
                        userProfileImage.loadImage(url)
                    }

            // 댓글 달기
            replyEditView.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    // 입력되는 텍스트에 변화가 있을 때
                    replySubmit.isEnabled = count>0
                }
                override fun afterTextChanged(arg0: Editable) {}
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            })
            replySubmit.setOnClickListener { _ ->
                val reference = getReplies(boardKey)?.document()?:return@setOnClickListener
                showProgressDialog()
                reference.set(ReplyInfo(
                        writerUid = getUid()?:return@setOnClickListener,
                        contents = replyEditView.text.toString(),
                        objectId = reference.id,
                        boardId = boardKey,
                        writerType = if (isUser()) "user" else "hospital"
                    )
                )
                        .onSuccessTask { _ ->
                            // 댓글 카운트 추가
                            val boardRef = getBoard(boardKey)?:throw IllegalStateException()
                            FirebaseFirestore.getInstance().runTransaction {
                                val boardInfo = it.get(boardRef).toObject(BoardInfo::class.java)?:throw IllegalStateException()
                                it.set(boardRef, boardInfo.apply { replyCount++; if(replyCount < 0) throw IllegalStateException("Reply Count is Zero") })
                            }
                        }
                        .addOnSuccessListener { toast("댓글 추가 완료"); hideSoftKeyboard()}
                        .addOnCompleteListener { hideProgressDialog() }

            }

            // 댓글 리스트 출력
            getReplies(boardKey)?.orderBy("writeDate", Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.toObjects(ReplyInfo::class.java).also{ mutableList ->
                    Tasks.whenAllSuccess<Pair<String, SomebodyInfo>>(
                            mutableList?.filter{ it.writerType == "user" }?.mapNotNull { replyInfo ->
                                getUserInfo(replyInfo.writerUid)?.continueWith { replyInfo.writerUid to SomebodyInfo(it.result.profilePicUrl, it.result.nickName, replyInfo.writerUid == getUid()) }
                            }?.plus(mutableList.filter{ it.writerType == "hospital" }.mapNotNull { replyInfo ->
                                getHospitalInfo(replyInfo.writerUid)?.continueWith { replyInfo.writerUid to SomebodyInfo(it.result?.profilePicUrl!!, it.result?.name!!, replyInfo.writerUid == getUid()) }
                            })
                    ).addOnSuccessListener { list ->
                        val map = list.toMap()
                        recyclerView.layoutManager = LinearLayoutManager(this@BoardDetailActivity)
                        recyclerView.adapter = mutableList?.let { it1 -> ReplyRecyclerAdapter(this@BoardDetailActivity, it1, map)}
                    }
                }
            }?.let { addListenerRegistrations(it) }

        }

    }

    private fun prepareUi() {
        setActionBarLeftImage(R.drawable.back)
        hidActionBarRight()
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
    }
}
