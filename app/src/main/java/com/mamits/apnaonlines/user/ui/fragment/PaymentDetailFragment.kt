package com.mamits.apnaonlines.user.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.listener.OnItemClickListener
import com.mamits.apnaonlines.user.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.user.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.user.ui.base.BaseFragment
import com.mamits.apnaonlines.user.util.Constants
import com.mamits.apnaonlines.user.util.ImageGlideUtils
import kotlinx.android.synthetic.main.fragment_payment_detail.view.image_qr_code
import kotlinx.android.synthetic.main.fragment_payment_detail.view.relative_frag
import kotlinx.android.synthetic.main.fragment_payment_detail.view.text_instr
import kotlinx.android.synthetic.main.fragment_payment_detail.view.text_instruction
import kotlinx.android.synthetic.main.fragment_payment_detail.view.text_payment_mode
import kotlinx.android.synthetic.main.fragment_payment_detail.view.text_scan_instruction
import kotlinx.android.synthetic.main.fragment_payment_detail.view.text_store_name

class PaymentDetailFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null

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
        if (context != null) {
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it?.containsKey("obj")) {
                mOrderHistoryDataItem = it?.getParcelable("obj")
                if (!mOrderHistoryDataItem?.isPaytoShop?.isNullOrEmpty()!! && mOrderHistoryDataItem?.isPaytoShop?.equals(
                        "Yes",
                        true
                    )!!
                ) {
                    view?.text_payment_mode?.text =
                        "Please visit ${mOrderHistoryDataItem?.storedetail?.name} shop & make payment of ${
                            activity?.resources?.getString(R.string.rs)
                        } ${mOrderHistoryDataItem?.payableAmount}"
                    view?.text_store_name?.visibility = View.GONE
                    view?.text_scan_instruction?.visibility = View.GONE
                    view?.image_qr_code?.visibility = View.GONE
                    view?.text_instruction?.visibility = View.GONE
                } else {
                    view?.text_scan_instruction?.text = "ऊपर दिए नंबर पर पेमेंट करे"
                    view?.text_instruction?.text =
                        "क्यूआर कोड को स्कैन करके ${activity?.resources?.getString(R.string.rs)} ${mOrderHistoryDataItem?.payableAmount}का पेमेंट करे"
                    view?.image_qr_code?.visibility = View.VISIBLE
                    view?.text_store_name?.visibility = View.VISIBLE
                    view?.text_scan_instruction?.visibility = View.VISIBLE
                    view?.text_instruction?.visibility = View.VISIBLE
                    view?.text_payment_mode?.text =
                        "Upi number: ${mOrderHistoryDataItem?.storedetail?.upiNumber}"
                }
                view?.text_store_name?.text = mOrderHistoryDataItem?.storedetail?.name
                view?.text_instr?.text =
                    "पेमेंट करने के बाद स्क्रीनशॉट लेकर  \" OTP  एवं \nडॉक्यूमेंट भेजें \" बटन पर क्लिक करके शेयर जरूर करे "
                ImageGlideUtils.loadUrlImage(
                    activity!!,
                    mOrderHistoryDataItem?.storedetail?.qrcode!!,
                    view?.image_qr_code
                )
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
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
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