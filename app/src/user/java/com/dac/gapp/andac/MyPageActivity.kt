package com.dac.gapp.andac

import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.base.BaseMyPageActivity
import com.dac.gapp.andac.extension.loadImage
import com.dac.gapp.andac.fragment.AccountSettingFragment
import com.dac.gapp.andac.fragment.FavoritesFragment
import com.dac.gapp.andac.fragment.MyBoardsFragment
import com.dac.gapp.andac.model.firebase.UserInfo
import kotlinx.android.synthetic.user.activity_my_page.*

open class MyPageActivity : BaseMyPageActivity() {

    var userInfo: UserInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)
        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText(R.string.mypage)
        setActionBarRightText(R.string.logout)
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        setOnActionBarRightClickListener(View.OnClickListener {
            getAuth()!!.signOut()
            finish()
        })

        // 버튼 그룹 리스너
        myPageRadioGroup.setOnCheckedChangeListener { _, id ->
            when(id){
                R.id.accountSettingBtn -> changeFragment(AccountSettingFragment())
                R.id.favoritBtn -> changeFragment(FavoritesFragment())
                R.id.myBoardsBtn -> changeFragment(MyBoardsFragment())

                else -> {
//                    accountSetting.visibility = View.INVISIBLE
//                    recyclerView.visibility = View.VISIBLE
                }
            }
        }

        // 디폴트 화면
        accountSettingBtn.performClick()

        // Set Profile
        getUser()?.addSnapshotListener { snapshot, _ ->
            snapshot?:return@addSnapshotListener
            val userInfo = snapshot.toObject(UserInfo::class.java)?:return@addSnapshotListener
            this@MyPageActivity.userInfo = userInfo
            if(userInfo.profilePicUrl.isNotEmpty()) profilePic.loadImage(userInfo.profilePicUrl)
            nameText.text = userInfo.nickName
        }?.let { addListenerRegistrations(it) }
    }

}
