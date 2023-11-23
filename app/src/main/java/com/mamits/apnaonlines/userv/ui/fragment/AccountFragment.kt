package com.mamits.apnaonlines.userv.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.mamits.apnaonlines.userv.BuildConfig
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.model.response.OrderDetailItem
import com.mamits.apnaonlines.userv.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.userv.ui.activity.EnquirySupportWebviewActivity
import com.mamits.apnaonlines.userv.ui.activity.FormPreviewActivity
import com.mamits.apnaonlines.userv.ui.adapter.OrderHistoryAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseFragment
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.utils.DateUtils
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.common_toolbar_normal.view.image_back
import kotlinx.android.synthetic.main.common_toolbar_normal.view.toolbar_title
import kotlinx.android.synthetic.main.fragment_account.view.ll_change_pwd
import kotlinx.android.synthetic.main.fragment_account.view.ll_container
import kotlinx.android.synthetic.main.fragment_account.view.ll_enquiry
import kotlinx.android.synthetic.main.fragment_account.view.ll_help
import kotlinx.android.synthetic.main.fragment_account.view.ll_shareapp
import kotlinx.android.synthetic.main.fragment_account.view.ll_update_profile
import kotlinx.android.synthetic.main.fragment_account.view.ll_update_vendor
import kotlinx.android.synthetic.main.fragment_account.view.relative_frag
import kotlinx.android.synthetic.main.fragment_account.view.text_email
import kotlinx.android.synthetic.main.fragment_account.view.text_mobile
import kotlinx.android.synthetic.main.fragment_account.view.text_order_title
import kotlinx.android.synthetic.main.fragment_account.view.text_username
import kotlinx.android.synthetic.main.layout_progress.relative_progress

