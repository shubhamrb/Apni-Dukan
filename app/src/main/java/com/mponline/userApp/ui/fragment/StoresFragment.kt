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
import com.mponline.userApp.model.response.CategorylistItem
import com.mponline.userApp.model.response.ProductListItem
import com.mponline.userApp.model.response.StorelistItem
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_stores.view.*
import kotlinx.android.synthetic.main.fragment_stores.view.ll_container
import kotlinx.android.synthetic.main.fragment_stores.view.rv_stores
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class StoresFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mCategorylistItem: CategorylistItem? = null
    var mSubCategorylistItem: CategorylistItem? = null
    var mProductListItem: ProductListItem? = null

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
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.STORE_DETAIL_PAGE_WITH_PROD,
                    Constants.WITH_NAV_DRAWER,
                    obj,
                    mProductListItem
                )
            }
        }
    }

    private fun callNearByStores() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                latitude = "23.2599",
                longitude = "77.4126"
            )
            viewModel?.getStoreAround(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        setDataToUI(this?.data!!)
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
                latitude = "23.2599",
                longitude = "77.4126",
                category_id = mCategorylistItem?.id!!
            )
            viewModel?.getStoreByCategory(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    if (status) {
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

    private fun callStoreByProduct() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                latitude = "23.2599",
                longitude = "77.4126",
                product_id = mProductListItem?.id!!
            )
            viewModel?.getStoreByProduct(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    if (status) {
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

    fun setDataToUI(data: ArrayList<StorelistItem>) {
        data?.let {
            if (it?.size >= 0) {
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

}