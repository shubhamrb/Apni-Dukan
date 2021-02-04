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
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_stores.view.*

class HomeFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_home, container, false)

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

        //Banner
        val adapter = BannerPagerAdapter(activity!!, childFragmentManager)
        view?.viewpager_banner.adapter = adapter
        view?.dots_indicator.setViewPager(view?.viewpager_banner)
//        indicator_startup.setViewPager(viewpager_startup)
        view?.viewpager_banner?.clipToPadding = false
        view?.viewpager_banner?.pageMargin = CommonUtils.convertDpToPixel(activity!!, 15)
        view?.viewpager_banner?.setPadding(
            CommonUtils.convertDpToPixel(activity!!, 0),
            0,
            CommonUtils.convertDpToPixel(activity!!, 120),
            0
        )
        view?.viewpager_banner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
            }
        })

        //Service
        view?.rv_services?.layoutManager =
            GridLayoutManager(
                activity, 3
            )
        view?.rv_services?.adapter = ServicesAdapter(
            activity,
            this
        )

        //Stores
        view?.rv_stores?.setHasFixedSize(true)
        view?.rv_stores?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_stores?.adapter = StoresAdapter(
            activity,
            this
        )

        //Nearby Stores
        view?.rv_nearbystores?.setHasFixedSize(true)
        view?.rv_nearbystores?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        view?.rv_nearbystores?.adapter = StoresAdapter(
            activity,
            this
        )

        //Banner
        val adapter2 = BannerPagerAdapter(activity!!, childFragmentManager)
        view?.viewpager_bottom_banner.adapter = adapter2
        view?.dots__bottom_indicator.setViewPager(view?.viewpager_bottom_banner)
//        indicator_startup.setViewPager(viewpager_startup)
        view?.viewpager_bottom_banner?.clipToPadding = false
        view?.viewpager_bottom_banner?.pageMargin = CommonUtils.convertDpToPixel(activity!!, 15)
        view?.viewpager_bottom_banner?.setPadding(
            CommonUtils.convertDpToPixel(activity!!, 0),
            0,
            CommonUtils.convertDpToPixel(activity!!, 120),
            0
        )
        view?.viewpager_bottom_banner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
            }
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.cv_service->{
                mSwichFragmentListener?.onSwitchFragment(Constants.SERVICE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
            }
            R.id.cv_store->{
                mSwichFragmentListener?.onSwitchFragment(Constants.STORE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
            }
        }
    }
}