@AndroidEntryPoint
class AccountFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    private val START_PAGE = 0
    private var CURRENT_PAGE = START_PAGE
    private val LIMIT = 15
    var mAdapter: OrderHistoryAdapter? = null
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
        if (context != null) {
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        view?.relative_frag?.setOnClickListener { }
        view?.image_back?.setOnClickListener { }
        view?.toolbar_title?.text = "My Account"

        view?.image_back?.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        view?.text_order_title?.setOnClickListener {
            mSwichFragmentListener?.onSwitchFragment(Constants.ORDER_HISTORY_PAGE, "", null, null)
        }

        view?.ll_shareapp?.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Apna Online")
                var shareMessage = "\nLet me recommend you Apna Online application\n\n"
                shareMessage =
                    """
                   ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                   """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        }
        view?.ll_help?.setOnClickListener {
            var intent: Intent = Intent(context, EnquirySupportWebviewActivity::class.java)
            intent?.putExtra("type", "help")
            context?.startActivity(intent)
        }
        view?.ll_enquiry?.setOnClickListener {
            var intent: Intent = Intent(context, EnquirySupportWebviewActivity::class.java)
            intent?.putExtra("type", "enquiry")
            context?.startActivity(intent)
        }
        //Stores
        /*view?.rv_order_history?.setHasFixedSize(true)
        view?.rv_order_history?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_order_history?.adapter = OrderHistoryAdapter(
            activity,
            this
        )*/

        view?.ll_update_profile?.setOnClickListener {
            mSwichFragmentListener?.onSwitchFragmentParent(
                Constants.UPDATE_PROFILE,
                Constants.WITH_NAV_DRAWER,
                null,
                null
            )
        }
        view?.ll_update_vendor?.setOnClickListener {
            var bundle: Bundle = Bundle();
            bundle.putBoolean("update_vendor", true)
            mSwichFragmentListener?.onSwitchFragmentParent(
                Constants.UPDATE_PROFILE,
                Constants.WITH_NAV_DRAWER,
                null,
                bundle
            )
        }
        view?.ll_change_pwd?.setOnClickListener {
            mSwichFragmentListener?.onSwitchFragmentParent(
                Constants.CHANGE_PWD,
                Constants.WITH_NAV_DRAWER,
                null,
                null
            )
        }


        /* view?.rv_order_history?.setHasFixedSize(true)
         view?.rv_order_history?.layoutManager =
             LinearLayoutManager(
                 activity,
                 RecyclerView.VERTICAL,
                 false
             )
         mAdapter = OrderHistoryAdapter(
             activity,
             this@AccountFragment,
             isFromAccount = true
         )
         view?.rv_order_history?.adapter = mAdapter

         callOrderHistoryApi(CURRENT_PAGE)
         view.btn_next.setOnClickListener {
             CURRENT_PAGE++
             callOrderHistoryApi(CURRENT_PAGE)
         }*/
    }

    fun setUserInfo() {
        mView?.run {
            text_username.text = mPreferenceUtils?.getValue(Constants.USER_NAME)
            if (!mPreferenceUtils?.getValue(Constants.USER_EMAIL)?.isNullOrEmpty()) {
                text_email.text = mPreferenceUtils?.getValue(Constants.USER_EMAIL)
                text_email.visibility = View.VISIBLE
            } else {
                text_email.visibility = View.GONE
            }
            text_mobile.text = mPreferenceUtils?.getValue(Constants.USER_MOBILE)
        }
    }

    private fun callOrderHistoryApi(current_page: Int) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                start = current_page.toString(),
                pagelength = LIMIT.toString()
            )
            viewModel?.getOrderHistory(commonRequestObj)?.observe(viewLifecycleOwner, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        if (data != null && data?.size > 0) {
                            view?.text_order_title?.visibility = View.VISIBLE

                            var orderlist: ArrayList<OrderDetailItem> = arrayListOf()
                            data?.forEachIndexed { index, order ->
                                if (order?.status == 2) {
                                    var currDateTime =
                                        DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss")
                                    var estimatedDateTime = DateUtils.addHrMinuteToDateStr(
                                        order?.acceptedAt!!,
                                        if (order?.timeType?.equals("hour")!!) true else false,
                                        order?.orderCompletionTime
                                    )
                                    var timerObj = DateUtils.checkTimeDifference(
                                        currDateTime,
                                        estimatedDateTime
                                    )
                                    data?.get(index)?.timerObj = timerObj
                                }
                            }
                            mAdapter?.setList(data)

                            Handler().postDelayed(Runnable {
                                switchView(1, "")
                            }, 500)
                        } else {
                            view?.text_order_title?.visibility = View.GONE
                        }
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
        mSwichFragmentListener?.onSwichToolbar(Constants.HIDE_NAV_DRAWER_TOOLBAR, "", null)
        setUserInfo()
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_store -> {
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.STORE_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    null,
                    null
                )
            }

            R.id.text_make_payment -> {
                if (obj is OrderHistoryDataItem) {
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.PAYMENT_SUMMARY_PAGE,
                        Constants.WITH_NAV_DRAWER,
                        obj,
                        null
                    )
                }
            }

            R.id.ll_submit_rating -> {
                if (obj is OrderHistoryDataItem) {
//                    callSaveRating(obj)
                }
            }

            R.id.text_view_details -> {
                if (obj is OrderHistoryDataItem) {
                    var intent: Intent = Intent(activity!!, FormPreviewActivity::class.java)
                    intent?.putExtra("data", obj?.orderDetail!!)
                    activity?.startActivity(intent)
                }
            }

            R.id.rl_chat -> {
                if (obj is OrderHistoryDataItem) {
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.CHAT_MSG_PAGE_FROM_DETAIL,
                        Constants.WITH_NAV_DRAWER,
                        obj?.id,
                        obj.storedetail?.userId
                    )
                }
            }

            R.id.rl_call -> {
                if (obj is OrderHistoryDataItem) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${obj?.storedetail?.mobileNumber}")
                    activity?.startActivity(intent)
                }
            }

            R.id.rl_whatsapp -> {
                if (obj is OrderHistoryDataItem) {
                    val url = "https://api.whatsapp.com/send?phone=${
                        if (obj?.storedetail?.whatsappNo?.startsWith("+91")!!) obj?.storedetail?.whatsappNo!! else "+91" + obj?.storedetail?.whatsappNo!!
                    }"
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