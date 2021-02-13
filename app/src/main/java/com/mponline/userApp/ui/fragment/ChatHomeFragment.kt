package com.mponline.userApp.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.adapter.BannerPagerAdapter
import com.mponline.userApp.ui.adapter.ChatAdapter
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.common_toolbar_normal.view.*
import kotlinx.android.synthetic.main.fragment_chat_home.view.*
import kotlinx.android.synthetic.main.item_chat.view.*

class ChatHomeFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null

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
        if(context!=null){
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.relative_frag?.setOnClickListener {  }
        view?.image_back?.setOnClickListener {  }
        view?.toolbar_title?.text = "Chat"
        //Chat home
        view?.rv_chat_list?.setHasFixedSize(true)
        view?.rv_chat_list?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_chat_list?.adapter = ChatAdapter(
            activity,
            this
        )

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        CommonUtils.printLog("ONRESUME","Called")
        mSwichFragmentListener?.onSwichToolbar(Constants.HIDE_NAV_DRAWER_TOOLBAR,"",null)
        mView?.app_bar_normal?.visibility = View.VISIBLE
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onStop() {
        super.onStop()
        mView?.app_bar_normal?.visibility = View.INVISIBLE
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.rl_chat->{
                mSwichFragmentListener?.onSwitchFragment(Constants.CHAT_MSG_PAGE, Constants.WITH_NAV_DRAWER, null, null)
            }

        }
    }
}