package com.mamits.apnaonlines.user.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.listener.OnItemClickListener
import com.mamits.apnaonlines.user.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.user.model.LocationUtils
import com.mamits.apnaonlines.user.model.response.CategorylistItem
import com.mamits.apnaonlines.user.model.response.GetStoreDetailResponse
import com.mamits.apnaonlines.user.model.response.ProductListItem
import com.mamits.apnaonlines.user.model.response.StoreDetailDataItem
import com.mamits.apnaonlines.user.model.response.StorelistItem
import com.mamits.apnaonlines.user.ui.adapter.ServicesAdapter
import com.mamits.apnaonlines.user.ui.adapter.StoreDetailBannerPagerAdapter
import com.mamits.apnaonlines.user.ui.base.BaseFragment
import com.mamits.apnaonlines.user.util.CommonUtils
import com.mamits.apnaonlines.user.util.Constants
import com.mamits.apnaonlines.user.util.ImageGlideUtils
import com.mamits.apnaonlines.user.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_store_detail.view.btn_location
import kotlinx.android.synthetic.main.fragment_store_detail.view.btn_share
import kotlinx.android.synthetic.main.fragment_store_detail.view.image_store
import kotlinx.android.synthetic.main.fragment_store_detail.view.image_store_status
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_container
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_grey
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_grey_1
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_grey_2
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_grey_3
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_grey_4
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_rating
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_rating_1
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_rating_2
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_rating_3
import kotlinx.android.synthetic.main.fragment_store_detail.view.ll_rating_4
import kotlinx.android.synthetic.main.fragment_store_detail.view.ratingbar_rating
import kotlinx.android.synthetic.main.fragment_store_detail.view.ratingbar_store
import kotlinx.android.synthetic.main.fragment_store_detail.view.relative_frag
import kotlinx.android.synthetic.main.fragment_store_detail.view.rv_services
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_checkout
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_rating
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_store_desc
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_store_location
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_store_name
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_store_status
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_total_users
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_users_1
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_users_2
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_users_3
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_users_4
import kotlinx.android.synthetic.main.fragment_store_detail.view.text_users_5
import kotlinx.android.synthetic.main.fragment_store_detail.view.viewpager_banner_storedetail
import kotlinx.android.synthetic.main.layout_empty.relative_empty
import kotlinx.android.synthetic.main.layout_progress.relative_progress
import java.text.DecimalFormat


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

        view?.btn_share?.setOnClickListener {
            if (mStoreDetailDataItem != null) {
                var url = "https://apnaonlines.com/stores/" + mStoreDetailDataItem?.id

                val intent = Intent(Intent.ACTION_SEND)
                val shareBody = mStoreDetailDataItem?.name + "\n" + url
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(intent, "Select"))
            }
        }

        view?.btn_location?.setOnClickListener {
            if (mStoreDetailDataItem != null) {
                val uri =
                    "http://maps.google.com/maps?q=loc:" + mStoreDetailDataItem?.latitude + "," + mStoreDetailDataItem?.longitude
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps");
                requireContext().startActivity(intent)
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
                    try {
                        callStoreDetail(mStorelistItem?.id!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
        try {
            if (CommonUtils.isOnline(requireActivity())) {
                switchView(3, "")
                var commonRequestObj = getCommonRequestObj(
                    apiKey = getApiKey(),
                    store_id = store_id,
                    latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils?.getCurrentLocation()?.lng!!
                )
                viewModel.getStoreDetail(commonRequestObj).observe(viewLifecycleOwner, Observer {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setDataToUI(mGetStoreDetailResponse: GetStoreDetailResponse) {
        mGetStoreDetailResponse?.let {
            if (it?.data != null && it?.data?.size!! > 0) {
                mView?.run {
                    ImageGlideUtils.loadUrlImage(
                        requireActivity(),
                        it?.data?.get(0)?.storelogo,
                        image_store
                    )
                    text_store_name.text = "${it?.data?.get(0)?.name}"
                    text_store_location.text = "${it?.data?.get(0)?.distance} Km away from you"
                    if (it?.data?.get(0)?.is_available != null && it?.data?.get(0)?.is_available!! == "1") {
                        if (it?.data?.get(0)?.openstatus != null && it?.data?.get(0)?.openstatus!! == "1") {
                            text_store_status.text = "Open"
                            image_store_status.setImageResource(R.drawable.circle_green)
                        } else {
                            text_store_status.text = "Closed"
                            image_store_status.setImageResource(R.drawable.circle_red)
                        }
                    } else {
                        text_store_status.text = "Closed"
                        image_store_status.setImageResource(R.drawable.circle_red)
                    }
                    /* text_store_status.text =
                         if (it?.data?.get(0)?.is_available != null && it?.data?.get(0)?.is_available!! == "1") "Open" else "Closed"
                     image_store_status.setImageResource(
                             if (it?.data?.get(0)?.is_available != null && it?.data?.get(
                                     0
                                 )?.is_available!! == "1"
                             ) R.drawable.circle_green else R.drawable.circle_red
                     )*/
                    if (it?.data?.get(0)?.ratting != null) {
//                        ratingbar_store.rating = it?.data?.get(0)?.ratting?.toFloat()
                        ratingbar_rating.rating = it?.data?.get(0)?.ratting?.toFloat()
//                        text_rating.text = it?.data?.get(0)?.ratting
                        text_total_users.text = "Total ${it?.data?.get(0)?.totalrating} users"
                        var totalRating = 0f

                        if (it?.data?.get(0)?.totalrating != null && it?.data?.get(0)?.totalrating?.isDigitsOnly()!!) {
                            totalRating = it?.data?.get(0)?.totalrating?.toFloat()!!
                        }
                        var maxRatings = 0f
                        if (it?.data?.get(0)?.rating != null) {
                            maxRatings =
                                maxRatings + (it?.data?.get(0)?.rating?.five?.toFloat()!! * 5)
                            val param5 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.five?.toFloat()!!
                            )
                            text_users_5.text = it?.data?.get(0)?.rating?.five + " Rate"
                            val param5grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                totalRating - (it?.data?.get(0)?.rating?.five?.toFloat()!!)
                            )

                            maxRatings =
                                maxRatings + (it?.data?.get(0)?.rating?.four?.toFloat()!! * 4)
                            val param4 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.four?.toFloat()!!
                            )
                            text_users_4.text = it?.data?.get(0)?.rating?.four + " Rate"
                            val param4grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                totalRating - (it?.data?.get(0)?.rating?.four?.toFloat()!!)
                            )

                            maxRatings =
                                maxRatings + (it?.data?.get(0)?.rating?.three?.toFloat()!! * 3)
                            val param3 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.three?.toFloat()!!
                            )
                            text_users_3.text = it?.data?.get(0)?.rating?.three + " Rate"
                            val param3grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                totalRating - (it?.data?.get(0)?.rating?.three?.toFloat()!!)
                            )

                            maxRatings =
                                maxRatings + (it?.data?.get(0)?.rating?.two?.toFloat()!! * 2)
                            val param2 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.two?.toFloat()!!
                            )
                            text_users_2.text = it?.data?.get(0)?.rating?.two + " Rate"
                            val param2grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                totalRating - (it?.data?.get(0)?.rating?.two?.toFloat()!!)
                            )

                            maxRatings =
                                maxRatings + (it?.data?.get(0)?.rating?.one?.toFloat()!! * 1)
                            val param1 = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                it?.data?.get(0)?.rating?.one?.toFloat()!!
                            )
                            text_users_1.text = it?.data?.get(0)?.rating?.one + " Rate"
                            val param1grey = LinearLayout.LayoutParams(
                                0,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                totalRating - (it?.data?.get(0)?.rating?.one?.toFloat()!!)
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

                            //Rating
                            var rating =
                                if (totalRating > 0) (maxRatings / totalRating).toDouble() else 0.0
                            var df = DecimalFormat("#.#");
                            var formattedRating = df.format(rating);

                            text_rating.text = formattedRating
                            ratingbar_store.rating = rating.toFloat()
                            ratingbar_rating.rating = rating.toFloat()
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

                    val adapter =
                        StoreDetailBannerPagerAdapter(
                            activity!!,
                            childFragmentManager,
                            it?.data?.get(0)?.banner_image!!
                        )
                    viewpager_banner_storedetail.adapter = adapter
                    viewpager_banner_storedetail.startAutoScroll(3000)
                }
            }
        }
    }


    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_service -> {
                if (obj is CategorylistItem) {
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