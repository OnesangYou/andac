package com.dac.gapp.andac.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.model.ActivityResultEvent
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.dac.gapp.andac.util.RxBus
import com.dac.gapp.andac.util.getFullFormat
import kotlinx.android.synthetic.main.base_item_card.view.*
import org.jetbrains.anko.alert

class BoardRecyclerAdapter
(private val context : BaseActivity?, private var mDataList: List<BoardInfo>, private var userInfoMap: Map<String, UserInfo>, private var hospitalInfoMap: Map<String, HospitalInfo>, private var onItemClickListener : ((BoardInfo, UserInfo) -> Unit)? = null) : RecyclerView.Adapter<BoardRecyclerAdapter.BoardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardHolder {
        return BoardHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BoardHolder, position: Int) {
        val item = mDataList[position]
        val userInfo = userInfoMap[item.writerUid]?:return

        with(holder){
            title_text.text = item.title //아이템을 홀더에 넣어주면 되요 지금 타이틀넣은것
            contents_text.text = item.contents //아이템을 홀더에 넣기 지금건 컨텐츠
            date.text = item.writeDate?.getFullFormat()
            replyText.text = "댓글 ${item.replyCount} 개"
            likeText.text = "좋아요 ${item.likeCount} 개"

            item.pictureUrls?.forEachIndexed { index, url ->
                Glide.with(context).load(url).into(pictures[index])
            }

            pictures.forEachIndexed { index, pic ->
                if(item.pictureUrls != null && item.pictureUrls!!.size > index){
                    Glide.with(context).load(item.pictureUrls!![index]).into(pic)
                }
            }

            userInfo.apply {
                text_nickname.text = nickName
            }

            itemView.setOnClickListener{onItemClickListener?.invoke(item, userInfo)}

            button_like.setOnClickListener {
                Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show()
            }

            button_writting.setOnClickListener {
                Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show()
            }

            // 수정하기 버튼

            context?.let {ba->
                if(item.writerUid == ba.getUid()) {
                    menu.visibility = View.VISIBLE
                    menu.setOnClickListener {
                        PopupMenu(context, it).apply {
                            menuInflater.inflate(R.menu.board_menu, this.menu)
                            setOnMenuItemClickListener { menuItem ->
                                when (menuItem.itemId) {
                                    R.id.modifyBtn -> ba.startActivityForResult(Intent(ba, BoardWriteActivity::class.java).putExtra(ba.OBJECT_KEY, item.objectId), RequestCode.OBJECT_ADD.value)
                                    R.id.deleteBtn -> ba.showDeleteBoardDialog(item.objectId)
                                }
                                false
                            }
                        }.show()

                    }
                } else {
                    menu.visibility = View.INVISIBLE
                }
            }

            // hospital_hashtag
            hospital_hashtag.text = hospitalInfoMap[item.hospitalUid]?.name

        }
    }

    private fun BaseActivity.showDeleteBoardDialog(boardId : String){
        showProgressDialog()
        alert(title = "게시물 삭제", message = "게시물을 삭제하시겠습니까?") {
            positiveButton("YES"){ _ ->
                // 삭제 진행
                showProgressDialog()
                getBoard(boardId)?.delete()?.addOnCompleteListener {
                    hideProgressDialog()
                    RxBus.publish(ActivityResultEvent(
                            requestCode = RequestCode.OBJECT_ADD.value,
                            resultCode = Activity.RESULT_OK
                    ))
                }
            }

            negativeButton("NO"){hideProgressDialog()}
        }.show()
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    class BoardHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card, parent, false)) {

        val title_text: TextView = itemView.title_text
        val contents_text: TextView = itemView.contents_text
        val button_like: Button = itemView.button_like
        val button_writting: Button = itemView.button_writting
        val text_nickname: TextView = itemView.text_nickname
        val date: TextView = itemView.date
        val pictures = arrayListOf(itemView.picture_1, itemView.picture_2, itemView.picture_3)
        val hospital_hashtag: TextView = itemView.hospital_hashtag
        val menu: Button = itemView.menu
        val replyText = itemView.replyText
        val likeText = itemView.likeText
    }
}
