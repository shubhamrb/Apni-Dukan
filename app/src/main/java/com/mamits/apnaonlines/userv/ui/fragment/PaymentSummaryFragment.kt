package com.mamits.apnaonlines.userv.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.model.LocationUtils
import com.mamits.apnaonlines.userv.model.PaymentSummaryObj
import com.mamits.apnaonlines.userv.model.response.DataItem
import com.mamits.apnaonlines.userv.model.response.GetCouponListResponse
import com.mamits.apnaonlines.userv.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.userv.ui.activity.OffersActivity
import com.mamits.apnaonlines.userv.ui.adapter.PaymentDetailAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseFragment
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_payment_summary.view.edt_coupon_code
import kotlinx.android.synthetic.main.fragment_payment_summary.view.image_close
import kotlinx.android.synthetic.main.fragment_payment_summary.view.relative_frag
import kotlinx.android.synthetic.main.fragment_payment_summary.view.rv_payment_details
import kotlinx.android.synthetic.main.fragment_payment_summary.view.text_apply
import kotlinx.android.synthetic.main.fragment_payment_summary.view.text_coupon_amt
import kotlinx.android.synthetic.main.fragment_payment_summary.view.text_free_coupons
import kotlinx.android.synthetic.main.fragment_payment_summary.view.text_pay_online
import kotlinx.android.synthetic.main.fragment_payment_summary.view.text_pay_to_shop
import kotlinx.android.synthetic.main.fragment_payment_summary.view.text_subtotal_amt
import kotlinx.android.synthetic.main.fragment_payment_summary.view.text_total_amt
import kotlinx.android.synthetic.main.fragment_payment_summary.view.text_upi
import kotlinx.android.synthetic.main.fragment_service.view.ll_container
import kotlinx.android.synthetic.main.layout_progress.relative_progress
import java.util.concurrent.ExecutionException

