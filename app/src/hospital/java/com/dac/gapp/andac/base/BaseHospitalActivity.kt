package com.dac.gapp.andac.base

import android.annotation.SuppressLint
import android.util.Log
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.firebase.HospitalInfo

@SuppressLint("Registered")
open class BaseHospitalActivity : BaseActivity() {

    fun onCheckApproval(onSuccess: () -> Unit){
        getHospitals().document(getUid()).get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val hospitalInfo = task.result.toObject(HospitalInfo::class.java)

                if(hospitalInfo!!.isApproval) {
                    onSuccess()
                } else {
                    toast(getString(R.string.waitApproval))
                }
            }
        }
    }

    fun onCheckApproval(onSuccess: (Boolean) -> Unit){
        getHospitals().document(getUid()).get().addOnCompleteListener{ task ->
            if(task.isSuccessful){
                val hospitalInfo = task.result.toObject(HospitalInfo::class.java)
                if(hospitalInfo != null) {
                    onSuccess(hospitalInfo.isApproval)
                } else {
                    // TODO 회원가입이 잘못됬을 경우... 처리해야함..
                    toast("hospitalInfo is null")
                }
            } else {
                Log.d(KBJ, task.exception.toString())
                onSuccess(false)
            }
        }
    }

}
