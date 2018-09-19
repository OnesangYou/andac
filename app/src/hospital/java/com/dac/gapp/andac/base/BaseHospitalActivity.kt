package com.dac.gapp.andac.base

import android.annotation.SuppressLint
import com.dac.gapp.andac.model.firebase.HospitalInfo

@SuppressLint("Registered")
open class BaseHospitalActivity : BaseActivity() {

    fun onCheckApproval(onSuccess: (Boolean) -> Unit){
        getUid()?.let {
            getHospitals().document(it).get().addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val hospitalInfo = task.result.toObject(HospitalInfo::class.java)
                    if(hospitalInfo != null) {
                        onSuccess(hospitalInfo.approval)
                    }
                } else {
                    onSuccess(false)
                }
            }
        }?:goToLogin()
    }

}
