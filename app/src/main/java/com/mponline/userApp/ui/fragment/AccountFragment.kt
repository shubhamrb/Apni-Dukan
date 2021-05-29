package com.mponline.userApp.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.LocationUtils
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.activity.FormPreviewActivity
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
import kotlinx.android.synthetic.main.fragment_account.view.rv_order_history
import kotlinx.android.synthetic.main.layout_progress.*

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

        callOrderHistoryApi()
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

    private fun callOrderHistoryApi() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                longitude = LocationUtils?.getCurrentLocation()?.lng!!
            )
            viewModel?.getOrderHistory(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        view?.rv_order_history?.setHasFixedSize(true)
                        view?.rv_order_history?.layoutManager =
                            LinearLayoutManager(
                                activity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        view?.rv_order_history?.adapter = OrderHistoryAdapter(
                            activity,
                            this@AccountFragment,
                            data!!
                        )
                    } else {
                        switchView(0, "")
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            resources?.getString(R.string.no_net)!!
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
            R.id.text_make_payment->{
                if(obj is OrderHistoryDataItem) {
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.PAYMENT_SUMMARY_PAGE,
                        Constants.WITH_NAV_DRAWER,
                        obj,
                        null
                    )
                }
            }
            R.id.ll_submit_rating->{
                if(obj is OrderHistoryDataItem) {
//                    callSaveRating(obj)
                }
            }
            R.id.text_view_details->{
                if(obj is OrderHistoryDataItem){
                    var intent: Intent = Intent(activity!!, FormPreviewActivity::class.java)
                    intent?.putExtra("data", obj?.orderDetail!!)
                    activity?.startActivity(intent)
                }
            }
            R.id.rl_chat->{
                if(obj is OrderHistoryDataItem) {
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.CHAT_MSG_PAGE_FROM_DETAIL,
                        Constants.WITH_NAV_DRAWER,
                        obj?.id,
                        obj.storedetail?.userId
                    )
                }
            }
            R.id.rl_call->{
                if(obj is OrderHistoryDataItem) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${obj?.storedetail?.mobileNumber}")
                    activity?.startActivity(intent)
                }
            }
            R.id.rl_whatsapp->{
                if(obj is OrderHistoryDataItem) {
                    val url = "https://api.whatsapp.com/send?phone=${if(obj?.storedetail?.whatsappNo?.startsWith("+91")!!) obj?.storedetail?.whatsappNo!! else "+91"+obj?.storedetail?.whatsappNo!!}"
                    try {
                        val pm: PackageManager = activity?.packageManager!!
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        activity?.startActivity(i)
                    } catch (e: PackageManager.NameNotFoundException) {
                        activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }
            }
        }
    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }
                2 -> {
                    relative_progress?.visibility = View.GONE
                }
                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    ll_container?.visibility = View.GONE
                }
            }
        }
    }

}