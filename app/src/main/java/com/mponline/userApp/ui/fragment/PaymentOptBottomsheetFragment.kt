package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.mponline.userApp.R
import com.mponline.userApp.model.request.UserAuthRequestObj
import com.mponline.userApp.model.response.Data
import com.mponline.userApp.model.response.OrderDetailItem
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.activity.MainActivity
import com.mponline.userApp.ui.activity.PaymentActivity
import com.mponline.userApp.ui.activity.RegisterActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.PreferenceUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_bottom_otp.*
import kotlinx.android.synthetic.main.layout_bottom_otp.edt_otp
import kotlinx.android.synthetic.main.layout_bottom_otp.text_edit_phone
import kotlinx.android.synthetic.main.layout_bottom_otp.text_verify_otp
import kotlinx.android.synthetic.main.layout_bottom_otp.view.*
import kotlinx.android.synthetic.main.layout_bottom_payment_opt.*
import kotlinx.android.synthetic.main.layout_otp.*
import kotlinx.android.synthetic.main.layout_progress.*


@AndroidEntryPoint
class PaymentOptBottomsheetFragment : BottomSheetDialogFragment() {
    private var mListener: PaymentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mOrderItem:OrderHistoryDataItem? = null
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
        view?.run {
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

        fun newInstance(orderObj:OrderHistoryDataItem): PaymentOptBottomsheetFragment =
            PaymentOptBottomsheetFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("data", orderObj)
                    }
                }

    }

}
