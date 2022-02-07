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
import kotlinx.android.synthetic.main.fragment_change_pwd.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class ChangePwdFragment : BaseFragment(), OnItemClickListener {
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
        mView = inflater!!.inflate(R.layout.fragment_change_pwd, container, false)

        return mView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context!=null){
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.image_back?.setOnClickListener {  }
        view?.toolbar_title?.text = "Change Password"
        view?.image_back?.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        view?.run {
            text_change_pwd.setOnClickListener {
                if(edt_old_pwd.text.toString()?.isNullOrEmpty()){
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter old password"
                    )
                }else if(edt_new_pwd1.text.toString()?.isNullOrEmpty()){
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please enter new password"
                    )
                }else if(edt_confirm_pwd.text.toString()?.isNullOrEmpty()){
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Please confirm new password"
                    )
                }else if(!edt_confirm_pwd.text.toString()?.equals(edt_new_pwd1.text.toString()!!)!!){
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        "Password didn't match"
                    )
                }else {
                    callChangePwd()
                }
            }
        }

    }

    private fun callChangePwd() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                oldpassword = edt_old_pwd.text.toString().trim(),
                newpassword = edt_confirm_pwd.text.toString().trim()
            )
            viewModel?.changePwd(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    switchView(1, "")
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
        mSwichFragmentListener?.onSwichToolbar(Constants.HIDE_NAV_DRAWER_TOOLBAR,"",null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
//            R.id.cv_store->{
//                mSwichFragmentListener?.onSwitchFragment(Constants.STORE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
//            }

        }
    }
}