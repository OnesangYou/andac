package com.dac.gapp.andac

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dac.gapp.andac.adapter.NoticeRecyclerAdapter
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.databinding.ActivityNoticeBinding
import com.dac.gapp.andac.model.Notice
import com.dac.gapp.andac.model.firebase.NoticeInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import timber.log.Timber

class NoticeActivity : BaseActivity() {
    lateinit var binding: ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)
        setActionBarLeftImage(R.drawable.back)
        setActionBarRightImage(R.drawable.defult_img)
        setActionBarCenterText("공지사항")
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })

        binding = getBinding()
        val noticeList : MutableList <ExpandableGroup<*>> = mutableListOf()

        FirebaseFirestore.getInstance()
                .collection("notice")
                .get()
                .addOnCompleteListener { snapshot ->
                    snapshot.result.forEach {
                        data -> data.toObject(NoticeInfo::class.java).run {
                        val list : MutableList<NoticeInfo> = mutableListOf()
                        list.add(this)
                        Timber.d(list[0].title)

                        val notice = Notice(title, list)
                        noticeList.add(notice)

                        Timber.d(toString())
                    } }

                    binding.noticeRecylcerView.let {
                        it.layoutManager = LinearLayoutManager(this)
                        it.adapter =NoticeRecyclerAdapter(this,noticeList)
                    }
                }

    }
}