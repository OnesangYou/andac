package com.dac.gapp.andac.util

import android.util.Log
import com.dac.gapp.andac.BuildConfig
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

object RemoteConfig {
    fun init() : Task<Void> {
        //  developer mode enable when debug
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()

        // set in-app defaults  값을 새로 추가할때마다 디폴트 값을 넣어줘야함
        val remoteConfigDefaults = HashMap<String, Any>()
        remoteConfigDefaults["latest_user_app_version"] = 1810070042
        remoteConfigDefaults["latest_hospital_app_version"] = 1810070042

        // FirebaseRemoteConfig init
        return FirebaseRemoteConfig.getInstance().run {
            setConfigSettings(configSettings)
            setDefaults(remoteConfigDefaults)
            // every 60 minutes refresh cache
            // default value is 12 hours
            fetch(60).addOnCompleteListener { task: Task<Void> ->
                if (task.isSuccessful) {
                    Log.d("RemoteConfig", "remote config is fetched.")
                    activateFetched()
                }
            }
        }
    }



}