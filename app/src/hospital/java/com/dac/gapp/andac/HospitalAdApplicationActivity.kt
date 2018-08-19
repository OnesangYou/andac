package com.dac.gapp.andac

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.fragment.*

class HospitalAdApplicationActivity : BaseActivity() {
    companion object {
        private const val EXTRA_AD_TYPE = "EXTRA_AD_TYPE"
        private const val EXTRA_AD_APPLICATION_TYPE = "EXTRA_AD_APPLICATION_TYPE"
        private const val EXTRA_AD_MANAGEMENT_TYPE = "EXTRA_AD_MANAGEMENT_TYPE"

        fun createIntentForAdApplication(context: Context): Intent {
            val intent = Intent(context, HospitalAdApplicationActivity::class.java)
            intent.putExtra(EXTRA_AD_TYPE, EXTRA_AD_APPLICATION_TYPE)
            return intent
        }

        fun createIntentForAdManagement(context: Context): Intent {
            val intent = Intent(context, HospitalAdApplicationActivity::class.java)
            intent.putExtra(EXTRA_AD_TYPE, EXTRA_AD_MANAGEMENT_TYPE)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_ad_application)

        if (savedInstanceState != null) {
            return
        }

        val firstFragment =
                if (intent.extras[EXTRA_AD_TYPE] == EXTRA_AD_APPLICATION_TYPE)
                    ApplyForHospitalAdFragment()
                else
                    HospitalAdManagementFragment()

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        firstFragment.arguments = intent.extras

        // Add the fragment to the 'fragment_container' FrameLayout
        supportFragmentManager.beginTransaction()
                .add(R.id.layoutFragmentContainer, firstFragment).commit()
    }

}