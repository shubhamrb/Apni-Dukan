package com.mamits.apnaonlines.user.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.listener.OnItemClickListener
import com.mamits.apnaonlines.user.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.user.ui.adapter.*
import com.mamits.apnaonlines.user.ui.base.BaseFragment
import com.mamits.apnaonlines.user.util.Constants
import kotlinx.android.synthetic.main.fragment_stores.view.relative_frag
import kotlinx.android.synthetic.main.fragment_sub_services.view.*

class CouponFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_sub_services, container, false)

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


        //coupons
        view?.text_list_title?.text = activity?.resources?.getString(R.string.select_coupons)
        view?.rv_sub_service?.setHasFixedSize(true)
        view?.rv_sub_service?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_sub_service?.adapter = CouponsAdapter(
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
        when(view?.id){
//            R.id.cv_store->{
//                mSwichFragmentListener?.onSwitchFragment(Constants.STORE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
//            }

        }
    }
}