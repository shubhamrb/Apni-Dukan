package com.mamits.apnaonlines.userv

import android.app.Application
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        /*try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            var sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (e:Exception) {
            e.printStackTrace();
        }*/
        configureLiveConnection()
    }

    private fun configureLiveConnection() {
        val networkInspectorConfiguration = ConnectionBuddyConfiguration.Builder(this).build()
        ConnectionBuddy.getInstance().init(networkInspectorConfiguration)
    }
}
