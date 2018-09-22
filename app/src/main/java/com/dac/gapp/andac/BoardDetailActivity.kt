package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.PopupMenu
import com.dac.gapp.andac.adapter.ReplyRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.RequestCode
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
        prepareUi()
        intent.getStringExtra(OBJECT_KEY)?.also { boardKey ->
            getBoard(boardKey)?.addSnapshotListener { documentSnapshot, _ ->
                val boardInfo = documentSnapshot?.toObject(BoardInfo::class.java)?:return@addSnapshotListener
                title_text.text = boardInfo.title
                contents_text.text = boardInfo.contents
                val pictureList = arrayListOf(picture_1, picture_2, picture_3)
                boardInfo.pictureUrls?.forEachIndexed { index, url ->
                    pictureList[index].loadImage(url)
                }
                date.text = boardInfo.writeDate?.getFullFormat() ?: ""
                replyText.text = "댓글 ${boardInfo.replyCount} 개"
                likeText.text = "좋아요 ${boardInfo.likeCount} 개"

                // 메뉴
                if(isLogin() && boardInfo.writerUid == getUid()){
                    menu.visibility = View.VISIBLE
                    menu.setOnClickListener {
                        PopupMenu(this, it).apply {
                            menuInflater.inflate(R.menu.board_menu, this.menu)
                            setOnMenuItemClickListener { menuItem ->
                                when (menuItem.itemId) {
                                    R.id.modifyBtn -> startActivityForResult(Intent(this@BoardDetailActivity, BoardWriteActivity::class.java).putExtra(OBJECT_KEY, boardInfo.objectId), RequestCode.OBJECT_ADD.value)
                                    R.id.deleteBtn -> showDeleteBoardDialog(boardInfo.objectId)
                                }
                                false
                            }
                        }.show()

                    }
                } else {
                    menu.visibility = View.INVISIBLE
                }

                // Set Writer Profile
                getUserInfo(boardInfo.writerUid)?.continueWith { task1 ->
                    text_nickname.text = task1.result.nickName
                    imageView.loadImage(task1.result.profilePicUrl)
                }

                // Set Hospital Name
                val hospitalUid = boardInfo.hospitalUid.also { if(it.isEmpty()) return@addSnapshotListener }
                getHospital(hospitalUid).get()
                .continueWith { it->
                    hospital_hashtag.text = it.result.toObject(HospitalInfo::class.java)?.name
                }


            }?.let { addListenerRegistrations(it) }
            // 비로그인
            if(!isLogin()){
                arrayListOf(replyEditView, replySubmit, userProfileImage).forEach { it.visibility = View.GONE }
            }
            // 로그인
            else {
                // 댓글 프사
                if(isUser()) getUserInfo()?.continueWith { it.result.profilePicUrl }
                else getHospitalInfo()?.continueWith { it.result?.profilePicUrl }
                        ?.continueWith {
                            val url = it.result?:return@continueWith
                            userProfileImage.loadImage(url)
                        }

                // 댓글 글자 변경 리스너
                replyEditView.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                        // 입력되는 텍스트에 변화가 있을 때
                        replySubmit.isEnabled = count>0
                    }
                    override fun afterTextChanged(arg0: Editable) {}
                    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                })

                // 댓글 달기
                replySubmit.setOnClickListener { _ ->
                    val reference = getReplies(boardKey)?.document()?:return@setOnClickListener
                    showProgressDialog()
                    reference.set(ReplyInfo(
                            writerUid = getUid()?:return@setOnClickListener,
                            contents = replyEditView.text.toString(),
                            objectId = reference.id,
                            boardId = boardKey,
                            writerType = if (isUser()) "user" else "hospital"
                    ))
                            .onSuccessTask { _ ->
                                // 댓글 카운트 추가
                                addReplyCount(boardKey)
                            }
                            .addOnSuccessListener {
                                toast("댓글 추가 완료")
                                hideSoftKeyboard()
                                replyEditView.text.clear()
                            }
                            .addOnCompleteListener { hideProgressDialog() }

                }

                // like 버튼
                button_like.isEnabled = false
                getUserLikeBoard(boardKey)?.get()?.addOnSuccessListener { snapshot ->
                    button_like.isChecked = snapshot.exists()
                    button_like.isEnabled = true
                    button_like.setOnClickListener { view ->
                        view.isEnabled = false
                        clickBoardLikeBtn(boardKey,button_like.isChecked)?.addOnCompleteListener { view.isEnabled = true }
                    }
                }

            }

            // 댓글 리스트 출력
            getReplies(boardKey)?.orderBy("writeDate", Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.toObjects(ReplyInfo::class.java).also{ mutableList ->
                    Tasks.whenAllSuccess<Pair<String, SomebodyInfo>>(
                            mutableList?.filter{ it.writerType == "user" }?.mapNotNull { replyInfo ->
                                getUserInfo(replyInfo.writerUid)?.continueWith { replyInfo.writerUid to SomebodyInfo(it.result.profilePicUrl, it.result.nickName, amIWriter(replyInfo)) }
                            }?.plus(mutableList.filter{ it.writerType == "hospital" }.mapNotNull { replyInfo ->
                                getHospitalInfo(replyInfo.writerUid)?.continueWith { replyInfo.writerUid to SomebodyInfo(it.result?.profilePicUrl!!, it.result?.name!!, amIWriter(replyInfo)) }
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

    private fun amIWriter(replyInfo: ReplyInfo) =
            if(getAuth()?.currentUser == null) false
            else replyInfo.writerUid == getUid()

    private fun prepareUi() {
        setActionBarLeftImage(R.drawable.back)
        hidActionBarRight()
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })
    }
}
