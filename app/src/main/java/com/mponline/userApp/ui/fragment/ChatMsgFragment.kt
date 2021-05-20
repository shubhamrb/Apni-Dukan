package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import androidx.viewpager.widget.ViewPager
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.response.CategorylistItem
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.model.response.StorelistItem
import com.mponline.userApp.ui.adapter.*
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chat_msg.view.*
import kotlinx.android.synthetic.main.fragment_chat_msg.view.relative_frag
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import kotlinx.android.synthetic.main.layout_empty.view.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class ChatMsgFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_chat_msg, container, false)

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
            if(arguments?.containsKey("data")!!){
                mOrderHistoryDataItem = arguments?.getParcelable("data")
                callGetChatList()
            }
        }
        view?.relative_frag?.setOnClickListener {  }
        view?.image_send_msg?.setOnClickListener {
            if(!view?.edit_msg?.text?.toString()?.trim()?.isNullOrEmpty()!!){
                //API hit
            }else{
                //show error

            }
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
        when(view?.id){
            R.id.cv_store->{
                mSwichFragmentListener?.onSwitchFragment(Constants.STORE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
            }

        }
    }

    private fun callGetChatList() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                orderid = mOrderHistoryDataItem?.orderId!!
            )
            viewModel?.getChatList(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    if (success) {
                        switchView(1, "")
                        view?.rv_chat_msg?.setHasFixedSize(true)
                        view?.rv_chat_msg?.layoutManager =
                            LinearLayoutManager(
                                activity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        view?.rv_chat_msg?.adapter = ChatMsgAdapter(
                            activity,
                            this@ChatMsgFragment,
                            data
                        )
                    } else {
                        switchView(0, "")
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            resources?.getString(R.string.no_net)!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    rv_chat_msg?.visibility = View.GONE
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                    rv_chat_msg?.visibility = View.VISIBLE
                }
                2 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    rv_chat_msg?.visibility = View.GONE
                }
                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    relative_empty?.visibility = View.GONE
                    rv_chat_msg?.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        fun newInstance(
            context: Activity,
            mOrderHistoryDataItem: Any
        ): Fragment {
            val fragment = ChatMsgFragment()
            if(mOrderHistoryDataItem is OrderHistoryDataItem){
                val bundle = Bundle()
                bundle.putParcelable("data", mOrderHistoryDataItem)
                fragment.arguments = bundle
            }
            return fragment
        }
    }


}