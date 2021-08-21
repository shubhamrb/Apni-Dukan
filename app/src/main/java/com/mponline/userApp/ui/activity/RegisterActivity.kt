package com.mponline.userApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.request.UserAuthRequestObj
import com.mponline.userApp.model.response.*
import com.mponline.userApp.ui.adapter.NotificationAdapter
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.ui.fragment.*
import com.mponline.userApp.util.CameraGalleryFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bottom_navigation
import kotlinx.android.synthetic.main.activity_register.edit_mobile_no
import kotlinx.android.synthetic.main.activity_register.text_register
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_instruction.view.*
import kotlinx.android.synthetic.main.layout_otp.*
import kotlinx.android.synthetic.main.layout_progress.*

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
        init()
        FirebaseMessaging.getInstance().token?.addOnSuccessListener {
            it?.let {
                mFcmToken = it
                CommonUtils.printLog("FCM_TOKEN", "${it}")
                mPreferenceUtils?.setValue(Constants.FCM_TOKEN, mFcmToken)
            }
        }

    }

    fun init() {
//        CommonUtils.openKeyboard(this)
        edt_1.addTextChangedListener(object : TextWatcher {
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
        })

    }

    private fun callRegisterApi(isResendOtp:Boolean = false) {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            name = edt_name.text.toString().trim(),
            mobile = edit_mobile_no.text.toString().trim(),
            pin = edt_1.text.toString().trim() + edt_2.text.toString()
                .trim() + edt_3.text.toString().trim() + edt_4.text.toString()
                .trim() + edt_5.text.toString().trim() + edt_6.text.toString().trim(),
            device_type = Constants.DEVICE_TYPE,
            device_token = mPreferenceUtils?.getValue(Constants.FCM_TOKEN)
        )
        val messageId = viewModel?.validateRegister(commonRequestObj, this@RegisterActivity)

        if (messageId != resources?.getString(R.string.valid)) {
            messageId.let { it2 ->
                CommonUtils.createSnackBar(findViewById(android.R.id.content), it2)
            }
        } else if (CommonUtils.isOnline(this)) {
            if(!isResendOtp) {
                relative_progress.visibility = View.VISIBLE
            }else{
                progressDialogShow()
            }
            viewModel?.register(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status!!) {
                        if(!isResendOtp) {
                            showOtpDialog(it?.data)
                            relative_progress.visibility = View.GONE
                        }else{
                            progressDialogDismiss()
                        }
                    } else {
                        relative_progress.visibility = View.GONE
                    }
                    CommonUtils.createSnackBar(
                        findViewById(android.R.id.content)!!,
                        message!!
                    )
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }


    override fun onClick(pos: Int, view: View, obj: Any?) {

    }

    fun showOtpDialog(mSignupResponse: Data) {
        val instance = OtpBottomsheetFragment.newInstance(edit_mobile_no.text.toString().trim(), mSignupResponse,
            edt_1.text.toString().trim() + edt_2.text.toString()
                .trim() + edt_3.text.toString().trim() + edt_4.text.toString()
                .trim() + edt_5.text.toString().trim() + edt_6.text.toString().trim(), edt_name.text.toString().trim())
        instance.isCancelable = false
        instance.show(supportFragmentManager, "OTP")
    }

    override fun onOtpverify(obj: Any?) {
        if(obj!=null && obj is LoginResponse){
            if(obj.status!!){
                obj?.let {
                    mPreferenceUtils?.setValue(Constants.USER_NAME, obj?.data?.user?.name!!)
                    mPreferenceUtils?.setValue(Constants.USER_MOBILE, obj?.data?.user?.phone!!)
                    mPreferenceUtils?.setValue(Constants.USER_ID, obj?.data?.user?.id!!)
                    mPreferenceUtils?.setValue(Constants.USER_TOKEN, obj?.data?.token!!)
                    mPreferenceUtils?.setValue(Constants.USER_INFO, Gson().toJson(obj))
                    CommonUtils.createSnackBar(
                        findViewById(android.R.id.content)!!,
                        obj?.message!!
                    )
                    var intent:Intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }else{
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!,
                    obj?.message!!
                )
            }
        }
    }

    override fun onResendOtp(obj: Any?) {
        callRegisterApi(true)
    }

}