package com.mponline.userApp.ui.base


import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mponline.userApp.R
import com.mponline.userApp.db.AppDatabase
import com.mponline.userApp.livedata.ConnectionLiveData
import com.recyclemybin.utils.Constants
import com.recyclemybin.utils.PreferenceUtils
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.models.ConnectivityEvent
import com.zplesac.connectionbuddy.models.ConnectivityState

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



}
