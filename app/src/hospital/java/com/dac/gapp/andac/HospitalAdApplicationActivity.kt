package com.dac.gapp.andac

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.fragment.*

class HospitalAdApplicationActivity : BaseActivity() {
    private var fragments: HashMap<Int, Fragment> = HashMap()
    private var current: Int = 0

    init {
        fragments[R.id.navigation_main] = MainFragment()
        fragments[R.id.navigation_search_hospital] = SearchHospitalFragment()
        fragments[R.id.navigation_chat] = ChatRoomFragment()
        fragments[R.id.navigation_board] = BoardFragment()
        fragments[R.id.navigation_event] = EventListFragment()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, HospitalAdApplicationActivity::class.java)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_ad_application)

        if (savedInstanceState != null) {
            return
        }

        // Create a new Fragment to be placed in the activity layout
        val firstFragment = ApplyForHospitalAdFragment()

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        firstFragment.arguments = intent.extras

        // Add the fragment to the 'fragment_container' FrameLayout
        supportFragmentManager.beginTransaction()
                .add(R.id.layoutFragmentContainer, firstFragment).commit()
    }

}