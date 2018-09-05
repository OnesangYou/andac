package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import kotlinx.android.synthetic.hospital.fragment_join_certi.*


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

        // 계좌 사진 업로드
        bankAccountUploadBtn.setOnClickListener{
            // Image File 가져옴
            context?.getAlbumImage()?.subscribe { albumFile ->
                getJoinActivity().bankAccountPicUri = albumFile }

        }

        // 사업자등록증 업로드
        busiRegiUploadBtn.setOnClickListener {
            context?.getAlbumImage()?.subscribe {albumFile ->
                getJoinActivity().busiRegiPicUri = albumFile
            }
        }

        // Next 버튼
        nextBtn.setOnClickListener { _ ->
            getJoinActivity().apply {

                arrayListOf(busniss_id, bank_name, bankAccountNumberText, bankAccountMasterText).forEach {
                    if(it.text.isNullOrEmpty()){
                        toast(it.hint)
                        return@setOnClickListener
                    }
                }

                busiRegiPicUri?:let{
                    toast("사업자등록증을 업로드하세요")
                    return@setOnClickListener
                }

                bankAccountPicUri?:let{
                    toast("통장사본을 업로드하세요")
                    return@setOnClickListener
                }

                hospitalInfo.busniss_id = busniss_id.text.toString()
                hospitalInfo.bankName = bank_name.text.toString()
                hospitalInfo.bankAccountNumber = bankAccountNumberText.text.toString()
                hospitalInfo.bankAccountMaster = bankAccountMasterText.text.toString()

                goToNextView()
            }

        }

    }




}// Required empty public constructor
