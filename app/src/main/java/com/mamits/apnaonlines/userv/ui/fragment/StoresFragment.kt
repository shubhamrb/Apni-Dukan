package com.mamits.apnaonlines.userv.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.model.LocationUtils
import com.mamits.apnaonlines.userv.model.response.CategorylistItem
import com.mamits.apnaonlines.userv.model.response.FilterDataSelectedObj
import com.mamits.apnaonlines.userv.model.response.ProductListItem
import com.mamits.apnaonlines.userv.model.response.StorelistItem
import com.mamits.apnaonlines.userv.ui.adapter.StoresAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseFragment
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_stores.text_res_title
import kotlinx.android.synthetic.main.fragment_stores.view.btn_next
import kotlinx.android.synthetic.main.fragment_stores.view.fab_filter
import kotlinx.android.synthetic.main.fragment_stores.view.ll_container
import kotlinx.android.synthetic.main.fragment_stores.view.relative_frag
import kotlinx.android.synthetic.main.fragment_stores.view.rv_stores
import kotlinx.android.synthetic.main.layout_progress.relative_progress
import java.util.Collections

@AndroidEntryPoint
class StoresFragment : BaseFragment(), OnItemClickListener,
    FilterBottomsheetFragment.FilterListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mCategorylistItem: CategorylistItem? = null
    var mSubCategorylistItem: CategorylistItem? = null
    var mProductListItem: ProductListItem? = null
    var mStoreList: ArrayList<StorelistItem>? = arrayListOf()
    var mFilterDataSelectedObj: FilterDataSelectedObj? = FilterDataSelectedObj()

    private var storesAdapter: StoresAdapter? = null
    private val START_PAGE = 0
    private var CURRENT_PAGE = START_PAGE
    private val LIMIT = 15
    private var isNext = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_stores, container, false)

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
        view?.fab_filter?.setOnClickListener {
            val instance = FilterBottomsheetFragment.newInstance(mFilterDataSelectedObj!!)
            instance.show(childFragmentManager, "Filter")
        }
        view?.rv_stores?.setHasFixedSize(true)
        view?.rv_stores?.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        storesAdapter = StoresAdapter(
            activity,
            this
        )
        view?.rv_stores?.adapter = storesAdapter

        arguments?.let {
            if (it?.containsKey("obj") && it?.containsKey("subobj")) {
                mCategorylistItem = arguments?.getParcelable<CategorylistItem>("obj")
                mSubCategorylistItem = arguments?.getParcelable<CategorylistItem>("subobj")
                callStoreByCategory(CURRENT_PAGE)

                view.btn_next.setOnClickListener {
                    CURRENT_PAGE++
                    callStoreByCategory(CURRENT_PAGE)
                }
            } else if (it?.containsKey("product")) {
                mProductListItem = arguments?.getParcelable<ProductListItem>("product")
                mProductListItem?.let {
                    callStoreByProduct(CURRENT_PAGE)
                }
                view.btn_next.setOnClickListener {
                    CURRENT_PAGE++
                    callStoreByProduct(CURRENT_PAGE)
                }
            } else {
                callNearByStores(CURRENT_PAGE)
                view.btn_next.setOnClickListener {
                    CURRENT_PAGE++
                    callNearByStores(CURRENT_PAGE)
                }
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
        super.onStart()
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_store -> {
                if (mProductListItem != null) {
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.STORE_DETAIL_PAGE_WITH_PROD,
                        Constants.WITH_NAV_DRAWER,
                        obj,
                        mProductListItem
                    )
                } else {
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.STORE_DETAIL_PAGE,
                        Constants.WITH_NAV_DRAWER,
                        obj,
                        null
                    )
                }
            }
        }
    }

    private fun callNearByStores(current_page: Int) {
        try {
            if (CommonUtils.isOnline(requireActivity())) {
                switchView(3, "")
                var commonRequestObj = getCommonRequestObj(
                    apiKey = getApiKey(),
                    latitude = LocationUtils.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils.getCurrentLocation()?.lng!!,
                    start = current_page.toString(),
                    pagelength = LIMIT.toString()
                )
                viewModel.getStoreAround(commonRequestObj).observe(viewLifecycleOwner, Observer {
                    it?.run {
                        if (status) {
                            switchView(1, "")
                            mStoreList?.addAll(it?.data!!)

                            if (it?.next) {
                                view?.btn_next!!.visibility = View.VISIBLE
                            } else {
                                view?.btn_next!!.visibility = View.GONE
                            }
                            setDataToUI(mStoreList!!, true)
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callStoreByCategory(current_page: Int) {
        try {
            if (CommonUtils.isOnline(requireActivity())) {
                switchView(3, "")
                var commonRequestObj = getCommonRequestObj(
                    apiKey = getApiKey(),
                    latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                    category_id = mCategorylistItem?.id!!,
                    start = current_page.toString(),
                    pagelength = LIMIT.toString()
                )
                viewModel?.getStoreByCategory(commonRequestObj)
                    ?.observe(viewLifecycleOwner, Observer {
                        it?.run {
                            if (status) {
                                switchView(1, "")
                                mStoreList?.addAll(it?.data.stores!!)
                                if (it?.data.next) {
                                    view?.btn_next!!.visibility = View.VISIBLE
                                } else {
                                    view?.btn_next!!.visibility = View.GONE
                                }
                                setDataToUI(mStoreList!!, true)
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callStoreByProduct(current_page: Int) {
        try {
            if (CommonUtils.isOnline(requireActivity())) {
                switchView(3, "")
                var commonRequestObj = getCommonRequestObj(
                    apiKey = getApiKey(),
                    latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                    product_id = mProductListItem?.id!!,
                    start = current_page.toString(),
                    pagelength = LIMIT.toString()
                )
                viewModel?.getStoreByProduct(commonRequestObj)
                    .observe(viewLifecycleOwner, Observer {
                        it?.run {
                            try {
                                if (status) {
                                    mStoreList?.addAll(it?.data?.stores!!)
                                    switchView(1, "")
                                    if (it?.data.next) {
                                        view?.btn_next!!.visibility = View.VISIBLE
                                    } else {
                                        view?.btn_next!!.visibility = View.GONE
                                    }
                                    setDataToUI(mStoreList!!, true)
                                } else {
                                    switchView(0, "")
                                    CommonUtils.createSnackBar(
                                        activity?.findViewById(android.R.id.content)!!,
                                        resources?.getString(R.string.no_net)!!
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    })

            } else {
                CommonUtils.createSnackBar(
                    activity?.findViewById(android.R.id.content)!!,
                    resources?.getString(R.string.no_net)!!
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setDataToUI(data: ArrayList<StorelistItem>, isNearyby: Boolean = false) {
        data?.let {

            text_res_title?.text = "Search Result (${data?.size})"
            if (it?.size >= 0) {
                if (isNearyby) {
                    Collections.sort(it, Comparator { obj1, obj2 ->
                        obj1.distance.compareTo(obj2.distance) // To compare string values
                    })
                }
                storesAdapter?.setList(data!!)
            }
        }
    }


    companion object {
        fun newInstance(
            context: Activity,
            category: CategorylistItem,
            subcategory: CategorylistItem
        ): Fragment {
            val fragment = StoresFragment()
            val bundle = Bundle()
            bundle.putParcelable("obj", category)
            bundle.putParcelable("subobj", subcategory)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(
            context: Activity,
            mProductListItem: ProductListItem
        ): Fragment {
            val fragment = StoresFragment()
            val bundle = Bundle()
            bundle.putParcelable("product", mProductListItem)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(
            context: Activity
        ): Fragment {
            val fragment = StoresFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }

    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }

                1 -> {
                    relative_progress?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }

                2 -> {
                    relative_progress?.visibility = View.GONE
                }

                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    ll_container?.visibility = View.GONE
                }
            }
        }
    }

    override fun onApplyFilter(obj: Any?) {
        if (obj != null && obj is FilterDataSelectedObj && mStoreList != null && mStoreList?.size!! > 0) {
            mFilterDataSelectedObj = obj
            var filteredList: ArrayList<StorelistItem> = arrayListOf()
            filteredList?.addAll(mStoreList!!)
            mStoreList?.forEach {
                if (!obj?.mlocation?.equals("All")!!) {
                    var dist = 0f
                    if (it?.distance != null) {
                        dist = it?.distance?.toFloat()
                    }
                    when (obj?.mlocation) {
                        "Within 5km" -> {
                            if (dist > 5) {
                                filteredList?.remove(it)
                            }
                        }

                        "Within 10km" -> {
                            if (dist > 10) {
                                filteredList?.remove(it)
                            }
                        }

                        "Within 15km" -> {
                            if (dist > 15) {
                                filteredList?.remove(it)
                            }
                        }
                    }
                }


                if (!obj?.mrating?.equals("All")!!) {
                    var rating = 0f
                    if (it?.ratting != null && !it?.ratting?.isNullOrEmpty()) {
                        rating = it?.ratting?.toFloat()
                    }
                    when (obj?.mrating) {
                        "No rating" -> {
                            if (rating != 0f) {
                                filteredList?.remove(it)
                            }
                        }

                        "1 star" -> {
                            if (rating > 2f || rating <= 0f) {
                                filteredList?.remove(it)
                            }
                        }

                        "2 star" -> {
                            if (rating < 2f || rating > 2.9f) {
                                filteredList?.remove(it)
                            }
                        }

                        "3 star" -> {
                            if (rating < 3 || rating > 3.9f) {
                                filteredList?.remove(it)
                            }
                        }

                        "4 star" -> {
                            if (rating < 4 || rating > 4.9f) {
                                filteredList?.remove(it)
                            }
                        }

                        "5 star" -> {
                            if (rating != 5f) {
                                filteredList?.remove(it)
                            }
                        }
                    }

                }
            }
            if (!obj?.mprice?.equals("All")!!) {
                when (obj?.mprice) {
                    "Low to high" -> {
                        Collections.sort(filteredList, Comparator { obj1, obj2 ->
                            obj1.price.compareTo(obj2.price) // To compare string values
                        })
                    }

                    "High to low" -> {
                        Collections.sort(filteredList, Comparator { obj1, obj2 ->
                            obj2.price.compareTo(obj1.price) // To compare string values
                        })
                    }
                }
            }
            if (filteredList.size == 0) {
                view?.btn_next!!.visibility = View.GONE
            }
            setDataToUI(filteredList, false)
        }
    }

}