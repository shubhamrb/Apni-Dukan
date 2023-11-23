package com.mamits.apnaonlines.userv.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.model.request.UserAuthRequestObj
import com.mamits.apnaonlines.userv.model.response.Data
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.utils.PreferenceUtils
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_bottom_otp.btn_close
import kotlinx.android.synthetic.main.layout_bottom_otp.edt_otp
import kotlinx.android.synthetic.main.layout_bottom_otp.view.edt_otp
import kotlinx.android.synthetic.main.layout_bottom_otp.view.text_edit_phone
import kotlinx.android.synthetic.main.layout_bottom_otp.view.text_mobile_text
import kotlinx.android.synthetic.main.layout_bottom_otp.view.text_resend
import kotlinx.android.synthetic.main.layout_bottom_otp.view.text_verify_otp

const val ARG_ITEM_COUNT = "item_count"

@AndroidEntryPoint
class OtpBottomsheetFragment : BottomSheetDialogFragment() {
    private var mListener: OtpListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mPreferenceUtils: PreferenceUtils? = null
    var mUserMobile = ""
    var mVendorCode = ""
    var username = ""
    var mFcmToken = ""
    var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    var mSignupdata: Data? = null
    var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_otp, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mPreferenceUtils = PreferenceUtils(activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        initProgressDialog()
        arguments?.let {
            if (it?.containsKey("name")) {
                username = it?.getString("name")!!
            }

            if (it?.containsKey("data")) {
                mSignupdata = it?.getParcelable("data")
            }

            mUserMobile = it?.getString(Constants.USER_MOBILE)!!
            mVendorCode = it?.getString(Constants.VENDOR_CODE)!!

            view?.text_mobile_text?.text = "Kindly check your SMS sent on ${mUserMobile}"
            startTimer()
        }
        view.run {
            btn_close?.setOnClickListener {
                mListener?.onFocusVendorField()
                dismiss()
            }
            text_resend?.setOnClickListener {
                if (text_resend.text.contains("resend", true)) {

                    if (mSignupdata != null) {
                        callResendOtp()
                    } else {
                        callLoginResendOtp()
                    }
                }
            }
            text_verify_otp.setOnClickListener {
                if (edt_otp.text.toString().trim()?.isNullOrEmpty() || edt_otp.text.toString()
                        .trim()?.length!! < 4
                ) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter valid OTP"
                    )
                } else if (mSignupdata != null && !edt_otp.text.toString().trim()
                        ?.equals(mSignupdata?.otp)
                ) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Invalid OTP"
                    )
                } else if (mSignupdata == null || edt_otp.text.toString().trim()
                        ?.equals(mSignupdata?.otp)
                ) {
                    if (mSignupdata != null) {
                        callVerifyOtp(mUserMobile, mVendorCode)
                    } else {
                        callVerifyLoginOtp(mUserMobile, mVendorCode)
                    }
                } else {

                }
            }

            text_edit_phone?.setOnClickListener {
                dismiss()
            }
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                CommonUtils.printLog(
                    "FETCHINGTOKEN",
                    "Fetching FCM registration token failed" + task.exception
                )
                return@OnCompleteListener
            }
            val token = task.result
            mFcmToken = token
            CommonUtils.printLog("FCM_TOKEN", "${token}")
            mPreferenceUtils?.setValue(Constants.FCM_TOKEN, mFcmToken)
        })

    }

    fun startTimer() {
        countDownTimer = MyCountDownTimer(60000, 1000)
        mView?.text_resend?.isClickable = false
        mView?.text_resend?.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        countDownTimer?.start()
    }

    inner class MyCountDownTimer(startTime: Long, interval: Long) :
        CountDownTimer(startTime, interval) {
        override fun onTick(millisUntilFinished: Long) {
            var sec = (millisUntilFinished / 1000).toString()
            if (millisUntilFinished / 1000 < 10 && millisUntilFinished / 1000 >= 0) {
                sec = "0$sec"
            }
            mView?.text_resend?.text = "00:$sec"
        }

        override fun onFinish() {
            try {
                mView?.text_resend?.text = Html.fromHtml("<u>Resend</u>")
                mView?.text_resend?.isClickable = true
                mView?.text_resend?.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
            } catch (e: java.lang.Exception) {
                CommonUtils.printLog("EXCEPTION_RESENT", "${e?.message}")
            }
        }
    }

    private fun callLoginResendOtp() {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = mUserMobile,
            device_type = Constants.DEVICE_TYPE,
            device_token = mFcmToken
        )
        if (mFcmToken?.isNullOrEmpty()) {
            progressDialogShow()
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    CommonUtils.printLog(
                        "FETCHINGTOKEN2",
                        "Fetching FCM registration token failed" + task.exception
                    )
                    return@OnCompleteListener
                }
                val token = task.result
                mFcmToken = token
                CommonUtils.printLog("FCM_TOKEN", "${token}")
                mPreferenceUtils?.setValue(Constants.FCM_TOKEN, mFcmToken)
                callLoginResendOtp()
            })
        } else
            if (CommonUtils.isOnline(activity!!)) {
                progressDialogShow()
                viewModel?.login(commonRequestObj)?.observe(this, Observer {
                    it?.run {
                        progressDialogDismiss()
                        if (status!!) {
                            startTimer()
                        }
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            message!!
                        )
                    }
                })
            } else {
                CommonUtils.createSnackBar(
                    activity?.findViewById(android.R.id.content)!!,
                    resources?.getString(R.string.no_net)!!
                )
            }
    }

    private fun callResendOtp() {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            name = username,
            mobile = mUserMobile,
            device_type = Constants.DEVICE_TYPE,
            device_token = mFcmToken
        )
        if (mFcmToken?.isNullOrEmpty()) {
            progressDialogShow()
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    CommonUtils.printLog(
                        "FETCHINGTOKEN2",
                        "Fetching FCM registration token failed" + task.exception
                    )
                    return@OnCompleteListener
                }
                val token = task.result
                mFcmToken = token
                CommonUtils.printLog("FCM_TOKEN", "${token}")
                mPreferenceUtils?.setValue(Constants.FCM_TOKEN, mFcmToken)
                callResendOtp()
            })
        } else
            if (CommonUtils.isOnline(activity!!)) {
                progressDialogShow()
                viewModel?.register(commonRequestObj)?.observe(this, Observer {
                    it?.run {
                        progressDialogDismiss()
                        if (status!!) {
                            mSignupdata = this?.data
                            startTimer()
                        }
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            message!!
                        )
                    }
                })
            } else {
                CommonUtils.createSnackBar(
                    activity?.findViewById(android.R.id.content)!!,
                    resources?.getString(R.string.no_net)!!
                )
            }
    }

    private fun callVerifyLoginOtp(mobile: String, code: String) {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = mobile,
            device_type = Constants.DEVICE_TYPE,
            device_token = mFcmToken,
            otp = edt_otp.text.toString().trim(),
            vendor_code = code
        )
        if (CommonUtils.isOnline(activity!!)) {
            progressDialogShow()
            viewModel.verifyLoginOtp(commonRequestObj).observe(viewLifecycleOwner, Observer {
                it?.run {
                    progressDialogDismiss()
                    if (status) {
                        progressDialogDismiss()
                        mListener?.onOtpverify(it)
                    } else {
                        progressDialogDismiss()
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            message!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    private fun callVerifyOtp(mobile: String, code: String) {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = mobile,
            name = username,
            device_type = Constants.DEVICE_TYPE,
            device_token = mFcmToken,
            otp = edt_otp.text.toString().trim(),
            vendor_code = code
        )
        if (CommonUtils.isOnline(activity!!)) {
            progressDialogShow()
            viewModel.verifyOtp(commonRequestObj).observe(viewLifecycleOwner, Observer {
                it?.run {
                    progressDialogDismiss()
                    if (status) {
                        progressDialogDismiss()
                        mListener?.onOtpverify(it)
                    } else {
                        progressDialogDismiss()
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            message!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    private fun initProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setMessage(getString(R.string.please_wait))
        }
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

    fun getApiKey(): String {
        return Constants.DUMMY_API_KEY
    }

    fun progressDialogDismiss() {
        if (progressDialog == null) {
            return
        }
        if (progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as OtpListener
        } else {
            mListener = context as OtpListener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface OtpListener {
        fun onOtpverify(obj: Any?)
        fun onResendOtp(obj: Any?)
        fun onFocusVendorField()
    }


    companion object {

        fun newInstance(
            mobile: String,
            data: Data,
            name: String,
            vendor_code: String
        ): OtpBottomsheetFragment =
            OtpBottomsheetFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.USER_MOBILE, mobile)
                    putString(Constants.VENDOR_CODE, vendor_code)
                    putString("name", name)
                    putParcelable("data", data)
                }
            }

        fun newInstance(
            mobile: String,
            code: String?
        ): OtpBottomsheetFragment =
            OtpBottomsheetFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.USER_MOBILE, mobile)
                    putString(Constants.VENDOR_CODE, code)
                }
            }

    }

}
