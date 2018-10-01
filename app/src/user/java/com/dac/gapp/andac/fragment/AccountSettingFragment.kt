package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.NoticeActivity

import com.dac.gapp.andac.R
import com.dac.gapp.andac.base.BaseFragment
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.user.fragment_account_setting.*
import org.jetbrains.anko.startActivity


/**
 * A simple [Fragment] subclass.
 */
class AccountSettingFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile_change_btn.setOnClickListener { _ ->
            // 프로필 사진 변경
            context?.apply {
                getAlbumImage{uri ->
                    showProgressDialog()
                    val path = getUser()?.path?: return@getAlbumImage
                    FirebaseStorage.getInstance().getReference(path).child("profilePic.jpg").putFile(uri).continueWithTask { it ->
                        getUser()?.set(mapOf("profilePicUrl" to it.result.downloadUrl.toString()), SetOptions.merge())
                    }.addOnCompleteListener { hideProgressDialog() }
                }
            }
        }

        change_pw_btn.setOnClickListener { context?.findPassword() }

        // 버전 정보
        versionText.setOnClickListener { context?.toastVersion() }

        // 공지사항
        noticeBtn.setOnClickListener { context?.startActivity<NoticeActivity>() }
    }

}// Required empty public constructor
