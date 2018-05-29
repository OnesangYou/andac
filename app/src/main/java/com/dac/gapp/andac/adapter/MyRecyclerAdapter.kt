package com.dac.gapp.andac.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.dac.gapp.andac.model.CardItem
import com.dac.gapp.andac.R

class MyRecyclerAdapter
//여기까지가  클릭리스너 끝나는 부분임. 만약에 외부에서 연결이 됬다고 한다면 실제로 클릭이일어나는 부분은 바인드뷰홀더에서 이루어져야한다.
//외부에서 데이터를 받을 수 있게 컨스트럭터도 하나 만들어봄.
(private val context : Context?, private val mDataList: List<CardItem>) : RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {

    override//뷰홀더를 만드는 부분이고 리턴을 해주면 바인더 부분으로 들어
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    //데이터를 바운드해주는 부분 데이터를 세팅해 줄 수 있다.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mDataList[position]
        holder.title.setText(item.title) //아이템을 홀더에 넣어주면 되요 지금 타이틀넣은것
        holder.contents.setText(item.contents) //아이템을 홀더에 넣기 지금건 컨텐츠

        // 클릭이되었을때 일어나는건 바인두뷰홀더에서 작성해주어야함.
        // 만약 외부에서 리스너를 연결을 했다면
        holder.itemView.setOnClickListener {
            Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show()
        }

        //여기서 좋아요버튼 클릭되는 리스너만들것임.
        holder.like.setOnClickListener {
            Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show()
        }
        //여기서 댓글쓰기버튼 클릭되는 리스너 만들것. 만들다보니까 온라이크버튼클릭드, 온롸이팅 클릭드 어케 자동으로 생기나 궁금
        holder.writting.setOnClickListener {
            Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show()
        }

    }

    // 어댑터가 가지고있는 아이템의 개수를 지정해주면 된다. mdatalist로 밖에서 데이타 들고오기때문에 그것에 크기에 맞춰주면 된다.
    override fun getItemCount(): Int {
        return mDataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var title: TextView
        internal var contents: TextView
        internal var like: Button
        internal var writting: Button

        init {
            title = itemView.findViewById(R.id.title_text)
            contents = itemView.findViewById(R.id.contents_text)
            like = itemView.findViewById(R.id.button_like)
            writting = itemView.findViewById(R.id.button_writting)
        }
    }
}
