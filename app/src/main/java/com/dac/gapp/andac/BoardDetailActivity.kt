package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import com.dac.gapp.andac.adapter.ReplyRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.extension.loadImage
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.ReplyInfo
import com.dac.gapp.andac.model.firebase.SomebodyInfo
import com.dac.gapp.andac.util.getFullFormat
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_board_detail.*
import kotlinx.android.synthetic.main.base_item_card.*

class BoardDetailActivity : BaseActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail)
        intent.getStringExtra(OBJECT_KEY)?.also { key ->
            showProgressDialog()
            getBoard(key)?.get()?.continueWith { it.result.toObject(BoardInfo::class.java) }?.continueWith {
                // Set Board
                val item = it.result?: throw IllegalStateException()
                title_text.text = title
                contents_text.text = item.contents
                val pictureList = arrayListOf(picture_1, picture_2, picture_3)
                item.pictureUrls?.forEachIndexed { index, url ->
                    pictureList[index].loadImage(url)
                }
                date.text = item.writeDate?.getFullFormat() ?: ""
                replyText.text = "댓글 ${item.replyCount} 개"
                likeText.text = "좋아요 ${item.likeCount} 개"
                item
            }?.continueWithTask { task ->
                // Set Writer Profile
                val hospitalInfo = task.result?:return@continueWithTask task
                getUserInfo(hospitalInfo.writerUid)?.continueWith { task1 ->
                    text_nickname.text = task1.result.nickName
                    imageView.loadImage(task1.result.profilePicUrl)
                }
                return@continueWithTask task
            }?.continueWithTask { info ->
                // Set Hospital Name
                info.result?.let{ getHospital(it.hospitalUid).get()
                }?.continueWith { hospital_hashtag.text = it.result.toObject(HospitalInfo::class.java)?.name }
            }?.addOnCompleteListener{hideProgressDialog()}

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
                getReplies(key)?.document()?.let { reference ->
                    reference.set(ReplyInfo(
                            writerUid = getUid()?:return@setOnClickListener,
                            contents = replyEditView.text.toString(),
                            objectId = reference.id,
                            boardId = key,
                            writerType = if (isUser()) "user" else "hospital"
                        )
                    ).addOnSuccessListener { toast("댓글 추가 완료"); hideSoftKeyboard()}
                }
            }

            // 댓글 리스트 출력
            getReplies(key)?.orderBy("writeDate", Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, _ ->
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
}
