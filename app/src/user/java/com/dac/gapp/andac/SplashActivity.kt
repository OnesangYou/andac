package com.dac.gapp.andac

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import java.util.*



class SplashActivity : AppCompatActivity() {

    val permissions = Arrays.asList("android.permission.INTERNET", "android.permission.ACCESS_FINE_LOCATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isDonePermission = true
        for(it in permissions){
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                            it)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                it)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    // 토스트가 안띄어짐 ㅠ
                    Toast.makeText(this@SplashActivity, "권한이 거절되어 앱이 종료됩니다\n권한 승낙후 앱을 다시 실행해주세요", Toast.LENGTH_SHORT).show()
                    finish()
                    return

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            permissions.toTypedArray(),
                            999)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                    isDonePermission = false
                }
            }
        }

        if(isDonePermission){
            startActivity(MainActivity.createIntent(this))
            finish()
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            999 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startActivity(MainActivity.createIntent(this))
                    finish()
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // 토스트가 안띄어짐 ㅠ
                    Toast.makeText(this, "권한이 거절되어 앱이 종료됩니다\n권한 승낙후 앱을 다시 실행해주세요", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

}