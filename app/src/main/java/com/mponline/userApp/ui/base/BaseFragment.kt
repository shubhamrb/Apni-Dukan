package com.mponline.userApp.ui.base


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.R
import com.mponline.userApp.db.AppDatabase
import com.mponline.userApp.livedata.ConnectionLiveData
import com.mponline.userApp.util.CameraGalleryUtils
import com.recyclemybin.utils.PreferenceUtils
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
    var mCurrentPhotoPath:String = ""


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

    @SuppressLint("SimpleDateFormat")
    fun createImageFile(activity: Activity, extension: String = ".jpg", cameraUtils: CameraGalleryUtils): File {
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





}
