package com.dac.gapp.andac.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.base_item_card.*
import kotlinx.android.synthetic.main.base_item_card.view.*
import org.jetbrains.anko.alert

class BoardRecyclerAdapter
//여기까지가  클릭리스너 끝나는 부분임. 만약에 외부에서 연결이 됬다고 한다면 실제로 클릭이일어나는 부분은 바인드뷰홀더에서 이루어져야한다.
//외부에서 데이터를 받을 수 있게 컨스트럭터도 하나 만들어봄.
(private val context : BaseActivity?, private var mDataList: List<BoardInfo>, private var userInfoMap: Map<String, UserInfo?>) : RecyclerView.Adapter<BoardRecyclerAdapter.RepositoryHolder>() {

    override//뷰홀더를 만드는 부분이고 리턴을 해주면 바인더 부분으로 들어
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryHolder {
//        val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_card, parent, false)
        return RepositoryHolder(parent)
    }

    fun setDataList(mDataList: List<BoardInfo>, userInfoMap: Map<String, UserInfo?>){
        this.mDataList = mDataList
        this.userInfoMap = userInfoMap
        this.notifyDataSetChanged()
    }

    //데이터를 바운드해주는 부분 데이터를 세팅해 줄 수 있다.
    override fun onBindViewHolder(holder: RepositoryHolder, position: Int) {
        val item = mDataList[position]
        val userInfo = userInfoMap[item.writerUid]

        with(holder){
            title_text.text = item.title //아이템을 홀더에 넣어주면 되요 지금 타이틀넣은것
            contents_text.text = item.contents //아이템을 홀더에 넣기 지금건 컨텐츠


            val pictures = arrayListOf<ImageView>(picture_1, picture_2, picture_3)
            item.pictureUrls?.forEachIndexed { index, url ->
                Glide.with(context).load(url).into(pictures[index])
            }

            userInfo?.apply {
                text_nickname.text = nickName
            }


            itemView.setOnClickListener {
                Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show()
            }

            button_like.setOnClickListener {
                Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show()
            }

            button_writting.setOnClickListener {
                Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show()
            }

            // 수정하기 버튼

            context?.let {ba->

                if(item.writerUid == ba.getUid()) {
                    modifyBtn.visibility = View.VISIBLE
                    modifyBtn.setOnClickListener {
                        ba.startActivity(Intent(ba, BoardWriteActivity::class.java).putExtra(ba.BOARD_KEY, item.boardId))
                    }
                    deleteBtn.visibility = View.VISIBLE
                    deleteBtn.setOnClickListener {
                        // 삭제 다이얼로그
                        ba.showDeleteBoardDialog(item.boardId)
                    }

                } else {
                    modifyBtn.visibility = View.INVISIBLE
                    deleteBtn.visibility = View.INVISIBLE

                }
            }
        }
    }

    private fun BaseActivity.showDeleteBoardDialog(boardId : String){
        showProgressDialog()
        alert(title = "게시물 삭제", message = "게시물을 삭제하시겠습니까?") {
            positiveButton("YES"){
                // 삭제 진행
                showProgressDialog()
                getBoard(boardId)
                        .delete()
                        .addOnCompleteListener { hideProgressDialog() }
            }

            negativeButton("NO"){}
        }.show()
    }


    override fun getItemCount(): Int {
        return mDataList.size
    }

    abstract class AndroidExtensionsViewHolder(override val containerView: View)
        : RecyclerView.ViewHolder(containerView), LayoutContainer

    class RepositoryHolder(parent: ViewGroup) : AndroidExtensionsViewHolder(
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card, parent, false)) {

        val title_text: TextView = itemView.title_text
        val contents_text: TextView = itemView.contents_text
        val button_like: Button = itemView.button_like
        val button_writting: Button = itemView.button_writting
        val text_nickname: TextView = itemView.text_nickname
        val date: TextView = itemView.date
        val modifyBtn: Button = itemView.modifyBtn
    }
}
