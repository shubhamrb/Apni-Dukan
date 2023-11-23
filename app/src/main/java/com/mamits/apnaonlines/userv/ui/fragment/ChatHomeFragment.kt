package com.mamits.apnaonlines.userv.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.userv.ui.adapter.ChatAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseFragment
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.common_toolbar_normal.view.image_back
import kotlinx.android.synthetic.main.common_toolbar_normal.view.toolbar_title
import kotlinx.android.synthetic.main.fragment_chat_home.view.next_btn
import kotlinx.android.synthetic.main.fragment_chat_home.view.relative_frag
import kotlinx.android.synthetic.main.fragment_chat_home.view.rv_chat_list
import kotlinx.android.synthetic.main.fragment_chat_msg.view.rv_chat_msg
import kotlinx.android.synthetic.main.layout_empty.view.relative_empty
import kotlinx.android.synthetic.main.layout_empty.view.text_empty
import kotlinx.android.synthetic.main.layout_progress.relative_progress

@AndroidEntryPoint
class ChatHomeFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    private val START_PAGE = 0
    private var CURRENT_PAGE = START_PAGE
    private val LIMIT = 15
    var chatAdapter: ChatAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_chat_home, container, false)

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

        view?.relative_frag?.setOnClickListener { }
        view?.image_back?.setOnClickListener { }
        view?.toolbar_title?.text = "Chat"

        //Chat home
        view?.rv_chat_list?.setHasFixedSize(true)
        view?.rv_chat_list?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )

        chatAdapter = ChatAdapter(
            activity,
            this@ChatHomeFragment
        )

        view?.rv_chat_list?.adapter = chatAdapter

        view?.next_btn.setOnClickListener {
            CURRENT_PAGE++
            callOrderHistoryApi(CURRENT_PAGE)
        }
        callOrderHistoryApi(CURRENT_PAGE)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        CommonUtils.printLog("ONRESUME", "Called")
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
//        mView?.app_bar_normal?.visibility = View.VISIBLE
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onStop() {
        super.onStop()
//        mView?.app_bar_normal?.visibility = View.INVISIBLE
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.rl_chat -> {
                if (obj != null && obj is OrderHistoryDataItem) {
                    if (obj?.status == 1) {
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            "Please wait till vendor accept your order request"
                        )
                    } else if (obj?.status == 2 || obj?.status == 5) {
                        mSwichFragmentListener?.onSwitchFragment(
                            Constants.CHAT_MSG_PAGE,
                            Constants.WITH_NAV_DRAWER,
                            obj,
                            null
                        )
                    }
                }
            }

        }
    }

    private fun callOrderHistoryApi(current_page: Int) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                orderid = "",
                start = current_page.toString(),
                pagelength = LIMIT.toString(),
                status = "2"
            )
            viewModel.getOrderHistory(commonRequestObj).observe(viewLifecycleOwner, Observer {
                it?.run {
                    if (status) {
                        if (data != null && data?.size!! > 0) {
                            switchView(1, "")
                            if (it?.next) {
                                view?.next_btn!!.visibility = View.VISIBLE
                            } else {
                                view?.next_btn!!.visibility = View.GONE
                            }
                            chatAdapter?.setList(data)
                        } else {
                            switchView(0, "No Chat Found")
                        }
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
                    text_empty.text = msg
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


}