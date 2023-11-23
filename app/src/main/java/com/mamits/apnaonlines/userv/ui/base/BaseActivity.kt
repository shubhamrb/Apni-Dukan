package com.mamits.apnaonlines.userv.ui.base

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.BuildConfig
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.db.AppDatabase
import com.mamits.apnaonlines.userv.livedata.ConnectionLiveData
import com.mamits.apnaonlines.userv.model.UserCurrentAddress
import com.mamits.apnaonlines.userv.model.request.CommonRequestObj
import com.mamits.apnaonlines.userv.model.request.HeaderInfo
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.utils.PreferenceUtils
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.models.ConnectivityEvent
import com.zplesac.connectionbuddy.models.ConnectivityState
import java.util.*
import kotlin.collections.HashMap


open class BaseActivity : AppCompatActivity() {

    var isConnected: Boolean = false
    private var connectivityEvent: ConnectivityEvent = ConnectivityEvent()
    var mPreferenceUtils: PreferenceUtils = PreferenceUtils()
    var mFcmPreferenceUtils: PreferenceUtils = PreferenceUtils()
    var mConnectionLiveData = ConnectionLiveData()
    var progressDialog: ProgressDialog? = null
    var database: AppDatabase? = null
    lateinit var mUserId: String
    var mMobileNo: String = ""
    var mEmailId: String = ""
    var mQualification: String = ""
    var mDeeplinkSuffixUrl: String = ""


