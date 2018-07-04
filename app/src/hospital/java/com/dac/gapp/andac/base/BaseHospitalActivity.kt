package com.dac.gapp.andac.base

import android.annotation.SuppressLint
import android.util.Log
import com.dac.gapp.andac.R
import com.dac.gapp.andac.model.firebase.HospitalInfo

@SuppressLint("Registered")
open class BaseHospitalActivity : BaseActivity() {

    fun onCheckApproval(onSuccess: (Boolean) -> Unit){
        getUid()?.let {
            getHospitals().document(it).get().addOnCompleteListener{ task ->
                if(task.isSuccessful){
                    val hospitalInfo = task.result.toObject(HospitalInfo::class.java)
                    if(hospitalInfo != null) {
                        onSuccess(hospitalInfo.isApproval)
                    } else {
                        Log.d(KBJ,"hospitalInfo is null")
                    }
                } else {
                    Log.d(KBJ, task.exception.toString())
                    onSuccess(false)
                }
            }
        }?:goToLogin()
    }

}
