package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.View
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.fragment.*
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity() {
    private var fragments: HashMap<Int, Fragment> = HashMap()
    private var current : Int = 0

    init {
        fragments[R.id.navigation_main] = MainFragment()
        fragments[R.id.navigation_search_hospital] = SearchHospitalFragment()
        fragments[R.id.navigation_chat] = ChatRoomFragment()
        fragments[R.id.navigation_board] = BoardFragment()
        fragments[R.id.navigation_event] = EventListFragment()
    }

    // static method
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        changeFragment(fragments[item.itemId]!!)
        current = item.itemId
        return@OnNavigationItemSelectedListener true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Get the ActionBar here to configure the way it behaves.
        supportActionBar?.apply{
            setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(false) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        BottomNavigationViewHelper.removeShiftMode(navigation)


        if (savedInstanceState != null) {
            return
        }

        // Create a new Fragment to be placed in the activity layout
        val firstFragment = MainFragment()

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        firstFragment.arguments = intent.extras

        // Add the fragment to the 'fragment_container' FrameLayout
        supportFragmentManager.beginTransaction()
                .add(R.id.layoutFragmentContainer, firstFragment).commit()

        // Go to My Page
        my_page.setOnClickListener {

            // 로그인 상태 체크
            if(getCurrentUser() == null){
                goToLogin(true)
            } else {
                startActivity(Intent(this, MyPageActivity::class.java))
            }

        }
    }

    internal object BottomNavigationViewHelper {

        @SuppressLint("RestrictedApi")
        fun removeShiftMode(view: BottomNavigationView) {
            val menuView = view.getChildAt(0) as BottomNavigationMenuView
            try {
                val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
                shiftingMode.isAccessible = true
                shiftingMode.setBoolean(menuView, false)
                shiftingMode.isAccessible = false
                for (i in 0 until menuView.childCount) {
                    val item = menuView.getChildAt(i) as BottomNavigationItemView
                    item.setShiftingMode(false)
                    // set once again checked value, so view will be updated
                    item.setChecked(item.itemData.isChecked)
                }
            } catch (e: NoSuchFieldException) {
                Timber.e("Unable to get shift mode field")
            } catch (e: IllegalAccessException) {
                Timber.tag("ERROR ILLEGAL ALG").e("Unable to change value of shift mode")
            }

        }
    }

    override fun onBackPressed() {
        backPressed()
    }

}
