package com.mponline.userApp.ui.fragment

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
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.LocationUtils
import com.mponline.userApp.model.response.CategorylistItem
import com.mponline.userApp.model.response.FilterDataSelectedObj
import com.mponline.userApp.model.response.ProductListItem
import com.mponline.userApp.model.response.StorelistItem
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_stores.*
import kotlinx.android.synthetic.main.fragment_stores.view.*
import kotlinx.android.synthetic.main.layout_progress.*
import java.util.*

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
        arguments?.let {
            if (it?.containsKey("obj") && it?.containsKey("subobj")) {
                mCategorylistItem = arguments?.getParcelable<CategorylistItem>("obj")
                mSubCategorylistItem = arguments?.getParcelable<CategorylistItem>("subobj")
                callStoreByCategory()
            } else if (it?.containsKey("product")) {
                mProductListItem = arguments?.getParcelable<ProductListItem>("product")
                mProductListItem?.let {
                    callStoreByProduct()
                }
            } else {
                callNearByStores()
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

    private fun callNearByStores() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                longitude = LocationUtils?.getCurrentLocation()?.lng!!
            )
            viewModel?.getStoreAround(commonRequestObj)?.observe(this@StoresFragment, Observer {
                it?.run {
                    if (status) {
                        mStoreList = data
                        if(mStoreList!=null && mStoreList?.size!!>0){
                            text_res_title?.text = "Search Result (${mStoreList?.size})"
                        }
                        switchView(1, "")
                        setDataToUI(this?.data!!, true)
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

    private fun callStoreByCategory() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                category_id = mCategorylistItem?.id!!
            )
            viewModel?.getStoreByCategory(commonRequestObj)?.observe(this@StoresFragment, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        mStoreList = data?.stores!!
                        setDataToUI(this?.data?.stores!!)
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

    private fun callStoreByProduct() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                product_id = mProductListItem?.id!!
            )
            viewModel?.getStoreByProduct(commonRequestObj)?.observe(this@StoresFragment, Observer {
                it?.run {
                    if (status) {
                        mStoreList = data?.stores!!
                        switchView(1, "")
                        setDataToUI(this?.data?.stores!!)
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

    fun setDataToUI(data: ArrayList<StorelistItem>, isNearyby:Boolean = false) {
        data?.let {
            text_res_title?.text = "Search Result (${data?.size})"
            if (it?.size >= 0) {
                if(isNearyby){
                    Collections.sort(data, Comparator { obj1, obj2 ->
                        obj1.distance.compareTo(obj2.distance) // To compare string values
                    })
                }
                view?.rv_stores?.setHasFixedSize(true)
                view?.rv_stores?.layoutManager =
                    LinearLayoutManager(
                        activity,
                        RecyclerView.VERTICAL,
                        false
                    )
                view?.rv_stores?.adapter = StoresAdapter(
                    activity,
                    this,
                    it
                )
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
                    if (it?.distance != null && !it?.distance?.isNullOrEmpty()) {
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
                if (!obj?.mrating?.equals("All")!!) {
                    var rating = 0f
                    if (it?.ratting != null && !it?.ratting?.isNullOrEmpty()) {
                        rating = it?.ratting?.toFloat()
                    }
                    when (obj?.mrating) {
                        "No rating" -> {
                            if (rating !=0f) {
                                filteredList?.remove(it)
                            }
                        }
                        "1 star" -> {
                            if (rating !=1f) {
                                filteredList?.remove(it)
                            }
                        }
                        "2 star" -> {
                            if (rating !=2f) {
                                filteredList?.remove(it)
                            }
                        }
                        "3 star" -> {
                            if (rating !=3f) {
                                filteredList?.remove(it)
                            }
                        }
                        "4 star" -> {
                            if (rating !=4f) {
                                filteredList?.remove(it)
                            }
                        }
                        "5 star" -> {
                            if (rating !=5f) {
                                filteredList?.remove(it)
                            }
                        }
                    }

                }
            }
            setDataToUI(filteredList)
        }
    }

}