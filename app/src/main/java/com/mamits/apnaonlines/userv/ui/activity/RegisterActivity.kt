package com.mamits.apnaonlines.userv.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.request.UserAuthRequestObj
import com.mamits.apnaonlines.userv.model.response.Data
import com.mamits.apnaonlines.userv.model.response.LoginResponse
import com.mamits.apnaonlines.userv.model.response.StorelistItem
import com.mamits.apnaonlines.userv.ui.base.BaseActivity
import com.mamits.apnaonlines.userv.ui.fragment.OtpBottomsheetFragment
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_register.edit_mobile_no
import kotlinx.android.synthetic.main.activity_register.edt_name
import kotlinx.android.synthetic.main.activity_register.edt_vendor_code
import kotlinx.android.synthetic.main.activity_register.text_makelogin
import kotlinx.android.synthetic.main.activity_register.text_register
import kotlinx.android.synthetic.main.layout_progress.relative_progress

@AndroidEntryPoint
class RegisterActivity : BaseActivity(), OnItemClickListener, OtpBottomsheetFragment.OtpListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mFcmToken = "sdf dfs"
    val viewModel: UserListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        text_register.setOnClickListener {
            callRegisterApi()
        }
        text_makelogin.setOnClickListener {
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        FirebaseMessaging.getInstance().token?.addOnSuccessListener {
            it?.let {
                mFcmToken = it
                CommonUtils.printLog("FCM_TOKEN", "${it}")
                mPreferenceUtils?.setValue(Constants.FCM_TOKEN, mFcmToken)
            }
        }

    }

    private fun callRegisterApi(isResendOtp: Boolean = false) {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            name = edt_name.text.toString().trim(),
            mobile = edit_mobile_no.text.toString().trim(),
            device_type = Constants.DEVICE_TYPE,
            device_token = mPreferenceUtils?.getValue(Constants.FCM_TOKEN),
            vendor_code = edt_vendor_code.text.toString().trim(),
        )
        val messageId = viewModel.validateRegister(commonRequestObj, this@RegisterActivity)

        if (messageId != resources?.getString(R.string.valid)) {
            messageId.let { it2 ->
                CommonUtils.createSnackBar(findViewById(android.R.id.content), it2)
            }
        } else if (CommonUtils.isOnline(this)) {
            if (!isResendOtp) {
                relative_progress.visibility = View.VISIBLE
            } else {
                progressDialogShow()
            }
            viewModel.register(commonRequestObj).observe(this, Observer {
                it?.run {
                    if (status!!) {
                        if (!isResendOtp) {
                            showOtpDialog(it?.data)
                            relative_progress.visibility = View.GONE
                        } else {
                            progressDialogDismiss()
                        }
                    } else {
                        relative_progress.visibility = View.GONE
                    }
                    CommonUtils.createSnackBar(
                        findViewById(android.R.id.content)!!, message!!

                    )
                    if (message == "This phone number is already registered.") {
                        var intent: Intent =
                            Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!, resources?.getString(R.string.no_net)!!
            )
        }
    }


    override fun onClick(pos: Int, view: View, obj: Any?) {

    }

    fun showOtpDialog(mSignupResponse: Data) {
        val instance = OtpBottomsheetFragment.newInstance(
            edit_mobile_no.text.toString().trim(),
            mSignupResponse,
            edt_name.text.toString().trim(),
            edt_vendor_code.text.toString().trim()
        )
        instance.isCancelable = false
        instance.show(supportFragmentManager, "OTP")
    }

    override fun onOtpverify(obj: Any?) {
        if (obj != null && obj is LoginResponse) {
            if (obj.status!!) {
                obj.let {
                    mPreferenceUtils?.setValue(Constants.USER_NAME, obj?.data?.user?.name!!)
                    mPreferenceUtils?.setValue(Constants.USER_MOBILE, obj?.data?.user?.phone!!)
                    mPreferenceUtils?.setValue(Constants.USER_EMAIL, obj?.data?.user?.email!!)
                    mPreferenceUtils?.setValue(Constants.USER_ID, obj?.data?.user?.id!!)
                    mPreferenceUtils?.setValue(Constants.USER_TOKEN, obj?.data?.token!!)
                    mPreferenceUtils?.setValue(Constants.USER_INFO, Gson().toJson(obj))
                    mPreferenceUtils.setValue(Constants.VENDOR_CODE, it.data?.store!!.vendor_code!!)
                    val inputString: StorelistItem = it.data.store;
                    mPreferenceUtils.setValue(Constants.STORE_DATA, Gson().toJson(inputString))

                    CommonUtils.createSnackBar(
                        findViewById(android.R.id.content)!!, obj?.message!!
                    )
                    var intent: Intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            } else {
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!, obj?.message!!
                )
            }
        }
    }

    override fun onResendOtp(obj: Any?) {
        callRegisterApi(true)
    }

    override fun onFocusVendorField() {
        try {
            edt_vendor_code.requestFocus()
            val handler = Handler(Looper.myLooper()!!)
            handler.postDelayed({
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edt_vendor_code, InputMethodManager.SHOW_IMPLICIT)
            }, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}