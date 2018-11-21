package com.dac.gapp.andac

import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.base.BaseActivity
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_terms.*

class TermsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        setActionBarLeftImage(R.drawable.back)
//        setActionBarCenterText("약관")
        setActionBarRightImage(R.drawable.defult_img)
        setOnActionBarLeftClickListener(View.OnClickListener { finish() })

        val policyFile = intent.getStringExtra("policyFile")?:return

        FirebaseStorage.getInstance().getReference("policy/$policyFile").downloadUrl.addOnSuccessListener {
            webView.loadUrl(it.toString())
        }
    }

}
