package com.dac.gapp.andac.fragment


import android.os.Bundle
import androidx.core.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.NoticeActivity
import com.dac.gapp.andac.R
import com.dac.gapp.andac.TermsActivity
import com.dac.gapp.andac.base.BaseFragment
import com.dac.gapp.andac.databinding.FragmentAccountSettingBinding
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import org.jetbrains.anko.startActivity


/**
 * A simple [Fragment] subclass.
 */
class AccountSettingFragment : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflate(inflater,R.layout.fragment_account_setting, container, false)
    }

    lateinit var binding: FragmentAccountSettingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getBinding()

        binding.profileChangeBtn.setOnClickListener { _ ->
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

        binding.changePwBtn.setOnClickListener { context?.findPassword() }

        // 버전 정보
        binding.versionText.setOnClickListener { context?.toastVersion() }

        // 공지사항
        binding.noticeBtn.setOnClickListener { context?.startActivity<NoticeActivity>() }

        // 문의
        binding.questionText.setOnClickListener { context?.sendMail() }

        // 이용약관
        binding.termBtn.setOnClickListener { context?.startActivity<TermsActivity>("policyFile" to "andac_service_term.txt") }

        // 개인정보 취급방침
        binding.privacyPolicyBtn.setOnClickListener { context?.startActivity<TermsActivity>("policyFile" to "andac_privacy_policy.txt") }
    }

}// Required empty public constructor
