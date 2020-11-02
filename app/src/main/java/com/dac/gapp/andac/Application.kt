package com.dac.gapp.andac

import androidx.multidex.MultiDexApplication
import timber.log.Timber


class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}