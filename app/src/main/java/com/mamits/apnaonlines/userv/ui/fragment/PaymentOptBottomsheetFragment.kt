package com.mamits.apnaonlines.userv.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.userv.ui.activity.PaymentActivity
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_bottom_payment_opt.*


@AndroidEntryPoint
class PaymentOptBottomsheetFragment : BottomSheetDialogFragment() {
    private var mListener: PaymentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mOrderItem: OrderHistoryDataItem? = null
    var mView:View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_payment_opt, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
        arguments?.let {
            if(it?.containsKey("data")){
                mOrderItem = it?.getParcelable("data")
            }
        }
        view.run {
            text_paytm?.setOnClickListener {
                if(mOrderItem!=null){
                    var intent: Intent = Intent(activity!!, PaymentActivity::class.java)
                    intent?.putExtra("data", mOrderItem)
                    intent?.putExtra("paymentgateway", "paytm")
                    activity?.startActivity(intent)
                }
            }
            text_cashfree?.setOnClickListener {
                if(mOrderItem!=null){
                    var intent: Intent = Intent(activity!!, PaymentActivity::class.java)
                    intent?.putExtra("data", mOrderItem)
                    intent?.putExtra("paymentgateway", "cashfree")
                    activity?.startActivity(intent)
                }
            }
            text_phonepe?.setOnClickListener {
                if(mOrderItem!=null){
                    val intent = Intent(requireActivity(), PaymentActivity::class.java)
                    intent.putExtra("data", mOrderItem)
                    intent.putExtra("paymentgateway", "phonepe")
                    activity?.startActivity(intent)
                }
            }
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as PaymentListener
        } else {
            mListener = context as PaymentListener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface PaymentListener {
        fun onOptSelected(obj:Any?)
    }


    companion object {

        fun newInstance(orderObj: OrderHistoryDataItem): PaymentOptBottomsheetFragment =
            PaymentOptBottomsheetFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("data", orderObj)
                    }
                }

    }

}
