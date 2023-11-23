package com.mamits.apnaonlines.userv.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.ui.adapter.*
import com.mamits.apnaonlines.userv.ui.base.BaseFragment
import com.mamits.apnaonlines.userv.util.Constants
import kotlinx.android.synthetic.main.common_toolbar_normal.view.*
import kotlinx.android.synthetic.main.fragment_download_list.view.*

class DownloadListFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_download_list, container, false)

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

        view?.relative_frag?.setOnClickListener {  }
        view?.image_back?.setOnClickListener {
            //backstack
        }

        //coupons
        view?.toolbar_title?.text = activity?.resources?.getString(R.string.my_account)
        view?.rv_download_files?.setHasFixedSize(true)
        view?.rv_download_files?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_download_files?.adapter = DownloadAdapter(
            activity,
            this
        )

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.HIDE_NAV_DRAWER_TOOLBAR,"",null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
//            R.id.cv_store->{
//                mSwichFragmentListener?.onSwitchFragment(Constants.STORE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
//            }

        }
    }
}