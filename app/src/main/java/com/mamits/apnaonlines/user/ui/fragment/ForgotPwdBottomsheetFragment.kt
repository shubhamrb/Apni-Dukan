package com.mamits.apnaonlines.user.ui.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.model.request.UserAuthRequestObj
import com.mamits.apnaonlines.user.ui.activity.ChangePwdActivity
import com.mamits.apnaonlines.user.util.CommonUtils
import com.mamits.apnaonlines.user.util.Constants
import com.mamits.apnaonlines.user.utils.PreferenceUtils
import com.mamits.apnaonlines.user.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_bottom_forgotpwd.*
import kotlinx.android.synthetic.main.layout_bottom_forgotpwd.text_resend
import kotlinx.android.synthetic.main.layout_bottom_otp.view.*


@AndroidEntryPoint
class ForgotPwdBottomsheetFragment : BottomSheetDialogFragment() {
    private var mListener: OtpListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mPreferenceUtils: PreferenceUtils = PreferenceUtils()
    var mUserMobile = ""
    var progressDialog: ProgressDialog? = null
    var mView: View? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.layout_bottom_forgotpwd, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
//        arguments?.let {
//            mUserMobile = it?.getString(Constants.USER_MOBILE)!!
//        }
        view?.run {
            text_resend?.setOnClickListener {
                if (text_resend.text.contains("resend", true)) {
                    callSendOtp()
                }
            }
            text_submit_btn?.setOnClickListener {
                if (mUserMobile?.isNullOrEmpty()) {
                    if (edit_mobile_no?.text?.toString()?.trim()?.isNullOrEmpty()!!) {
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            "Please enter valid mobile number"
                        )
                    } else {
                        callSendOtp()
                    }
                } else {
                    if (edit_otp.text.toString().trim()?.isNullOrEmpty()) {
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            "Please enter valid OTP"
                        )
                    } else {
                        callForgotPwd()
                    }
                }
            }
            text_login_txt?.setOnClickListener {
                dismiss()
            }

        }
    }

    private fun callForgotPwd() {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = edit_mobile_no?.text?.toString()?.trim()!!,
            otp = edit_otp.text.toString().trim()
        )
        if (CommonUtils.isOnline(activity!!)) {
            progressDialogShow()
            viewModel?.forgotPwd(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status!!) {
                        progressDialogDismiss()
                        var intent = Intent(context, ChangePwdActivity::class.java)
                        intent.putExtra("mobile", edit_mobile_no?.text?.toString()?.trim())
                        context?.startActivity(intent)
                        dismiss()
                    } else {
                        progressDialogDismiss()
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

    private fun callSendOtp() {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = edit_mobile_no?.text?.toString()?.trim()!!
        )
        if (CommonUtils.isOnline(activity!!)) {
            progressDialogShow()
            viewModel?.sendOtp(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status!!) {
                        progressDialogDismiss()
                        startTimer()
                        mUserMobile = edit_mobile_no?.text?.toString()?.trim()!!
                        edit_mobile_no.visibility = View.GONE
                        ll_verify_otp_pwd.visibility = View.VISIBLE
                        text_subtext.text = "Kindly check your SMS send on ${
                            edit_mobile_no?.text?.toString()?.trim()!!
                        }"
                    } else {
                        progressDialogDismiss()
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

    fun startTimer() {
        countDownTimer = MyCountDownTimer(60000, 1000)
        text_resend?.isClickable = false
        text_resend?.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
        countDownTimer?.start()
    }

    inner class MyCountDownTimer(startTime: Long, interval: Long) :
        CountDownTimer(startTime, interval) {
        override fun onTick(millisUntilFinished: Long) {
            var sec = (millisUntilFinished / 1000).toString()
            if (millisUntilFinished / 1000 < 10 && millisUntilFinished / 1000 >= 0) {
                sec = "0$sec"
            }
            text_resend?.text = "00:$sec"
        }

        override fun onFinish() {
            try {
                text_resend?.text = Html.fromHtml("<u>Resend</u>")
                text_resend?.isClickable = true
                text_resend?.setTextColor(ContextCompat.getColor(activity!!, R.color.black))
            } catch (e: java.lang.Exception) {
                CommonUtils.printLog("EXCEPTION_RESENT", "${e?.message}")
            }
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
    }


    companion object {

        fun newInstance(): ForgotPwdBottomsheetFragment =
            ForgotPwdBottomsheetFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.USER_MOBILE, "")
                }
            }

    }

}
