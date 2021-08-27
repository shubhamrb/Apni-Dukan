package com.mponline.userApp.ui.base


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mponline.userApp.R
import com.mponline.userApp.db.AppDatabase
import com.mponline.userApp.livedata.ConnectionLiveData
import com.mponline.userApp.model.request.CommonRequestObj
import com.mponline.userApp.model.request.HeaderInfo
import com.mponline.userApp.util.CameraGalleryUtils
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.PreferenceUtils
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.models.ConnectivityEvent
import com.zplesac.connectionbuddy.models.ConnectivityState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseFragment : Fragment() {

    var isConnected: Boolean = false
    var mPreferenceUtils: PreferenceUtils = PreferenceUtils()
    var mFcmPreferenceUtils: PreferenceUtils = PreferenceUtils()
    var progressDialog: ProgressDialog? = null
    var mConnectionLiveData = ConnectionLiveData()
    private var connectivityEvent: ConnectivityEvent = ConnectivityEvent()
    var database: AppDatabase? = null
    var mCustomerNo: String = ""
    var mMobileNo: String = ""
    var mCurrentPhotoPath: String = ""


    abstract fun onNetworkChange(isConnected: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        context?.run { database = AppDatabase.getAppDataBase(this) }

        initLiveConnection(savedInstanceState)
        initPreference()
        initUserData()
        initProgressDialog()
    }

    private fun initProgressDialog() {
        context?.run {
            if (progressDialog == null) {
                progressDialog = ProgressDialog(context)
                progressDialog!!.setMessage(getString(R.string.please_wait))
            }
        }
    }


    fun initLiveConnection(savedInstanceState: Bundle?) {
        isConnected = ConnectionBuddy.getInstance().hasNetworkConnection()
        mConnectionLiveData.run {
            init(savedInstanceState != null)
                .observe(this@BaseFragment, Observer { connection ->
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

    override fun onStart() {
        super.onStart()
        mConnectionLiveData.registerForNetworkUpdates()
    }

    override fun onStop() {
        super.onStop()
        mConnectionLiveData.unregisterFromNetworkUpdates()
    }

    fun progressDialogShow() {
        if (progressDialog == null) {
            return
        }

        if (!progressDialog!!.isShowing) {
            progressDialog!!.setCancelable(false)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.show()
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
        context?.run {
            mPreferenceUtils = PreferenceUtils.getInstance(this)
            mFcmPreferenceUtils = PreferenceUtils.getFCMInstance(this, "FCM")
        }
    }

    private fun initUserData() {
        mCustomerNo = mPreferenceUtils.getValue(Constants.CUSTOMERNO)
        mMobileNo = mPreferenceUtils.getValue(Constants.MOBILE_NO)
    }

    fun isCameraStoragePermissionGranted(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkCameraStoragePermissions(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            Constants.REQUEST_PERMISSIONS
        )
    }

    fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocationPermissions() {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.REQUEST_LOC_PERMISSIONS
        )
    }


    @SuppressLint("SimpleDateFormat")
    fun createImageFile(
        activity: Activity,
        extension: String = ".jpg",
        cameraUtils: CameraGalleryUtils
    ): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        var storageDir: File? = null
        if (extension?.contains(".jpg") || extension?.contains(".png")) {
            storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        } else {
            storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        }

        val image = File.createTempFile(
            imageFileName, /* prefix */
            if (extension?.contains(".jpg") || extension?.contains(".png")) ".jpg" else extension, /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        cameraUtils.mCurrentPhotoPath = image.absolutePath
        return image
    }

    //    @SerializedName("api_key")
//    val apiKey: String = "",
//    @SerializedName("latitude")
//    val latitude: String = "",
//    @SerializedName("longitude")
//    val longitude: String = "",
//    @SerializedName("category_id")
//    val category_id: String = "",
//    @SerializedName("subcategory_id")
//    val subcategory_id: String = "",
//    @SerializedName("product_id")
//    val product_id: String = ""

    fun getCommonRequestObj(
        apiKey: String="",
        mobile: String="",
        name: String="",
        email: String="",
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
        discountamount: String = "",
        finalamountpay: String = "",
        userid: String = "",
        product_id: String = ""
    ): CommonRequestObj {
        return CommonRequestObj(
            apiKey = apiKey,
            mobile = mobile,
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
            discountamount = discountamount,
            finalamountpay = finalamountpay,
            userid = userid,
            headerInfo = HeaderInfo(Authorization = "Bearer "+mPreferenceUtils?.getValue(Constants.USER_TOKEN))
        )
    }

    fun getApiKey():String{
        return Constants.DUMMY_API_KEY
    }

    fun translateViewToLeftAnim(view: View, duration:Long = 1200){
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        var width = displayMetrics.widthPixels.toFloat()
        CommonUtils.printLog("DEVICE_WIDTH", "${width}")
        var animation: TranslateAnimation = TranslateAnimation(width, 0f, 0f, 0f)
        animation.duration = duration
        animation.repeatCount = 0
        animation.repeatMode = 0
        view?.startAnimation(animation)
    }

}
