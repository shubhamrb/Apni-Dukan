package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.PaymentSummaryObj
import com.mponline.userApp.model.response.DataItem
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.activity.OffersActivity
import com.mponline.userApp.ui.adapter.PaymentDetailAdapter
import com.mponline.userApp.ui.adapter.PaymentMethodAdapter
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import kotlinx.android.synthetic.main.fragment_payment_summary.view.*
import kotlinx.android.synthetic.main.fragment_payment_summary.view.relative_frag
import kotlinx.android.synthetic.main.fragment_service.view.*
import kotlinx.android.synthetic.main.fragment_service.view.ll_container
import kotlinx.android.synthetic.main.layout_otp.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class PaymentSummaryFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    var mOrderHistoryDataItem:OrderHistoryDataItem?= null
    val viewModel: UserListViewModel by viewModels()
    var mCouponAmt:String = "0.0"
    var mPayableAmt:String = "0.0"

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
            if(it?.containsKey("obj")){
                mOrderHistoryDataItem = it?.getParcelable("obj")
            }
        }

        view?.relative_frag?.setOnClickListener {

        }
        view?.image_close?.setOnClickListener {
            view?.edt_coupon_code?.setText("")
            //Remove Coupon
        }
        view?.text_apply?.setOnClickListener {
          if(!view?.edt_coupon_code?.text?.toString()?.trim()?.isNullOrEmpty()!!){
              callApplyCoupon(view?.edt_coupon_code?.text?.toString()?.trim()!!)
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
                    mCouponAmt = "0.0"
                    mPayableAmt = "0.0"
                    calculateTotal()
                }
            }
        })

        view?.text_free_coupons?.setOnClickListener {
            var intent:Intent = Intent(activity, OffersActivity::class.java)
            intent?.putExtra("order", mOrderHistoryDataItem)
            intent?.putExtra("type","coupon")
            startActivityForResult(intent, 1001)
//            mSwichFragmentListener?.onSwitchFragment(
//                Constants.COUPON_PAGE,
//                Constants.WITH_NAV_DRAWER,
//                null,
//                null
//            )
        }

        //Payment detail
        if(mOrderHistoryDataItem!=null){
            var arrayList:ArrayList<PaymentSummaryObj> = ArrayList()
            arrayList?.add(PaymentSummaryObj(formDetailName = mOrderHistoryDataItem?.products?.name!!,
            formDetailPrice = mOrderHistoryDataItem?.products?.price!!))
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
            mView?.text_coupon_amt?.text = activity?.resources?.getString(R.string.rs)+" ${mCouponAmt}"
            view?.text_subtotal_amt?.text = mOrderHistoryDataItem?.products?.price!!
        }

        //Payment method
        view?.rv_payment_methods?.layoutManager =
            GridLayoutManager(
                activity, 2
            )
        view?.rv_payment_methods?.adapter = PaymentMethodAdapter(
            activity,
            this
        )

    }

    fun calculateTotal(){
        var totalAmt = 0f
        totalAmt =  if(!mPayableAmt?.equals("0.0")) mPayableAmt?.toFloat() else mOrderHistoryDataItem?.orderAmount?.toFloat()!!
        mView?.text_total_amt?.text = activity?.resources?.getString(R.string.rs)+" ${totalAmt}"
    }

    private fun callApplyCoupon(couponCode:String) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(4, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                orderid = mOrderHistoryDataItem?.id!!,
                coupon = couponCode,
                orderamount = mOrderHistoryDataItem?.orderAmount!!
            )
            viewModel?.applyCoupon(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    switchView(1, "")
                    if (status) {
                        mCouponAmt = data?.discountamount!!
                        mPayableAmt = data?.finalamountpay!!
                        mView?.text_coupon_amt?.text = "- "+activity?.resources?.getString(R.string.rs)+" ${data?.discountamount!!}"
                    }else{
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
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR,"",null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_service -> {
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.SUB_SERVICE_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    null,
                    null
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1001 && resultCode == Activity.RESULT_OK){
            if(data!=null && data?.hasExtra("data")){
                var dataItem = data?.getParcelableExtra<DataItem>("data")!!
                if(dataItem is DataItem){
                    view?.edt_coupon_code?.setText(dataItem?.coupon)
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

}