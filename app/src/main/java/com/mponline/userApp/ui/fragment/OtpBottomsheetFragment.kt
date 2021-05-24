package com.mponline.userApp.ui.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
        arguments?.let {
            mUserMobile = it?.getString(Constants.USER_MOBILE)!!
        }
        view?.run {
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

    private fun callVerifyOtp(mobile: String) {
        var commonRequestObj = UserAuthRequestObj(
            apiKey = getApiKey(),
            mobile = mobile,
            otp = edt_otp.text.toString().trim()
        )
        if (CommonUtils.isOnline(activity!!)) {
            progressDialogShow()
            viewModel?.verifyOtp(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    if (status!!) {
                        progressDialogDismiss()
//                        mPreferenceUtils?.setValue(Constants.USER_NAME, data?.user?.name!!)
//                        mPreferenceUtils?.setValue(Constants.USER_MOBILE, data?.user?.phone!!)
//                        mPreferenceUtils?.setValue(Constants.USER_ID, data?.user?.id!!)
//                        mPreferenceUtils?.setValue(Constants.USER_TOKEN, data?.token!!)
//                        mPreferenceUtils?.setValue(Constants.USER_INFO, Gson().toJson(this))
                        startActivity(Intent(activity, MainActivity::class.java))
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
