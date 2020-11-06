package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.fragment.*
import com.dac.gapp.andac.model.ActivityResultEvent
import com.dac.gapp.andac.util.Preference
import com.dac.gapp.andac.util.RxBus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : BaseActivity() {
    private var fragments: HashMap<Int, Fragment> = HashMap()
    private var current : Int = 0

    init {
        fragments[R.id.navigation_main] = MainFragment()
//        fragments[R.id.navigation_search_hospital] = SearchHospitalFragment()
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

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        BottomNavigationViewHelper.removeShiftMode(navigation)

        if (savedInstanceState != null) {
            return
        }

        // Create a new Fragment to be placed in the activity layout
        val firstFragment = fragments[R.id.navigation_main]
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        firstFragment?.arguments = intent.extras

        // Add the fragment to the 'fragment_container' FrameLayout
        supportFragmentManager.beginTransaction()
                .add(R.id.layoutFragmentContainer, firstFragment).commit()
    }

    private var mDisposable: Disposable? = null

    override fun onResume() {
        super.onResume()
        mDisposable = MainFragment.observeTextViewClickEvent()
                .subscribe {id ->
                    navigation?.apply {
                        selectedItemId = id
                    }
                }
    }

    override fun onPause() {
        super.onPause()
        mDisposable?.dispose()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 이벤트 전달
        RxBus.publish(ActivityResultEvent(requestCode, resultCode, data))
    }

    override fun onDestroy() {
        val isAutoLogin = getSharedPreferences(Preference.FileName, 0).getBoolean(Preference.AutoLogin, false)
        if(!isAutoLogin) getAuth()?.signOut()
        super.onDestroy()
    }
}
