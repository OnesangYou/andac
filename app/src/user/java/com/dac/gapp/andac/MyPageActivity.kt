package com.dac.gapp.andac

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.dac.gapp.andac.base.BaseMyPageActivity
import com.dac.gapp.andac.databinding.ActivityMyPageBinding
import com.dac.gapp.andac.extension.loadImage
import com.dac.gapp.andac.fragment.*
import com.dac.gapp.andac.model.firebase.UserInfo
import org.jetbrains.anko.alert

open class MyPageActivity : BaseMyPageActivity() {

    var userInfo: UserInfo? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        val binding = getBinding<ActivityMyPageBinding>()

        setActionBarLeftImage(R.drawable.back)
        setActionBarCenterText(R.string.mypage)
        setActionBarRightText(R.string.logout)
        setOnActionBarLeftClickListener(View.OnClickListener {
            finish()
        })
        setOnActionBarRightClickListener(View.OnClickListener { _ ->
            alert(title = "로그아웃", message = "정말 로그아웃 하시겠습니까?") {
                positiveButton("YES") { _ ->
                    getAuth()!!.signOut()
                    finish()
                    goToLogin()
                }
                negativeButton("NO") {}
            }.show()
        })

        // 버튼 그룹 리스너
        binding.myPageRadioGroup.setOnCheckedChangeListener { _, id ->
            when(id){
                R.id.accountSettingBtn -> changeFragment(AccountSettingFragment())
                R.id.favoritBtn -> changeFragment(FavoritesFragment())
                R.id.myBoardsBtn -> changeFragment(MyBoardsFragment())
                R.id.myCouponBtn -> changeFragment(MyCouponFragment())
                R.id.paymentHistoryBtn -> changeFragment(PaymentHistoryFragment())
            }
        }

        // 디폴트 화면
        binding.accountSettingBtn.performClick()

        // Set Profile
        getUser()?.addSnapshotListener { snapshot, _ ->
            snapshot?:return@addSnapshotListener
            val userInfo = snapshot.toObject(UserInfo::class.java)?:return@addSnapshotListener
            this@MyPageActivity.userInfo = userInfo
            if(userInfo.profilePicUrl.isNotEmpty()) binding.profilePic.loadImage(userInfo.profilePicUrl)
            binding.nameText.text = userInfo.nickName
            binding.emailText.text = "( ${userInfo.email} )"
        }?.let { addListenerRegistrations(it) }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
