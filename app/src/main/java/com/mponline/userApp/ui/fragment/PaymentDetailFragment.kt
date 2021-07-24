package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.adapter.PaymentDetailAdapter
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.adapter.SubServiceAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.fragment_payment_detail.view.*
import kotlinx.android.synthetic.main.item_service.view.*

class PaymentDetailFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    var mOrderHistoryDataItem:OrderHistoryDataItem?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_payment_detail, container, false)

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
        arguments?.let {
            if(it?.containsKey("obj")){
                mOrderHistoryDataItem = it?.getParcelable("obj")
                if(!mOrderHistoryDataItem?.isPaytoShop?.isNullOrEmpty()!! && mOrderHistoryDataItem?.isPaytoShop?.equals("Yes",true)!!){
                    view?.text_payment_mode?.text = "Please visit ${mOrderHistoryDataItem?.storedetail?.name} shop & make payment of ${activity?.resources?.getString(R.string.rs)} ${mOrderHistoryDataItem?.payableAmount}"
                    view?.text_store_name?.visibility = View.GONE
                    view?.text_scan_instruction?.visibility = View.GONE
                    view?.image_qr_code?.visibility = View.GONE
                    view?.text_instruction?.visibility = View.GONE
                }else{
                    view?.text_scan_instruction?.text = "Scan the QR Code to make payment of ${activity?.resources?.getString(R.string.rs)} ${mOrderHistoryDataItem?.payableAmount}"
                    view?.text_instruction?.text = "Scan the QR Code to make payment of ${activity?.resources?.getString(R.string.rs)} ${mOrderHistoryDataItem?.payableAmount}"
                    view?.image_qr_code?.visibility = View.VISIBLE
                    view?.text_store_name?.visibility = View.VISIBLE
                    view?.text_scan_instruction?.visibility = View.VISIBLE
                    view?.text_instruction?.visibility = View.VISIBLE
                    view?.text_payment_mode?.text = "Upi number: ${mOrderHistoryDataItem?.storedetail?.upiNumber}"
                }
                view?.text_store_name?.text = mOrderHistoryDataItem?.storedetail?.name
                view?.text_instr?.text = "Once payment is done kindly share it with\nthe kiosk on chat section."
                ImageGlideUtils.loadUrlImage(activity!!, mOrderHistoryDataItem?.storedetail?.qrcode!!, view?.image_qr_code)
            }
        }
        view?.relative_frag?.setOnClickListener {

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
    }

    companion object {
        fun newInstance(
            context: Activity,
            mOrderHistoryDataItem: OrderHistoryDataItem
        ): Fragment {
            val fragment = PaymentDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("obj", mOrderHistoryDataItem)
            fragment.arguments = bundle
            return fragment
        }
    }

}