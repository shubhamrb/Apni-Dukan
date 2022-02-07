package com.mamits.apnaonlines.user

import android.app.Application
import com.google.android.gms.security.ProviderInstaller
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.ConnectionBuddyConfiguration
import dagger.hilt.android.HiltAndroidApp
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext

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
