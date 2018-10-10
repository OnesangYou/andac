package com.dac.gapp.andac.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dac.gapp.andac.R
import com.dac.gapp.andac.databinding.FragmentJoinCertiBinding


/**
 * A simple [Fragment] subclass.
 */
class JoinCertiFragment : JoinBaseFragment() {

    lateinit var binding: FragmentJoinCertiBinding

    override fun onChangeFragment() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflate(inflater, R.layout.fragment_join_certi, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = getBinding()

        // 계좌 사진 업로드
        binding.bankAccountUploadBtn.setOnClickListener{
            // Image File 가져옴
            context?.getAlbumImage{ albumFile ->
                getJoinActivity().bankAccountPicUri = albumFile }

        }

        // 사업자등록증 업로드
        binding.busiRegiUploadBtn.setOnClickListener {
            context?.getAlbumImage{albumFile ->
                getJoinActivity().busiRegiPicUri = albumFile
            }
        }

        // Next 버튼
        binding.nextBtn.setOnClickListener { _ ->
            getJoinActivity().apply {

                arrayListOf(binding.busnissId, binding.bankName, binding.bankAccountNumberText, binding.bankAccountMasterText).forEach {
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

                hospitalInfo.busniss_id = binding.busnissId.text.toString()
                hospitalInfo.bankName = binding.bankName.text.toString()
                hospitalInfo.bankAccountNumber = binding.bankAccountNumberText.text.toString()
                hospitalInfo.bankAccountMaster = binding.bankAccountMasterText.text.toString()

                goToNextView()
            }

        }

    }




}// Required empty public constructor
