package com.dac.gapp.andac

import android.app.Activity
import android.os.Bundle
import com.dac.gapp.andac.base.BaseActivity
import com.dac.gapp.andac.util.MyToast
import com.dac.gapp.andac.util.RemoteConfig
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*
import android.widget.Toast



class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkGooglePlayServices()
        RemoteConfig.init().addOnSuccessListener {
            TedPermission.with(this)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            checkMarketVersion(){
                                startActivity(MainActivity.createIntent(thisActivity()))
                                finish()
                            }
                        }

                        override fun onPermissionDenied(deniedPermissions: List<String>) {
                            MyToast.showShort(thisActivity(), "권한이 거절되어 앱이 종료됩니다\n권한 승낙후 앱을 다시 실행해주세요")
                            finish()
                        }
                    })
                    .setDeniedMessage("해당 권한이 없을 경우 앱을 사용할 수 없습니다!!")
                    .setPermissions(
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.CALL_PHONE
                    )
                    .check()
        }
    }

    fun thisActivity(): Activity {
        return this
    }
}