    open fun onNetworkChange(isConnected: Boolean) {}

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
//        if(BuildConfig.DEBUG) {
//            CommonUtils.printLog("DebugDB", DebugDB.getAddressLog())
//        }
        initPreference()
        initUserData()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        database = AppDatabase.getAppDataBase(this)
        initLiveConnection(savedInstanceState)
        initProgressDialog()

    }

    private fun initUserData() {
        mUserId = mPreferenceUtils.getValue(Constants.USER_ID)
        mMobileNo = mPreferenceUtils.getValue(Constants.MOBILE_NO)
        mEmailId = mPreferenceUtils.getValue(Constants.EMAIL_ID)
        mQualification = mPreferenceUtils.getValue(Constants.QUALIFICATION)
        mDeeplinkSuffixUrl = mPreferenceUtils.getValue(Constants.DEEPLINK_URL_SUFFIX)
    }

    private fun initProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setMessage(getString(R.string.please_wait))
        }
    }

    override fun onStart() {
        super.onStart()
        mConnectionLiveData.registerForNetworkUpdates()
    }

    override fun onStop() {
        super.onStop()
        mConnectionLiveData.unregisterFromNetworkUpdates()
    }

    fun progressDialogShow() {
        try {
            if (progressDialog == null) {
                return
            }

            if (!progressDialog!!.isShowing) {
                progressDialog!!.setCancelable(false)
                progressDialog!!.setCanceledOnTouchOutside(false)
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            e?.printStackTrace()
        }
    }

    fun progressDialogCancelableShow() {
        if (progressDialog == null) {
            return
        }

        if (!progressDialog!!.isShowing) {
            progressDialog!!.setCancelable(true)
            progressDialog!!.setCanceledOnTouchOutside(true)
            progressDialog!!.show()
        }
    }

    fun progressDialogDismiss() {
        if (progressDialog == null) {
            return
        }
        if (progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    private fun initPreference() {
        mPreferenceUtils = PreferenceUtils.getInstance(this)
        mFcmPreferenceUtils = PreferenceUtils.getFCMInstance(this, "FCM")
    }

    fun initLiveConnection(savedInstanceState: Bundle?) {
        isConnected = ConnectionBuddy.getInstance().hasNetworkConnection()
        mConnectionLiveData.run {
            init(savedInstanceState != null)
                .observe(this@BaseActivity, Observer { connection ->
                    // Checks if the connection variable is not null then only execute the block.
                    connection?.let {

                        connectivityEvent = connection
                        isConnected = connection.state.value == ConnectivityState.CONNECTED
                        // Convey the network change event to current Activity.
                        onNetworkChange(isConnected)
                    }
                })
        }
    }


    fun setUpRecyclerView(recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        recyclerView.setItemViewCacheSize(20)
        recyclerView.isDrawingCacheEnabled = true
        recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }


    fun animateViewVisibility(view: View, visible: Boolean, duration: Long = 300) {
        if (visible) {
            view.animate()
                .alpha(1.0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.VISIBLE
                    }
                })
            view.visibility = View.VISIBLE
        } else {
            view.animate()
                .alpha(0.0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.GONE
                    }
                })
            view.visibility = View.GONE
        }
    }

    fun isCameraStoragePermissionGranted(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                this,
                READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            return ContextCompat.checkSelfPermission(
                this,
                WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
        }

    }

    fun isAccountPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.GET_ACCOUNTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isReadSmsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkCameraStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_MEDIA_IMAGES, CAMERA),
                Constants.REQUEST_PERMISSIONS
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA),
                Constants.REQUEST_PERMISSIONS
            )
        }

    }

    fun checkStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE),
            Constants.REQUEST_PERMISSIONS
        )
    }

    fun checkReadSmsPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            Constants.REQUEST_PERMISSIONS
        )
    }

    fun checkSmsCameraStoragePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECEIVE_SMS,
                WRITE_EXTERNAL_STORAGE,
                CAMERA
            ),
            Constants.REQUEST_PERMISSIONS
        )
    }

    fun checkLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.REQUEST_LOC_PERMISSIONS
        )
    }

    fun checkCameraAudioPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(CAMERA, Manifest.permission.RECORD_AUDIO),
            Constants.REQUEST_PERMISSIONS
        )
    }

    companion object {
        open fun doLogout(mPreferenceUtils: PreferenceUtils, context: Context) {
            Thread(Runnable {
                mPreferenceUtils.clear()
//                val intent = Intent(context, LoginActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//                context.startActivity(intent)
            }).start()
        }
    }

    fun getDeviceInfo(): String {
        val map: HashMap<String, String> = HashMap<String, String>()
        map["SERIAL"] = Build.SERIAL
        map["MODEL"] = Build.MODEL
        map["ID"] = Build.ID
        map["RELEASE"] = Build.ID
        map["ANDROIDID"] = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        return map.toString()
    }

    fun getHeadermap(): String {
        if (mFcmPreferenceUtils.getValue(Constants.FCM_TOKEN) == "") {
            generateToken()
        }
        val map: HashMap<String, String> = HashMap<String, String>()
        map["AUTH-TOKEN"] = mPreferenceUtils.getValue(Constants.USER_TOKEN)
        map["userId"] = mPreferenceUtils.getValue(Constants.USER_ID)
        map["androidId"] = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        map["deviceToken"] = mFcmPreferenceUtils.getValue(Constants.FCM_TOKEN)
        map["appVersion"] = BuildConfig.VERSION_NAME + "|" + BuildConfig.VERSION_CODE
        map["deviceDetails"] = Build.SERIAL +
                "|" + Build.MODEL +
                "|" + Build.ID +
                "|" + Build.MANUFACTURER +
                "|" + Build.BRAND +
                "|" + Build.VERSION.RELEASE
        return map.toString()
    }

    fun getHeaderHashmap(): HashMap<String, String> {
        if (mFcmPreferenceUtils.getValue(Constants.FCM_TOKEN) == "") {
            generateToken()
        }
        val map: HashMap<String, String> = HashMap<String, String>()
        map["AUTH-TOKEN"] = mPreferenceUtils.getValue(Constants.USER_TOKEN)
        map["userId"] = mPreferenceUtils.getValue(Constants.USER_ID)
        map["androidId"] = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        map["deviceToken"] = mFcmPreferenceUtils.getValue(Constants.FCM_TOKEN)
        map["appVersion"] = BuildConfig.VERSION_NAME + "|" + BuildConfig.VERSION_CODE
        map["deviceDetails"] = Build.SERIAL +
                "|" + Build.MODEL +
                "|" + Build.ID +
                "|" + Build.MANUFACTURER +
                "|" + Build.BRAND +
                "|" + Build.VERSION.RELEASE
        return map
    }

    fun generateToken() {
//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    return@OnCompleteListener
//                }
//                // Get new Instance ID token
//                val token = task.result?.token
//                token?.run {
//                    CommonUtils.printLog("device token", "Refreshed token: $this")
//                    mFcmPreferenceUtils.setValue(Constants.FCM_TOKEN, this)
//                }
//
//            })
    }


    fun getAddressFromLatlong(latitude: Double, longitude: Double): UserCurrentAddress? {
        var userCurrentAddress: UserCurrentAddress? = null
        if (latitude != null && longitude != null) {
            CommonUtils.printLog("FetchedLocation", "Lat ${latitude} and  longitude ${longitude}")
            val geocoder: Geocoder
            var addresses: List<Address> = java.util.ArrayList()
            var aLocale = Locale.Builder().setLanguage("en").setLocale(Locale.US).build();
            geocoder = Geocoder(this@BaseActivity, aLocale)
            geocoder?.let {
                try {
                    addresses = geocoder?.getFromLocation(
                        latitude,
                        longitude,
                        5
                    )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    CommonUtils.printLog("FetchedLocationAddress", "${addresses?.toString()}")

                    addresses?.run {
                        if (this != null && this?.size!! > 0) {

                            for (i in 0..this?.size - 1) {
                                if (this.get(i)?.locality != null) {
                                    if (CommonUtils.isValidGPSName(this.get(i)?.locality) && (!TextUtils.isEmpty(
                                            this.get(i)?.adminArea
                                        ) && (CommonUtils.isValidGPSName(this.get(i)?.adminArea)))
                                        && (!TextUtils.isEmpty(this.get(i)?.countryName) && CommonUtils.isValidGPSName(
                                            this.get(i)?.countryName
                                        )) && ((!TextUtils.isEmpty(this.get(i)?.countryName) && CommonUtils.isValidGPSName(
                                            this.get(i)?.featureName
                                        ))) && ((!TextUtils.isEmpty(
                                            this.get(i)?.getAddressLine(0)
                                        ) && CommonUtils.isValidGPSName(
                                            this.get(i)?.getAddressLine(0)
                                        )))
                                    ) {
                                        val address = this.get(i)
                                            ?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                        val city = this.get(i)?.locality
                                        val state = this.get(i)?.adminArea
                                        val country = this.get(i)?.countryName
                                        val postalCode =
                                            if (this.get(i)?.postalCode != null) this?.get(i)?.postalCode else ""
                                        val knownName = this.get(i)?.featureName
                                        userCurrentAddress =
                                            UserCurrentAddress(
                                                address,
                                                city,
                                                state,
                                                country,
                                                postalCode,
                                                knownName
                                            )
                                        return userCurrentAddress
                                    }
                                }
                            }


                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return userCurrentAddress//UserCurrentAddress("", "", "", "", "", "")
                }
            }
            return userCurrentAddress
        }
        return userCurrentAddress//UserCurrentAddress("", "", "", "", "", "")
    }


//    fun showConfirmationPopup(
//        context: Activity,
//        title: String,
//        msg: String,
//        btnText: String,
//        type: String
//    ) {
//        val dialog = Dialog(context, R.style.Theme_Dialog)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        val view = context.layoutInflater.inflate(R.layout.item_confirmation_dialog, null, false)
//
//        view.text_conf_title.text = title
//        view.text_conf_msg.text = msg
//        view.text_conf_submit.text = btnText
//
//        view.image_conf_close.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        view.text_conf_submit.setOnClickListener {
//            onConfirmed(true, type)
//            dialog.dismiss()
//        }
//        dialog.setContentView(view)
//        dialog.setCancelable(false)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.show()
//    }


    open fun onConfirmed(flag: Boolean = false, type: String) {

    }


    fun showDatepicker(edt_date: EditText) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this@BaseActivity,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var mSelectedDate = "${year}-${monthOfYear + 1}-${dayOfMonth}"
                edt_date.setText(mSelectedDate)
            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = Date().time
        dpd.datePicker.maxDate = (c.getTimeInMillis()) + ((1000 * 60 * 60 * 24) * 20)
        dpd.show()
    }

    fun getCommonRequestObj(
        apiKey: String = "",
        mobile: String = "",
        password: String = "",
        name: String = "",
        email: String = "",
        orderid: String = "",
        coupon: String = "",
        orderamount: String = "",
        vendorid: String = "",
        latitude: String = "",
        longitude: String = "",
        category_id: String = "",
        category: String = "",
        store_id: String = "",
        storeid: String = "",
        search: String = "",
        subcategory_id: String = "",
        rating: String = "",
        oldpassword: String = "",
        newpassword: String = "",
        product_id: String = "",
        start: String = "",
        pagelength: String = ""
    ): CommonRequestObj {
        return CommonRequestObj(
            apiKey = apiKey,
            mobile = mobile,
            password = password,
            name = name,
            email = email,
            search = search,
            orderid = orderid,
            coupon = coupon,
            orderamount = orderamount,
            vendorid = vendorid,
            latitude = latitude,
            longitude = longitude,
            store_id = store_id,
            storeid = storeid,
            category_id = category_id,
            category = category,
            subcategory_id = subcategory_id,
            rating = rating,
            product_id = product_id,
            oldpassword = oldpassword,
            newpassword = newpassword,
            headerInfo = HeaderInfo(Authorization = "Bearer " + mPreferenceUtils?.getValue(Constants.USER_TOKEN)),
            start = start,
            pagelength = pagelength
        )
    }

    fun getApiKey(): String {
        return Constants.DUMMY_API_KEY
    }


}


















