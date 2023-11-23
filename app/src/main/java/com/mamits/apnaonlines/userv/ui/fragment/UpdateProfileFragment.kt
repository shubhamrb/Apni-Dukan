package com.mamits.apnaonlines.userv.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.model.LocationUtils
import com.mamits.apnaonlines.userv.ui.activity.LoginActivity
import com.mamits.apnaonlines.userv.ui.base.BaseFragment
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.common_toolbar_normal.view.image_back
import kotlinx.android.synthetic.main.common_toolbar_normal.view.toolbar_title
import kotlinx.android.synthetic.main.fragment_edit_profile.edit_vendor_code
import kotlinx.android.synthetic.main.fragment_edit_profile.edt_email
import kotlinx.android.synthetic.main.fragment_edit_profile.edt_mobile_no
import kotlinx.android.synthetic.main.fragment_edit_profile.edt_username
import kotlinx.android.synthetic.main.fragment_edit_profile.ll_container
import kotlinx.android.synthetic.main.fragment_edit_profile.view.text_list_title
import kotlinx.android.synthetic.main.fragment_edit_profile.view.text_update_profile
import kotlinx.android.synthetic.main.layout_empty.relative_empty
import kotlinx.android.synthetic.main.layout_progress.relative_progress

@AndroidEntryPoint
class UpdateProfileFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    private var isVendorUpdate: Boolean = false
    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_edit_profile, container, false)

        return mView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context != null) {
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.toolbar_title?.text = "Update Profile"
        arguments?.let {
            if (arguments?.containsKey("update_vendor")!!) {
                isVendorUpdate = true
                view?.text_list_title.text = "Update Vendor"
                view?.text_update_profile.text = "Update Vendor"
                view?.toolbar_title?.text = "Update Vendor"

                edt_username?.isEnabled = false
                edt_email?.isEnabled = false
                edt_mobile_no?.isEnabled = false
                onFocusVendorField()
            }
        }

        view?.image_back?.setOnClickListener { }
        view?.image_back?.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        view.run {
            edt_username?.setText(mPreferenceUtils?.getValue(Constants.USER_NAME))
            edt_email?.setText(mPreferenceUtils?.getValue(Constants.USER_EMAIL))
            edt_mobile_no?.setText(mPreferenceUtils?.getValue(Constants.USER_MOBILE))
            edit_vendor_code?.setText(mPreferenceUtils?.getValue(Constants.VENDOR_CODE))

            text_update_profile?.setOnClickListener {
                if (edt_username.text.toString()?.isNullOrEmpty()) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter full name"
                    )
                } /*else if (edt_email.text.toString()?.isNullOrEmpty()) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter email"
                    )
                }*/ else if (edit_vendor_code.text.toString()?.isNullOrEmpty()) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter vendor code"
                    )
                } else {
                    callUpdateProfile()
                }
            }
        }
    }

    private fun onFocusVendorField() {
        try {
            edit_vendor_code.requestFocus()
            val handler = Handler(Looper.myLooper()!!)
            handler.postDelayed({
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(edit_vendor_code, InputMethodManager.SHOW_IMPLICIT)
            }, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callUpdateProfile() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                mobile = edt_mobile_no.text.toString().trim(),
                name = edt_username.text.toString().trim(),
                email = edt_email.text.toString().trim(),
                vendor_code = edit_vendor_code.text.toString().trim()
            )
            viewModel.updateProfile(commonRequestObj).observe(this, Observer {
                it?.run {
                    switchView(1, "")
                    if (status) {
                        mPreferenceUtils.setValue(
                            Constants.USER_NAME,
                            edt_username.text.toString().trim()
                        )
                        mPreferenceUtils.setValue(
                            Constants.USER_EMAIL,
                            edt_email.text.toString().trim()
                        )
                        mPreferenceUtils.setValue(
                            Constants.VENDOR_CODE,
                            edit_vendor_code.text.toString().trim()
                        )

//                        if (isVendorUpdate) {
                        mPreferenceUtils.clear()
                        mPreferenceUtils.setValue(Constants.IS_FIRST_TIME, false)

                        LocationUtils.setCurrentLocation(null)
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Profile Update")
                            .setMessage("Profile Updated Successfully")
                            .setPositiveButton("Login") { dialog, _ ->
                                dialog.dismiss()
                                var intent: Intent =
                                    Intent(activity!!, LoginActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                activity?.startActivity(intent)
                                activity?.finish()
                            }
                            .setCancelable(false)
                            .show()
//                        }
                    }
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        message
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

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    ll_container?.visibility = View.GONE
                }

                1 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }

                2 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                }

                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.HIDE_NAV_DRAWER_TOOLBAR, "", null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
//            R.id.cv_store->{
//                mSwichFragmentListener?.onSwitchFragment(Constants.STORE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
//            }

        }
    }

    companion object {
        fun newInstance(
            bundle: Any?
        ): Fragment {
            val fragment = UpdateProfileFragment()
            fragment.arguments = bundle as Bundle?
            return fragment
        }

    }
}