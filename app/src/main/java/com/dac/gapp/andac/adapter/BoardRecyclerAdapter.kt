package com.dac.gapp.andac.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.ToggleButton
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ItemCardBinding
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.extension.likeCnt
import com.dac.gapp.andac.extension.loadImage
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import kotlinx.android.synthetic.main.base_item_card.view.*

class BoardRecyclerAdapter(
        private val context : BaseActivity?,
        private var mDataList: List<BoardInfo>,
        private var userInfoMap: Map<String, UserInfo>,
        private var hospitalInfoMap: Map<String, HospitalInfo>,
        private var likeSet: Set<String> = mutableSetOf(),
        private var onItemClickListener : ((BoardInfo, UserInfo) -> Unit)? = null
) : RecyclerView.Adapter<BoardRecyclerAdapter.BoardHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardHolder {
        return BoardHolder(parent)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BoardHolder, position: Int) {
        context?:return

        val boardInfo = mDataList[position]
        val userInfo = userInfoMap[boardInfo.writerUid] ?: return

        with(holder) {

            // binding 객체
            binding?.also {
                it.boardInfo = boardInfo
                it.hospitalInfo = hospitalInfoMap[mDataList[position].hospitalUid]
                it.userInfo = userInfo
            }

            // 게시판 사진
            pictures.forEach { it.visibility = View.GONE }
            boardInfo.pictureUrls?.forEachIndexed { index, url ->
                pictures[index].apply { visibility = View.VISIBLE }.loadImage(url)
            }

            // 클릭시 디테일 게시물 이동
            arrayListOf(button_writting, itemView).forEach { view ->
                view.setOnClickListener {
                    context.afterCheckLoginDo { onItemClickListener?.invoke(boardInfo, userInfo) }
                }
            }

            // 좋아요 클릭
            button_like.isEnabled = context.isUser()  // 유저만 사용 가능
            if (context.isLogin()) button_like.isChecked = likeSet.contains(boardInfo.objectId) // 좋아요 여부 출력
            button_like.setOnClickListener { view ->
                if (context.isLogin()) {
                    view.isEnabled = false
                    context.clickBoardLikeBtn(boardInfo.objectId, button_like.isChecked)?.addOnCompleteListener { view.isEnabled = true }

                    boardInfo.likeCount += if (button_like.isChecked) 1 else -1
                    likeText.likeCnt(boardInfo.likeCount)
                } else {
                    button_like.isChecked = false
                    context.goToLogin{
                        // Refresh Like Set
                        context.getUserLikeBoards()?.get()?.addOnSuccessListener { snapshot ->
                            likeSet = snapshot.mapNotNull { it.id }.toHashSet()
                            notifyDataSetChanged()
                        }
                    }
                }
            }

            // 메뉴 버튼
            if(context.isLogin() && boardInfo.writerUid == context.getUid()){
                menu.visibility = View.VISIBLE
                menu.setOnClickListener {
                    PopupMenu(this@BoardRecyclerAdapter.context, it).apply {
                        menuInflater.inflate(R.menu.board_menu, this.menu)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.modifyBtn -> context.startActivityForResult(Intent(context, BoardWriteActivity::class.java).putExtra(context.OBJECT_KEY, boardInfo.objectId), RequestCode.OBJECT_ADD.value)
                                R.id.deleteBtn -> context.showDeleteBoardDialog(boardInfo.objectId)
                            }
                            false
                        }
                    }.show()

                }
            } else {
                menu.visibility = View.INVISIBLE
            }

        }
    }



    override fun getItemCount(): Int {
        return mDataList.size
    }

    class BoardHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card, parent, false)) {
        var binding : ItemCardBinding? = DataBindingUtil.bind(itemView)
        val button_like: ToggleButton = itemView.button_like
        val button_writting: Button = itemView.button_writting
        val pictures = arrayListOf(itemView.picture_1, itemView.picture_2, itemView.picture_3)
        val menu: Button = itemView.menu
        val likeText = itemView.likeText
    }

}
