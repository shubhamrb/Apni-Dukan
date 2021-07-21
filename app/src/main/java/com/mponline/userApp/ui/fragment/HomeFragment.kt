package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnLocationFetchListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.LocationObj
import com.mponline.userApp.model.LocationUtils
import com.mponline.userApp.model.response.GetHomeDataResponse
import com.mponline.userApp.ui.adapter.BannerPagerAdapter
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.adapter.TopProductAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_banner.view.*
import kotlinx.android.synthetic.main.layout_location_fetching.*
import kotlinx.android.synthetic.main.layout_progress.*


@AndroidEntryPoint
class HomeFragment : BaseFragment(), OnItemClickListener, OnLocationFetchListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null

    val viewModel: UserListViewModel by viewModels()
    var isExamFormVisible = false
    var isBottomBannerVisible = false
    var mHandler:Handler? = null

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
        if (context != null) {
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHandler = Handler()
        view?.nestedscroll?.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (!isExamFormVisible) {
                var flag = isViewVisible(view.rv_top_exam_forms)
                CommonUtils.printLog("VIEW_VISIBLE", "${flag}")
                if (flag) {
                    isExamFormVisible = true
                    translateViewToLeftAnim(view?.rv_top_exam_forms)
                }
            }
            if (!isBottomBannerVisible) {
                var flag = isViewVisible(view.viewpager_bottom_banner)
                CommonUtils.printLog("VIEW_VISIBLE2", "${flag}")
                if (flag) {
                    isBottomBannerVisible = true
                    translateViewToLeftAnim(view?.viewpager_bottom_banner)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        callHomeApi()
    }

    override fun onStart() {
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
        super.onStart()
    }

    private fun callHomeApi() {
        if (LocationUtils.getCurrentLocation() == null) {
            mSwichFragmentListener?.onStartLocationAccess(this@HomeFragment)
            //Show waiting
            switchView(4, "")
        } else
            if (CommonUtils.isOnline(activity!!)) {
                switchView(3, "")
                var commonRequestObj = getCommonRequestObj(
                    apiKey = getApiKey(),
                    latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils?.getCurrentLocation()?.lng!!
                )
                viewModel?.getHomeData(commonRequestObj)?.observe(this@HomeFragment, Observer {
                    it?.run {
                        if (status) {
                            switchView(1, "")
                            setDataToUI(this)
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

    fun setDataToUI(mGetHomeDataResponse: GetHomeDataResponse) {
        mGetHomeDataResponse?.let {
            mView?.run {
                //Banner
                if (it?.data?.bannerlist != null) {
                    val adapter =
                        BannerPagerAdapter(activity!!, childFragmentManager, it?.data?.bannerlist!!)
                    viewpager_banner.adapter = adapter
                    dots_indicator.setViewPager(viewpager_banner)
//        indicator_startup.setViewPager(viewpager_startup)
                    viewpager_banner?.clipToPadding = false
                    viewpager_banner?.pageMargin = CommonUtils.convertDpToPixel(activity!!, 15)
                    viewpager_banner?.setPadding(
                        CommonUtils.convertDpToPixel(activity!!, 0),
                        0,
                        CommonUtils.convertDpToPixel(activity!!, 120),
                        0
                    )
                    viewpager_banner.addOnPageChangeListener(object :
                        ViewPager.OnPageChangeListener {

                        override fun onPageScrollStateChanged(state: Int) {

                        }

                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {

                        }

                        override fun onPageSelected(position: Int) {
                            if(mGetHomeDataResponse?.data?.bannerlist!=null && mGetHomeDataResponse?.data?.bannerlist?.size!!>0){

                            }
                        }
                    })
                    viewpager_banner.startAutoScroll(3000)
                    translateViewToLeftAnim(viewpager_banner, 1500)
                }

                //Category List
                if (it?.data?.categorylist != null) {
                    rv_services?.layoutManager =
                        GridLayoutManager(
                            activity, 3
                        )
                    rv_services?.adapter = ServicesAdapter(
                        activity,
                        this@HomeFragment,
                        it?.data?.categorylist!!
                    )
                }

                //Stores
                if (it?.data?.top_storelist != null) {
                    rv_stores?.setHasFixedSize(true)
                    rv_stores?.layoutManager =
                        LinearLayoutManager(
                            activity,
                            RecyclerView.VERTICAL,
                            false
                        )
                    rv_stores?.adapter = StoresAdapter(
                        activity,
                        this@HomeFragment,
                        it?.data?.top_storelist!!
                    )
                }

                //Top Products
                if (it?.data?.productlist != null) {
                    rv_top_exam_forms?.setHasFixedSize(true)
                    rv_top_exam_forms?.layoutManager =
                        LinearLayoutManager(
                            activity,
                            RecyclerView.HORIZONTAL,
                            false
                        )
                    rv_top_exam_forms?.adapter = TopProductAdapter(
                        activity,
                        this@HomeFragment,
                        it?.data?.productlist!!
                    )
                }

                //Nearby Stores
                if (it?.data?.storelist != null) {
                    rv_nearbystores?.setHasFixedSize(true)
                    rv_nearbystores?.layoutManager =
                        LinearLayoutManager(
                            activity,
                            RecyclerView.VERTICAL,
                            false
                        )
                    rv_nearbystores?.adapter = StoresAdapter(
                        activity,
                        this@HomeFragment,
                        it?.data?.storelist!!
                    )
                }

                //Bottom Banner
                if (mGetHomeDataResponse?.data?.bottom_bannerlist != null) {
                    val adapter2 = BannerPagerAdapter(
                        activity!!,
                        childFragmentManager,
                        mGetHomeDataResponse?.data?.bottom_bannerlist!!
                    )
                    viewpager_bottom_banner.adapter = adapter2
                    dots_bottom_indicator.setViewPager(viewpager_bottom_banner)
//        indicator_startup.setViewPager(viewpager_startup)
                    viewpager_bottom_banner?.clipToPadding = false
                    viewpager_bottom_banner?.pageMargin =
                        CommonUtils.convertDpToPixel(activity!!, 15)
                    viewpager_bottom_banner?.setPadding(
                        CommonUtils.convertDpToPixel(activity!!, 0),
                        0,
                        CommonUtils.convertDpToPixel(activity!!, 120),
                        0
                    )
                    viewpager_bottom_banner.addOnPageChangeListener(object :
                        ViewPager.OnPageChangeListener {

                        override fun onPageScrollStateChanged(state: Int) {

                        }

                        override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                        ) {

                        }

                        override fun onPageSelected(position: Int) {
                            if(mGetHomeDataResponse?.data?.bottom_bannerlist!=null && mGetHomeDataResponse?.data?.bottom_bannerlist?.size!!>0){
                            }
                        }
                    })
                    viewpager_bottom_banner.startAutoScroll(3000)
                }
            }
        }
    }


    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.rl_banner -> {
            }
            R.id.cv_service -> {
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.SERVICE_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    obj,
                    null
                )
            }
            R.id.rl_product -> {
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.STORE_PAGE_BY_PROD,
                    Constants.WITH_NAV_DRAWER,
                    obj,
                    null
                )
            }
            R.id.cv_store -> {
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.STORE_DETAIL_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    obj,
                    null
                )
            }
        }
    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    rl_location_fetch?.visibility = View.GONE
                    nestedscroll?.visibility = View.VISIBLE
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    rl_location_fetch?.visibility = View.GONE
                    nestedscroll?.visibility = View.VISIBLE
                }
                2 -> {
                    relative_progress?.visibility = View.GONE
                    rl_location_fetch?.visibility = View.GONE
                }
                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    rl_location_fetch?.visibility = View.GONE
                    nestedscroll?.visibility = View.GONE
                }
                4 -> {
                    relative_progress?.visibility = View.GONE
                    rl_location_fetch?.visibility = View.VISIBLE
                    nestedscroll?.visibility = View.GONE
                }
            }
        }
    }

    private fun isViewVisible(view: View): Boolean {
        val scrollBounds = Rect()
        mView?.nestedscroll?.getDrawingRect(scrollBounds)
        val top = view.y
        val bottom = top + view.height
        return if (scrollBounds.top < top && scrollBounds.bottom > bottom) {
            true //View is visible.
        } else {
            false //View is NOT visible.
        }
    }

    override fun onLocationSuccess(locationObj: LocationObj) {
        if (locationObj != null) {
            callHomeApi()
        }
    }

    override fun onLocationFailure() {

    }
}