package com.mponline.userApp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.adapter.BannerPagerAdapter
import com.mponline.userApp.ui.adapter.OrderHistoryAdapter
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.common_toolbar_normal.view.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_account.view.relative_frag
import kotlinx.android.synthetic.main.fragment_chat_home.view.*

@AndroidEntryPoint
class AccountFragment : BaseFragment(), OnItemClickListener {
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
        mView = inflater!!.inflate(R.layout.fragment_account, container, false)

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
        mView = view
        view?.relative_frag?.setOnClickListener {  }
        view?.image_back?.setOnClickListener {  }
        view?.toolbar_title?.text = "My Account"

        //Stores
        view?.rv_order_history?.setHasFixedSize(true)
        view?.rv_order_history?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_order_history?.adapter = OrderHistoryAdapter(
            activity,
            this
        )

        view?.ll_update_profile?.setOnClickListener {
            mSwichFragmentListener?.onSwitchFragmentParent(Constants.UPDATE_PROFILE, Constants.WITH_NAV_DRAWER, null, null)
        }
        view?.ll_change_pwd?.setOnClickListener {
            mSwichFragmentListener?.onSwitchFragmentParent(Constants.CHANGE_PWD, Constants.WITH_NAV_DRAWER, null, null)
        }
    }

    fun setUserInfo(){
        mView?.run {
            text_username.text = mPreferenceUtils?.getValue(Constants.USER_NAME)
            if(!mPreferenceUtils?.getValue(Constants.USER_EMAIL)?.isNullOrEmpty()){
                text_email.text = mPreferenceUtils?.getValue(Constants.USER_EMAIL)
                text_email.visibility = View.VISIBLE
            }else{
                text_email.visibility = View.GONE
            }
            text_mobile.text = mPreferenceUtils?.getValue(Constants.USER_MOBILE)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.HIDE_NAV_DRAWER_TOOLBAR,"",null)
        setUserInfo()
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.cv_store->{
                mSwichFragmentListener?.onSwitchFragment(Constants.STORE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
            }

        }
    }
}