@AndroidEntryPoint
class PaymentSummaryFragment : BaseFragment(), OnItemClickListener,
    PaymentOptBottomsheetFragment.PaymentListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null
    val viewModel: UserListViewModel by viewModels()
    var mCouponAmt: String = "0.0"
    var mPayableAmt: String = "0.0"
    var mGetCouponListResponse: GetCouponListResponse? = null
    var isCouponApplied = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_payment_summary, container, false)

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

        arguments?.let {
            if (it?.containsKey("obj")) {
                mOrderHistoryDataItem = it?.getParcelable("obj")
                if (mOrderHistoryDataItem?.payableAmount != null) {
                    mPayableAmt = mOrderHistoryDataItem?.payableAmount!!
                }
                if (mOrderHistoryDataItem?.offerAmount != null) {
                    mCouponAmt = mOrderHistoryDataItem?.offerAmount!!
                }
                callCouponList()
            }
        }

        view?.relative_frag?.setOnClickListener {

        }
        view?.image_close?.setOnClickListener {
            if (isCouponApplied) {
                callRemoveCoupon()
            } else {
                mView?.edt_coupon_code?.setText("")
            }
        }
        view?.text_apply?.setOnClickListener {
            if (!view?.edt_coupon_code?.text?.toString()?.trim()?.isNullOrEmpty()!!) {
                callApplyCoupon(view?.edt_coupon_code?.text?.toString()?.trim()!!)
            }else{
                CommonUtils.createSnackBar(
                    activity?.findViewById(android.R.id.content)!!,
                    "Please select any coupon"
                )
            }
        }

        view?.edt_coupon_code.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    view?.image_close?.visibility = View.VISIBLE
                } else {
                    view?.image_close?.visibility = View.GONE
                    mView?.text_coupon_amt?.text = ""
                    if (isCouponApplied) {
                        callRemoveCoupon()
                    }
                    mCouponAmt = "0.0"
                    mPayableAmt = mOrderHistoryDataItem?.payableAmount!!
                    calculateTotal()
                }
            }
        })

        view?.text_free_coupons?.setOnClickListener {
            var intent: Intent = Intent(activity, OffersActivity::class.java)
            intent?.putExtra("order", mOrderHistoryDataItem)
            intent?.putExtra("type", "coupon")
            startActivityForResult(intent, 1001)
        }

        //Payment detail
        if (mOrderHistoryDataItem != null) {
            var arrayList: ArrayList<PaymentSummaryObj> = ArrayList()
            arrayList?.add(
                PaymentSummaryObj(
                    formDetailName = mOrderHistoryDataItem?.products?.name!!,
                    formDetailPrice = mOrderHistoryDataItem?.orderAmount!!
                )
            )
            view?.rv_payment_details?.setHasFixedSize(true)
            view?.rv_payment_details?.layoutManager =
                LinearLayoutManager(
                    activity,
                    RecyclerView.VERTICAL,
                    false
                )
            view?.rv_payment_details?.adapter = PaymentDetailAdapter(
                activity,
                this,
                arrayList
            )
        }
        if (mOrderHistoryDataItem?.storedetail?.paymentAcceptMode?.contains("Online", true)!!) {
            view?.text_pay_online?.visibility = View.VISIBLE
        } else {
            view?.text_pay_online?.visibility = View.GONE
        }
        if (mOrderHistoryDataItem?.storedetail?.paymentAcceptMode?.contains("Offline", true)!!) {
            view?.text_pay_to_shop?.visibility = View.VISIBLE
        } else {
            view?.text_pay_to_shop?.visibility = View.GONE
        }
        if (mOrderHistoryDataItem?.storedetail?.paymentAcceptMode?.contains("Upi", true)!!) {
            view?.text_upi?.visibility = View.VISIBLE
        } else {
            view?.text_upi?.visibility = View.GONE
        }
        view?.text_pay_online?.setOnClickListener {
            var orderdetail = mOrderHistoryDataItem
            orderdetail?.payableAmount = mPayableAmt
            showPaymentDialog(orderdetail!!)
        }
        view?.text_pay_to_shop?.setOnClickListener {
            var orderdetail = mOrderHistoryDataItem
            orderdetail?.isPaytoShop = "Yes"
            orderdetail?.payableAmount = mPayableAmt
            mSwichFragmentListener?.onSwitchFragment(
                Constants.PAYMENT_DETAIL_PAGE,
                Constants.WITH_NAV_DRAWER,
                orderdetail,
                null
            )
        }
        view?.text_upi?.setOnClickListener {
            var orderdetail = mOrderHistoryDataItem
            orderdetail?.isPaytoShop = "No"
            orderdetail?.payableAmount = mPayableAmt
            mSwichFragmentListener?.onSwitchFragment(
                Constants.PAYMENT_DETAIL_PAGE,
                Constants.WITH_NAV_DRAWER,
                mOrderHistoryDataItem,
                null
            )
        }

        calculateTotal()
    }

    fun showPaymentDialog(orderData: OrderHistoryDataItem) {
        val instance = PaymentOptBottomsheetFragment.newInstance(orderData)
        instance.show(childFragmentManager, "Payment")
    }

    fun calculateTotal() {
        mView?.text_coupon_amt?.text =
            "- " + activity?.resources?.getString(R.string.rs) + " ${mCouponAmt}"
        view?.text_subtotal_amt?.text = mOrderHistoryDataItem?.orderAmount!!
        var totalAmt = 0f
        totalAmt =
            if (!mPayableAmt?.equals("0.0")) mPayableAmt?.toFloat() else mOrderHistoryDataItem?.orderAmount?.toFloat()!!
        mView?.text_total_amt?.text = activity?.resources?.getString(R.string.rs) + " ${totalAmt}"
    }

    private fun callApplyCoupon(couponCode: String) {
        try {
            if (CommonUtils.isOnline(requireActivity())) {
                switchView(4, "")
                var commonRequestObj = getCommonRequestObj(
                    apiKey = getApiKey(),
                    orderid = mOrderHistoryDataItem?.id!!,
                    coupon = couponCode,
                    orderamount = mOrderHistoryDataItem?.orderAmount!!
                )
                viewModel.applyCoupon(commonRequestObj).observe(viewLifecycleOwner, Observer {
                    it?.run {
                        switchView(1, "")
                        if (status) {
                            mCouponAmt = data?.discountamount!!
                            mPayableAmt = data?.finalamountpay!!
                            mView?.text_coupon_amt?.text =
                                "- " + activity?.resources?.getString(R.string.rs) + " ${data?.discountamount!!}"
                            isCouponApplied = true
                            view?.text_apply?.text = "Remove"

                            calculateTotal()
                        } else {
                            view?.edt_coupon_code?.setText("")
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callRemoveCoupon() {
        try {
            if (CommonUtils.isOnline(requireActivity())) {
                switchView(4, "")
                var commonRequestObj = getCommonRequestObj(
                    apiKey = getApiKey(),
                    orderid = mOrderHistoryDataItem?.id!!,
                    discountamount = mCouponAmt,
                    finalamountpay = mPayableAmt
                )
                viewModel.removeCoupon(commonRequestObj).observe(viewLifecycleOwner, Observer {
                    it?.run {
                        switchView(1, "")
                        if (status) {
                            isCouponApplied = false
                            mView?.edt_coupon_code?.setText("")
                            view?.text_apply?.text = "Apply"

                            if (!mPayableAmt?.isNullOrEmpty() && !mCouponAmt?.isNullOrEmpty()) {
                                var payamt = mPayableAmt?.toFloat()
                                var cpnAmt = mCouponAmt?.toFloat()
                                mPayableAmt = mOrderHistoryDataItem?.orderAmount!!
                                mCouponAmt = "0.00"
                                calculateTotal()
                            }
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
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    private fun callCouponList() {
        try {
            if (CommonUtils.isOnline(requireActivity())) {
                switchView(3, "")
                var commonRequestObj =
                    getCommonRequestObj(
                        apiKey = getApiKey(),
                        orderid = mOrderHistoryDataItem?.id!!,
                        latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                        longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                        start = "0",
                        pagelength = "100"
                    )
                viewModel.getCouponList(commonRequestObj).observe(viewLifecycleOwner, Observer {
                    it?.run {
                        if (status) {
                            switchView(1, "")
                            mGetCouponListResponse = this
                            mGetCouponListResponse?.data?.forEach {
                                if (it?.id?.equals(mOrderHistoryDataItem?.offerId)) {
                                    mView?.edt_coupon_code?.setText(it?.coupon)
                                    if (it?.coupon != null && !it?.coupon?.isNullOrEmpty()) {
                                        isCouponApplied = true
                                        mView?.text_apply?.setText("Remove")
                                    } else {
                                        isCouponApplied = false
                                        mView?.text_apply?.setText("Apply")
                                    }
                                    if (mOrderHistoryDataItem?.offerAmount != null && !mOrderHistoryDataItem?.offerAmount?.isNullOrEmpty()!!) {
                                        mPayableAmt = mOrderHistoryDataItem?.orderAmount!!
                                        var res =
                                            ((mOrderHistoryDataItem?.orderAmount?.toFloat()!!) + (mOrderHistoryDataItem?.offerAmount?.toFloat()!!))
                                        mOrderHistoryDataItem?.orderAmount =
                                            mOrderHistoryDataItem?.orderAmount
                                        mCouponAmt = mOrderHistoryDataItem?.offerAmount!!
                                        mPayableAmt = mOrderHistoryDataItem?.payableAmount!!
                                        mView?.text_coupon_amt?.text =
                                            "- " + activity?.resources?.getString(R.string.rs) + " ${it?.discount_amount!!}"
                                        calculateTotal()
                                    }
                                }
                            }
                        } else {
                            switchView(0, "")
                            //                        CommonUtils.createSnackBar(
                            //                            activity?.findViewById(android.R.id.content)!!,
                            //                            message
                            //                        )
                        }
                    }
                })
            } else {
                CommonUtils.createSnackBar(
                    activity?.findViewById(android.R.id.content)!!,
                    resources?.getString(R.string.no_net)!!
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_service -> {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            if (data != null && data?.hasExtra("data")) {
                var dataItem = data?.getParcelableExtra<DataItem>("data")!!
                if (dataItem is DataItem) {
                    view?.edt_coupon_code?.setText(dataItem?.coupon)
                    view?.text_apply?.performClick()
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

                4 -> {
                    relative_progress?.visibility = View.VISIBLE
                }
            }
        }
    }


    companion object {
        fun newInstance(
            context: Activity,
            mOrderHistoryDataItem: OrderHistoryDataItem
        ): Fragment {
            val fragment = PaymentSummaryFragment()
            val bundle = Bundle()
            bundle.putParcelable("obj", mOrderHistoryDataItem)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onOptSelected(obj: Any?) {
        TODO("Not yet implemented")
    }

}