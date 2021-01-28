package com.mponline.userApp

import android.app.Application
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        configureLiveConnection()
    }

    private fun configureLiveConnection() {
        val networkInspectorConfiguration = ConnectionBuddyConfiguration.Builder(this).build()
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration)
    }
}
