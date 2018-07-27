package com.dac.gapp.andac

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.enums.RequestCode
import com.dac.gapp.andac.fragment.*

class HospitalAdApplicationActivity : BaseActivity() {
    companion object {
        const val EXTRA_AD_TYPE = "EXTRA_AD_TYPE"
        const val EXTRA_AD_APPLICATION_TYPE = "EXTRA_AD_APPLICATION_TYPE"
        const val EXTRA_AD_MANAGEMNET_TYPE = "EXTRA_AD_MANAGEMNET_TYPE"

        fun createIntentForAdApplication(context: Context): Intent {
            val intent = Intent(context, HospitalAdApplicationActivity::class.java)
            intent.putExtra(EXTRA_AD_TYPE, EXTRA_AD_APPLICATION_TYPE)
            return intent
        }

        fun createIntentForAdManagement(context: Context): Intent {
            val intent = Intent(context, HospitalAdApplicationActivity::class.java)
            intent.putExtra(EXTRA_AD_TYPE, EXTRA_AD_MANAGEMNET_TYPE)
            return intent
        }
    }

    private var fragments: HashMap<Int, Fragment> = HashMap()
    private var current: Int = 0

    init {
        fragments[R.id.navigation_main] = ApplyForHospitalAdFragment()
        fragments[R.id.navigation_search_hospital] = SearchHospitalFragment()
        fragments[R.id.navigation_chat] = ChatRoomFragment()
        fragments[R.id.navigation_board] = BoardFragment()
        fragments[R.id.navigation_event] = EventListFragment()
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