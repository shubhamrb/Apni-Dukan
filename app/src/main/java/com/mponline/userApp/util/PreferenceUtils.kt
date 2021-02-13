package com.mponline.userApp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PreferenceUtils {

    private val mContext: Context? = null
    private var mSettings: SharedPreferences? = null
    private var mEditor: Editor? = null

    companion object {

        const val APP_PREF = "BIDS"
        const val APP_PREF_FCM = "FCM_BIDS"
        @SuppressLint("StaticFieldLeak")
        private var sInstance: PreferenceUtils? = null
        @SuppressLint("StaticFieldLeak")
        private var sFCMInstance: PreferenceUtils? = null

        @JvmStatic
        fun getInstance(context: Context): PreferenceUtils {
            if (sInstance == null) {
                sInstance = PreferenceUtils(context)
            }
            return sInstance as PreferenceUtils
        }

        fun getFCMInstance(context: Context, fcm: String): PreferenceUtils {
            if (sFCMInstance == null) {
                sFCMInstance = PreferenceUtils(context, fcm)
            }
            return sFCMInstance as PreferenceUtils
        }
    }

    constructor()

    @SuppressLint("CommitPrefEdits")
    constructor(mContext: Context?) {
        mSettings = mContext?.getSharedPreferences(APP_PREF, MODE_PRIVATE)
        mEditor = mSettings?.edit()
    }

    @SuppressLint("CommitPrefEdits")
    constructor(mContext: Context?, fcm: String) {
        mSettings = mContext?.getSharedPreferences(APP_PREF_FCM, MODE_PRIVATE)
        mEditor = mSettings?.edit()
    }


    /***
     * Set a value for the key
     */
    fun setValue(key: String, value: String) {
        mEditor!!.putString(key, value)
        mEditor!!.apply()
    }

    /***
     * Set a value for the key
     */
    fun setValue(key: String, value: Int) {
        mEditor!!.putInt(key, value)
        mEditor!!.apply()
    }

    /***
     * Set a value for the key
     */
    fun setValue(key: String, value: Double) {
        setValue(key, java.lang.Double.toString(value))
    }

    /***
     * Set a value for the key
     */
    fun setValue(key: String, value: Long) {
        mEditor!!.putLong(key, value)
        mEditor!!.apply()
    }

    /****
     * Gets the value from the settings stored natively on the device.
     */
    fun getValue(key: String): String {
        return mSettings?.getString(key, "")!!
    }

    fun getIntValue(key: String, defaultValue: Int): Int {
        return mSettings!!.getInt(key, defaultValue)
    }

    fun getLongValue(key: String, defaultValue: Long): Long {
        return mSettings!!.getLong(key, defaultValue)
    }

    /****
     * Gets the value from the preferences stored natively on the device.
     *
     * @param defValue Default value for the key, if one is not found.
     */
    fun getValue(key: String, defValue: Boolean): Boolean {
        return mSettings!!.getBoolean(key, defValue)
    }

    fun setValue(key: String, value: Boolean) {
        mEditor!!.putBoolean(key, value)
        mEditor!!.apply()
    }

    /****
     * Clear all the preferences store in this [Editor]
     */
    fun clear() {
        mEditor!!.clear().apply()
    }

    /**
     * Removes preference entry for the given key.
     */
    fun removeValue(key: String) {
        if (mEditor != null) {
            mEditor!!.remove(key).apply()
        }
    }
}
