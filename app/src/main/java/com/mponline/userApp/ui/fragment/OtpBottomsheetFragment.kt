package com.mponline.userApp.ui.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.mponline.userApp.R
import com.mponline.userApp.model.request.UserAuthRequestObj
import com.mponline.userApp.ui.activity.MainActivity
import com.mponline.userApp.ui.activity.RegisterActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.PreferenceUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_bottom_otp.*
import kotlinx.android.synthetic.main.layout_bottom_otp.edt_otp
import kotlinx.android.synthetic.main.layout_bottom_otp.text_edit_phone
import kotlinx.android.synthetic.main.layout_bottom_otp.text_verify_otp
import kotlinx.android.synthetic.main.layout_bottom_otp.view.*
import kotlinx.android.synthetic.main.layout_otp.*
import kotlinx.android.synthetic.main.layout_progress.*

const val ARG_ITEM_COUNT = "item_count"

@AndroidEntryPoint
class OtpBottomsheetFragment : BottomSheetDialogFragment() {
    private var mListener: OtpListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mPreferenceUtils: PreferenceUtils = PreferenceUtils()
    var mUserMobile = ""
    var progressDialog: ProgressDialog? = null
    private var countDownTimer: CountDownTimer? = null
    var mView:View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        initProgressDialog()
        arguments?.let {
            mUserMobile = it?.getString(Constants.USER_MOBILE)!!
            view?.text_mobile_text?.text = "Kindly check your SMS sent on ${mUserMobile}"
            startTimer()
        }
        view?.run {
            text_resend?.setOnClickListener {
                startTimer()
            }
            text_verify_otp.setOnClickListener {
                if(edt_otp.text.toString().trim()?.isNullOrEmpty() || edt_otp.text.toString().trim()?.length!! < 4){
                    CommonUtils.createSnackBar(activity?.findViewById(android.R.id.content)!!, "Please enter valid OTP")
                }else{
                    callVerifyOtp(mUserMobile)
                }
            }

            text_edit_phone?.setOnClickListener {
                dismiss()
            }
        }
    }

    fun startTimer() {
        countDownTimer = MyCountDownTimer(60000, 1000)
        mView?.text_resend?.isClickable = false
        mView?.text_resend?.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        countDownTimer?.start()
    }

    inner class MyCountDownTimer(startTime: Long, interval: Long) : CountDownTimer(startTime, interval) {
        override fun onTick(millisUntilFinished: Long) {
            var sec = (millisUntilFinished / 1000).toString()
            if (millisUntilFinished / 1000 < 10 && millisUntilFinished / 1000 >= 0) {
                sec = "0$sec"
            }
            mView?.text_resend?.text = "00:$sec"
        }

        override fun onFinish() {
            mView?.text_resend?.text = Html.fromHtml(activity?.resources?.getString(R.string.resend))
            mView?.text_resend?.isClickable = true
            mView?.text_resend?.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
        }
    }

    private fun callVerifyOtp(mobile: String) {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = mobile,
            otp = edt_otp.text.toString().trim()
        )
        if (CommonUtils.isOnline(activity!!)) {
            progressDialogShow()
            viewModel?.verifyOtp(commonRequestObj)?.observe(this, Observer {
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

    fun getApiKey():String{
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
        fun onOtpverify(obj:Any?)
        fun onResendOtp(obj:Any?)
    }


    companion object {

        fun newInstance(mobile:String): OtpBottomsheetFragment =
            OtpBottomsheetFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.USER_MOBILE, mobile)
                    }
                }

    }

}
