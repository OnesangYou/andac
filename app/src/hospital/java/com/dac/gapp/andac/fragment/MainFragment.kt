package com.dac.gapp.andac.fragment

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.*
import com.dac.gapp.andac.adapter.ColumnRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentMainBinding
import com.dac.gapp.andac.model.firebase.ColumnInfo
import com.dac.gapp.andac.model.firebase.HospitalInfo
import com.dac.gapp.andac.model.firebase.NoticeInfo
import com.dac.gapp.andac.util.OnItemClickListener
import com.dac.gapp.andac.util.addOnItemClickListener
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.jetbrains.anko.startActivity

class MainFragment : BaseFragment() {
    lateinit var binding: FragmentMainBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_main, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUi()

        // get
        context?.getHospitalInfo()?.addOnSuccessListener { hospitalInfo ->
            hospitalInfo ?: return@addOnSuccessListener
            binding.txtviewHospitalName.text = hospitalInfo.name
            if (!hospitalInfo.approval) {
                listOf(binding.layoutDashboard, binding.btnConsultingBoard, binding.layoutManagement).forEach { it.visibility = View.INVISIBLE }
                binding.requestText.visibility = View.VISIBLE
                binding.requestText.text = hospitalInfo.requestStr

            }
        }
        binding.txtviewMoreNotice.setOnClickListener{ startActivity(Intent(context, NoticeActivity::class.java))}
        binding.btnConsultingBoard.setOnClickListener { startActivity(Intent(context, ConsultBoardActivity::class.java)) }
        binding.btnHospitalEventManagement.setOnClickListener { context?.startActivity<HospitalEventListActivity>() }
        binding.btnHospitalAdManagement.setOnClickListener { startActivity(HospitalAdApplicationActivity.createIntentForAdManagement(requireContext())) }
        binding.btnHospitalAdApplication.setOnClickListener { startActivity(HospitalAdApplicationActivity.createIntentForAdApplication(requireContext())) }

        binding.myColumnsBtn.setOnClickListener { context?.startActivity<HospitalColumnListActivity>() }

        binding.columnList.layoutManager = GridLayoutManager(context, 2)
        binding.columnList.adapter = ColumnRecyclerAdapter(context?:return, arrayListOf(ColumnInfo(),ColumnInfo(),ColumnInfo(),ColumnInfo()), mapOf())
        setNotice()
        setAdapter()

        binding.moreCalum.setOnClickListener {
            context?.startActivity<ColumnActivity>()
        }
    }

    private fun setNotice() {
        val db = FirebaseFirestore.getInstance()
        val list: ArrayList<NoticeInfo> = ArrayList()
        db.collection("notice").orderBy("writeDate", Query.Direction.DESCENDING)
                .limit(4).get().addOnSuccessListener { snapshot ->
            for (document in snapshot) {
                document.toObject(NoticeInfo::class.java).let {
                    list.add(it)
                }
            }
            binding.noticeList = list
            binding.notifyChange()
        }
    }

    private fun setAdapter() {
        (context as MainActivity).apply {

            getColumns()
                    .whereEqualTo("approval", true) // 승인된 컬럼만 보임
                    .orderBy("writeDate", Query.Direction.DESCENDING).limit(4).get().addOnSuccessListener { querySnapshot ->
                querySnapshot
                        ?.let { it -> it.map { getColumn(it.id)?.get() } }
                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                        .onSuccessTask { it ->
                            val columnInfos = it?.filterNotNull()?.map { it.toObject(ColumnInfo::class.java)!! }
                            columnInfos?.groupBy { it.writerUid }
                                    ?.map { getHospital(it.key).get() }
                                    .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                                    .addOnSuccessListener { mutableList ->
                                        mutableList
                                                .filterNotNull()
                                                .map { it.id to it.toObject(HospitalInfo::class.java) }
                                                .toMap().also { hospitalInfoMap ->
                                                    binding.columnList.swapAdapter(ColumnRecyclerAdapter(this, columnInfos!!, hospitalInfoMap), false)
                                                    binding.columnList.addOnItemClickListener(object : OnItemClickListener {
                                                        override fun onItemClicked(position: Int, view: View) {
                                                            // 디테일
                                                            startActivity(Intent(this@apply, ColumnDetailActivity::class.java).putExtra(OBJECT_KEY, columnInfos[position].objectId))
                                                        }
                                                    })
                                                }
                                    }
                        }
                        .addOnCompleteListener { hideProgressDialog() }
            }
        }
    }

    private fun prepareUi() {
        context?.let { context ->
            context.setActionBarLeftImage(R.drawable.mypage)
            context.setActionBarRightImage(R.drawable.bell)
            context.setOnActionBarLeftClickListener(View.OnClickListener {
                // 로그인 상태 체크
                if (getCurrentUser() == null) {
                    goToLogin(true)
                } else {
                    startActivity(Intent(context, MyPageActivity::class.java))
                }
            })
            context.setOnActionBarRightClickListener(View.OnClickListener {
                //                MyToast.showShort(context, "TODO: 알림 설정")
            })
        }
    }

}

