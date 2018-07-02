package com.dac.gapp.andac.fragment


import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.hospital.fragment_join_certi.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class JoinCertiFragment : JoinBaseFragment() {
    override fun onChangeFragment() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_certi, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextBtn.setOnClickListener { getJoinActivity().goToNextView() }

        // 계좌 사진 업로드
        bankAccountUploadBtn.setOnClickListener{
            // Image File 가져옴
            context?.startAlbumImageUri()?.addOnSuccessListener { albumFile ->
                getJoinActivity().bankAccountPicUri = Uri.fromFile(File(albumFile.path)) }

        }

        // 사업자등록증 업로드
        busiRegiUploadBtn.setOnClickListener {
            context?.startAlbumImageUri()?.addOnSuccessListener {albumFile ->
                getJoinActivity().busiRegiPicUri = Uri.fromFile(File(albumFile.path))
            }
        }

    }




}// Required empty public constructor
