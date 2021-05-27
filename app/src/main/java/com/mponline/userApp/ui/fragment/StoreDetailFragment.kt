package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.response.*
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.ImageGlideUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_store_detail.*
import kotlinx.android.synthetic.main.fragment_store_detail.view.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*


@AndroidEntryPoint
class StoreDetailFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    val viewModel: UserListViewModel by viewModels()
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    var mCategorylistItem: CategorylistItem? = null
    var mStorelistItem: StorelistItem? = null
    var mSubCategorylistItem: CategorylistItem? = null
    var mStoreDetailDataItem: StoreDetailDataItem? = null
    var mProductListItem: ProductListItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_store_detail, container, false)

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
        mView = view
        view?.relative_frag?.setOnClickListener {

        }

        view?.text_checkout?.setOnClickListener {
            if (mProductListItem != null) {
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.INSTRUCTION_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    mStoreDetailDataItem,
                    mProductListItem
                )
            }
        }

        arguments?.let {
            if (it?.containsKey("obj") && it?.containsKey("subobj")) {
                mCategorylistItem = arguments?.getParcelable<CategorylistItem>("obj")
                mSubCategorylistItem = arguments?.getParcelable<CategorylistItem>("subobj")
            }
            if (it?.containsKey("store")) {
                mStorelistItem = arguments?.getParcelable<StorelistItem>("store")
                mStorelistItem?.let {
                    callStoreDetail(mStorelistItem?.id!!)
                }
            }
            if (it?.containsKey("prod")) {
                mProductListItem = arguments?.getParcelable<ProductListItem>("prod")
                mView?.text_checkout?.visibility = View.VISIBLE
            } else {
                mView?.text_checkout?.visibility = View.GONE
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
    }

    private fun callStoreDetail(store_id: String) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                store_id = store_id,
                latitude = "23.2599",
                longitude = "77.4126"
            )
            viewModel?.getStoreDetail(commonRequestObj)?.observe(activity!!, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        if (this?.data != null && this?.data?.size!! > 0) {
                            mStoreDetailDataItem = this?.data?.get(0)!!
                        }
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

    fun setDataToUI(mGetStoreDetailResponse: GetStoreDetailResponse) {
        mGetStoreDetailResponse?.let {
            if (it?.data != null && it?.data?.size!! > 0) {
                mView?.run {
                    ImageGlideUtils.loadUrlImage(
                        activity!!,
                        it?.data?.get(0)?.storelogo,
                        image_store
                    )
                    text_store_name.text = "${it?.data?.get(0)?.name}"
                    text_store_location.text = "${it?.data?.get(0)?.distance} Km away from you"
                    text_store_status.text =
                        if (it?.data?.get(0)?.is_available != null && it?.data?.get(0)?.is_available!! == "1") "Open" else "Closed"
                    image_store_status.setImageResource(
                        if (it?.data?.get(0)?.is_available != null && it?.data?.get(
                                0
                            )?.is_available!! == "1"
                        ) R.drawable.circle_green else R.drawable.circle_red
                    )
                    if (it?.data?.get(0)?.ratting != null) {
                        ratingbar_store.rating = it?.data?.get(0)?.ratting?.toFloat()
                        ratingbar_rating.rating = it?.data?.get(0)?.ratting?.toFloat()
                        text_rating.text = it?.data?.get(0)?.ratting
                        text_total_users.text = it?.data?.get(0)?.totalrating
                        if (it?.data?.get(0)?.rating != null) {
                            val param5 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.five?.toFloat()!!
                            )
                            text_users_5.text = it?.data?.get(0)?.rating?.five+" Rate"
                            val param5grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                10 - (it?.data?.get(0)?.rating?.five?.toFloat()!!)
                            )
                            val param4 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.four?.toFloat()!!
                            )
                            text_users_4.text = it?.data?.get(0)?.rating?.four+" Rate"
                            val param4grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                10 - (it?.data?.get(0)?.rating?.four?.toFloat()!!)
                            )
                            val param3 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.three?.toFloat()!!
                            )
                            text_users_3.text = it?.data?.get(0)?.rating?.three+" Rate"
                            val param3grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                10 - (it?.data?.get(0)?.rating?.three?.toFloat()!!)
                            )
                            val param2 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.two?.toFloat()!!
                            )
                            text_users_2.text = it?.data?.get(0)?.rating?.two+" Rate"
                            val param2grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                10 - (it?.data?.get(0)?.rating?.two?.toFloat()!!)
                            )
                            val param1 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.one?.toFloat()!!
                            )
                            text_users_1.text = it?.data?.get(0)?.rating?.one+" Rate"
                            val param1grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                10 - (it?.data?.get(0)?.rating?.one?.toFloat()!!)
                            )
                            ll_rating.setLayoutParams(param5)
                            ll_grey.setLayoutParams(param5grey)
                            ll_rating_4.setLayoutParams(param4)
                            ll_grey_4.setLayoutParams(param4grey)
                            ll_rating_3.setLayoutParams(param3)
                            ll_grey_3.setLayoutParams(param3grey)
                            ll_rating_2.setLayoutParams(param2)
                            ll_grey_2.setLayoutParams(param2grey)
                            ll_rating_1.setLayoutParams(param1)
                            ll_grey_1.setLayoutParams(param1grey)
                        }
                    }
                    text_store_desc.text = Html.fromHtml(it?.data?.get(0)?.description)

                    //Services
                    if (it?.data?.get(0)?.category != null && it?.data?.get(0)?.category?.size > 0) {
                        view?.rv_services?.layoutManager =
                            GridLayoutManager(
                                activity, 3
                            )
                        view?.rv_services?.adapter = ServicesAdapter(
                            activity,
                            this@StoreDetailFragment,
                            it?.data?.get(0)?.category
                        )
                    }

                    //Rating
                    text_rating.text = it?.data?.get(0)?.ratting
                }
            }
        }
    }


    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_service -> {
                if(obj is CategorylistItem){
                    obj?.storeId = mStoreDetailDataItem?.id!!
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.SUB_SERVICE_PAGE,
                        Constants.WITH_NAV_DRAWER,
                        null,
                        obj
                    )
                }
            }
        }
    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    ll_container?.visibility = View.GONE
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }
                2 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                }
                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.GONE
                }
            }
        }
    }

    companion object {

        //Not used
        fun newInstance(
            context: Activity,
            category: CategorylistItem,
            subcategory: CategorylistItem,
            mStorelistItem: StorelistItem,
            store_id: String
        ): Fragment {
            val fragment = StoreDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("obj", category)
            bundle.putParcelable("subobj", subcategory)
            bundle.putParcelable("store", mStorelistItem)
            bundle.putString("id", store_id)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(
            context: Activity,
            mStorelistItem: StorelistItem,
            mProductListItem: ProductListItem
        ): Fragment {
            val fragment = StoreDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("store", mStorelistItem)
            bundle.putParcelable("prod", mProductListItem)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(
            context: Activity,
            mStorelistItem: StorelistItem
        ): Fragment {
            val fragment = StoreDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("store", mStorelistItem)
            fragment.arguments = bundle
            return fragment
        }

    }

}