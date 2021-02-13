package com.mponline.userApp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.ui.adapter.PaymentDetailAdapter
import com.mponline.userApp.ui.adapter.PaymentMethodAdapter
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.utils.Constants
import kotlinx.android.synthetic.main.fragment_payment_summary.view.*
import kotlinx.android.synthetic.main.fragment_payment_summary.view.relative_frag
import kotlinx.android.synthetic.main.fragment_service.view.*

class PaymentSummaryFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null

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

        view?.relative_frag?.setOnClickListener {

        }
        view?.text_free_coupons?.setOnClickListener {
            mSwichFragmentListener?.onSwitchFragment(
                Constants.COUPON_PAGE,
                Constants.WITH_NAV_DRAWER,
                null,
                null
            )
        }

        //Payment detail
        view?.rv_payment_details?.setHasFixedSize(true)
        view?.rv_payment_details?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_payment_details?.adapter = PaymentDetailAdapter(
            activity,
            this
        )

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
}