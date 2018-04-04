package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.dac.gapp.andac.fragment.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    // static method
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_main -> {
                changeFragment(MainFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_search_hospital -> {
                changeFragment(SearchHospitalFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_chat -> {
                changeFragment(ChatRoomFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_board -> {
                changeFragment(BoardFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_event -> {
                changeFragment(EventListFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Get the ActionBar here to configure the way it behaves.
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false) // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

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
                .add(R.id.fragment_container, firstFragment).commit()

        // Go to My Page
        my_page.setOnClickListener({
            val nextIntent = Intent(this, MyPageActivity::class.java)
            startActivity(nextIntent)
        })
    }

    private fun changeFragment(newFragment: Fragment) {
        // Create fragment and give it an argument specifying the article it should show
        val args = Bundle()
        newFragment.arguments = args

        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
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
                Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field")
            } catch (e: IllegalAccessException) {
                Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode")
            }

        }
    }
}
