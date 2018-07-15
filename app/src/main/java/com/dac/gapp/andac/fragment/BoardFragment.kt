package com.dac.gapp.andac.fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.BoardWriteActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.adapter.BoardRecyclerAdapter
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.model.firebase.BoardInfo
import com.dac.gapp.andac.model.firebase.UserInfo
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_board.*


/**
 * A simple [Fragment] subclass.
 */
class BoardFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        context?.apply {
            if(isUser()) fabWriteBoard.setOnClickListener { _ ->
                getCurrentUser()?.let{ startActivity(Intent(context, BoardWriteActivity::class.java)) }
                ?:goToLogin()
            }
            else {
                fabWriteBoard.visibility = View.INVISIBLE
            }
        }

        // set recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // set boardTabGroup
        boardTabGroup.apply {
            setOnCheckedChangeListener{ _ : Any?, checkedId : Int ->
                when(checkedId) {
                    R.id.free_board     -> setAdapter(getString(R.string.free_board))
                    R.id.review_board   -> setAdapter(getString(R.string.review_board))
                    R.id.question_board -> setAdapter(getString(R.string.question_board))
                    R.id.hot_board      -> setAdapter(getString(R.string.hot_board))
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if(boardTabGroup.checkedRadioButtonId == -1) boardTabGroup.check(R.id.free_board)
    }

    private var registration: ListenerRegistration? = null

    private fun setAdapter(type : String) {
        context?.apply {
            showProgressDialog()
            registration = getBoards().whereEqualTo("type", type)
                    .addSnapshotListener{ querySnapshot: QuerySnapshot?, _: FirebaseFirestoreException? ->
                        querySnapshot?.let {
                            it.toObjects(BoardInfo::class.java).let { boardInfos ->
                                boardInfos.groupBy { it.writerUid }
                                        .map { getUser(it.key)?.get() }
                                        .let { Tasks.whenAllSuccess<DocumentSnapshot>(it) }
                                        .addOnSuccessListener {
                                            it
                                                    .filter { it != null }
                                                    .map { it.id to it.toObject(UserInfo::class.java) }
                                                    .toMap().also { userInfoMap ->
//                                                        recyclerView.adapter = BoardRecyclerAdapter(context, userInfoMap, it)
                                                        val adapter = recyclerView.adapter
                                                        if (adapter is BoardRecyclerAdapter) {
                                                            adapter.setDataList(boardInfos, userInfoMap)
                                                        } else {
                                                            recyclerView.adapter = BoardRecyclerAdapter(context, boardInfos, userInfoMap)
                                                        }
                                                    }
                                        }
                            }.addOnCompleteListener{hideProgressDialog()}
                        }?:let{
                            hideProgressDialog()
                        }
                    }

        }
    }

    override fun onStop() {
        super.onStop()
        registration?.remove()
    }

}

