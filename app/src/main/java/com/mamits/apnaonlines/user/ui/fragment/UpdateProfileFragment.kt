package com.mamits.apnaonlines.user.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.listener.OnItemClickListener
import com.mamits.apnaonlines.user.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.user.ui.base.BaseFragment
import com.mamits.apnaonlines.user.util.CommonUtils
import com.mamits.apnaonlines.user.util.Constants
import com.mamits.apnaonlines.user.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.common_toolbar_normal.view.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class UpdateProfileFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

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
        view?.image_back?.setOnClickListener { }
        view?.toolbar_title?.text = "Update Profile"
        view?.image_back?.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        view?.run {
            edt_username?.setText(mPreferenceUtils?.getValue(Constants.USER_NAME))
            edt_email?.setText(mPreferenceUtils?.getValue(Constants.USER_EMAIL))
            edt_mobile_no?.setText(mPreferenceUtils?.getValue(Constants.USER_MOBILE))

            text_update_profile?.setOnClickListener {
                if (edt_username.text.toString()?.isNullOrEmpty()) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter full name"
                    )
                } else if (edt_email.text.toString()?.isNullOrEmpty()) {
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter email"
                    )
                } else {
                    callUpdateProfile()
                }
            }
        }
    }

    private fun callUpdateProfile() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                mobile = edt_mobile_no.text.toString().trim(),
                name = edt_username.text.toString().trim(),
                email = edt_email.text.toString().trim()
            )
            viewModel?.updateProfile(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    switchView(1, "")
                    if (status) {
                        mPreferenceUtils?.setValue(
                            Constants.USER_NAME,
                            edt_username.text.toString().trim()
                        )
                        mPreferenceUtils?.setValue(
                            Constants.USER_EMAIL,
                            edt_email.text.toString().trim()
                        )
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
}