package com.dac.gapp.andac.base

import android.annotation.SuppressLint
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
                onSuccess(hospitalInfo!!.isApproval)
            }
        }
    }

}
