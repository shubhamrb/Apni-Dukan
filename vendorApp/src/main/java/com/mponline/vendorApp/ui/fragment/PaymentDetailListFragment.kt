package com.mponline.vendorApp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mponline.vendorApp.R
import com.mponline.vendorApp.listener.OnItemClickListener
import com.mponline.vendorApp.listener.OnSwichFragmentListener
import com.mponline.vendorApp.ui.adapter.RecentOrderAdapter
import com.mponline.vendorApp.ui.adapter.RecentTxnsAdapter
import com.mponline.vendorApp.ui.base.BaseFragment
import com.mponline.vendorApp.utils.Constants
import kotlinx.android.synthetic.main.fragment_payment_detail_list.view.*

class PaymentDetailListFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mType:String = ""
    var mSwichFragmentListener: OnSwichFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_payment_detail_list, container, false)

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
            if(it?.containsKey("type")){
                mType = arguments?.getString("type")!!
                if(mType == Constants.ALL_TXNS){
                    view?.group_txn_btns?.visibility = View.VISIBLE
                }else{
                    view?.group_txn_btns?.visibility = View.GONE
                }
            }
        }

//        view?.relative_frag?.setOnClickListener {  }

        //Stores
        view?.rv_txns?.setHasFixedSize(true)
        view?.rv_txns?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_txns?.adapter = RecentTxnsAdapter(
            activity,
            this, mType
        )

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
    }

    companion object{
        fun newInstance(
            context: Context,
            type:String,
            obj:Any?
        ): Fragment {
            val fragment = PaymentDetailListFragment()
            val bundle = Bundle()
            bundle.putString("type", type)
            fragment.arguments = bundle
            return fragment
        }
    }
}