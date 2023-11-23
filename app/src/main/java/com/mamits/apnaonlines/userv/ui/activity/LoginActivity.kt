package com.mamits.apnaonlines.userv.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.request.UserAuthRequestObj
import com.mamits.apnaonlines.userv.model.response.LoginResponse
import com.mamits.apnaonlines.userv.model.response.StorelistItem
import com.mamits.apnaonlines.userv.ui.base.BaseActivity
import com.mamits.apnaonlines.userv.ui.fragment.ForgotPwdBottomsheetFragment
import com.mamits.apnaonlines.userv.ui.fragment.OtpBottomsheetFragment
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.edit_mobile_no
import kotlinx.android.synthetic.main.activity_login.edit_vendor_code
import kotlinx.android.synthetic.main.activity_login.text_forgot_pwd
import kotlinx.android.synthetic.main.activity_login.text_login
import kotlinx.android.synthetic.main.activity_login.text_register
import kotlinx.android.synthetic.main.layout_progress.relative_progress

@AndroidEntryPoint
class LoginActivity : BaseActivity(), OnItemClickListener,
    ForgotPwdBottomsheetFragment.OtpListener, OtpBottomsheetFragment.OtpListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }

    var vendor_available: Boolean = true
    var vendor_code: String = ""
    val viewModel: UserListViewModel by viewModels()
    var mPin: String = ""
    var mFcmToken: String = "sdf sdfsdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!mPreferenceUtils.getValue(Constants.USER_MOBILE).isNullOrEmpty()) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else if (mPreferenceUtils.getBooleanValue(Constants.IS_FIRST_TIME)) {
            mPreferenceUtils.setValue(Constants.IS_FIRST_TIME, false)
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }
        setContentView(R.layout.activity_login)

        text_login.setOnClickListener {
            callVerifyVendorCodeApi()
        }
        text_forgot_pwd.setOnClickListener {
            showForgotPwdDialog()
        }
        text_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
        init()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            it?.let {
                mFcmToken = it
                CommonUtils.printLog("FCM_TOKEN", "${it}")
                mPreferenceUtils.setValue(Constants.FCM_TOKEN, mFcmToken)
            }
        }
    }

    fun showForgotPwdDialog() {
        val instance = ForgotPwdBottomsheetFragment.newInstance()
        instance.isCancelable = false
        instance.show(supportFragmentManager, "FORGOT_PWD")
    }

    fun init() {
        /*edt_1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_2.requestFocus()
                }
            }
        })
        edt_2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_3.requestFocus()
                } else {
                    edt_1.requestFocus()
                }
            }
        })
        edt_3.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_4.requestFocus()
                } else {
                    edt_2.requestFocus()
                }
            }
        })
        edt_4.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_5.requestFocus()
                } else {
                    edt_3.requestFocus()
                }
            }
        })
        edt_5.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    edt_6.requestFocus()
                } else {
                    edt_4.requestFocus()
                }
            }
        })
        edt_6.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    edt_5.requestFocus()
                }
            }
        })*/

    }

    private fun callVerifyVendorCodeApi() {
        if (vendor_available) {
            var commonRequestObj = UserAuthRequestObj(
                apiKey = getApiKey(),
                mobile = edit_mobile_no.text.toString().trim(),
                device_type = Constants.DEVICE_TYPE,
                device_token = mPreferenceUtils.getValue(Constants.FCM_TOKEN)
            )
            val messageId = viewModel.validateLogin(commonRequestObj, this@LoginActivity)

            if (messageId != resources?.getString(R.string.valid)) {
                messageId.let { it2 ->
                    CommonUtils.createSnackBar(findViewById(android.R.id.content), it2)
                }
            } else if (CommonUtils.isOnline(this)) {
                relative_progress.visibility = View.VISIBLE
                viewModel.verifyVendorCode(commonRequestObj).observe(this, Observer {
                    it?.run {
                        if (status) {
                            relative_progress.visibility = View.GONE

                            vendor_available = referred_by
                            if (referred_by) {
                                callLoginApi("")
                            } else {
                                edit_vendor_code.visibility = View.VISIBLE
                            }
                        } else {
                            relative_progress.visibility = View.GONE
                            CommonUtils.createSnackBar(
                                findViewById(android.R.id.content)!!,
                                message!!
                            )
                        }
                    }
                })
            } else {
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!,
                    resources?.getString(R.string.no_net)!!
                )
            }
        } else {
            //check if vendor code already available

            vendor_code = edit_vendor_code.text.toString()

            if (vendor_code == "") {
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!,
                    "Please enter the vendor code."
                )
                return
            }

            callLoginApi(vendor_code)
        }


    }

    private fun callLoginApi(vendor_code: String) {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = edit_mobile_no.text.toString().trim(),
            device_type = Constants.DEVICE_TYPE,
            device_token = mPreferenceUtils.getValue(Constants.FCM_TOKEN)
        )
        val messageId = viewModel.validateLogin(commonRequestObj, this@LoginActivity)

        if (messageId != resources?.getString(R.string.valid)) {
            messageId.let { it2 ->
                CommonUtils.createSnackBar(findViewById(android.R.id.content), it2)
            }
        } else if (CommonUtils.isOnline(this)) {
            relative_progress.visibility = View.VISIBLE
            viewModel.login(commonRequestObj).observe(this, Observer {
                it?.run {
                    if (status) {
                        relative_progress.visibility = View.GONE
                        showOtpDialog(vendor_code)
                    } else {
                        relative_progress.visibility = View.GONE
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            message!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    private fun showOtpDialog(code: String?) {
        val instance =
            OtpBottomsheetFragment.newInstance(edit_mobile_no.text.toString().trim(), code)
        instance.isCancelable = false
        instance.show(supportFragmentManager, "OTP")
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }

    override fun onOtpverify(obj: Any?) {
        val it: LoginResponse? = obj as? LoginResponse
        if (it?.status!!) {
            relative_progress.visibility = View.GONE
            mPreferenceUtils.setValue(Constants.USER_NAME, it.data?.user?.name!!)
            mPreferenceUtils.setValue(Constants.USER_MOBILE, it.data?.user?.phone!!)
            mPreferenceUtils.setValue(Constants.USER_EMAIL, it.data?.user?.email!!)
            mPreferenceUtils.setValue(Constants.USER_ID, it.data?.user?.id!!)
            mPreferenceUtils.setValue(Constants.USER_TOKEN, it.data?.token!!)
            mPreferenceUtils.setValue(Constants.VENDOR_CODE, it.data?.store!!.vendor_code!!)

            val inputString: StorelistItem = it.data.store;

            mPreferenceUtils.setValue(Constants.STORE_DATA, Gson().toJson(inputString))
            var intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
            relative_progress.visibility = View.GONE
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                it.message!!
            )
        }
    }

    override fun onResendOtp(obj: Any?) {

    }

    override fun onFocusVendorField() {
    }
}