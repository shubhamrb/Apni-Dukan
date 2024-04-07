package com.mamits.apnaonlines.userv.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.model.LocationUtils
import com.mamits.apnaonlines.userv.model.response.CategorylistItem
import com.mamits.apnaonlines.userv.model.response.GetStoreDetailResponse
import com.mamits.apnaonlines.userv.model.response.ProductListItem
import com.mamits.apnaonlines.userv.model.response.StoreDetailDataItem
import com.mamits.apnaonlines.userv.model.response.StorelistItem
import com.mamits.apnaonlines.userv.ui.adapter.ServicesAdapter
import com.mamits.apnaonlines.userv.ui.adapter.StoreDetailBannerPagerAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseFragment
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.util.ImageGlideUtils
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_store_detail.view.btn_location
import kotlinx.android.synthetic.main.fragment_store_detail.view.btn_share
import kotlinx.android.synthetic.main.fragment_store_detail.view.image_qr_code
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
import kotlinx.android.synthetic.main.fragment_store_detail.view.rl_call
import kotlinx.android.synthetic.main.fragment_store_detail.view.rl_whatsapp
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
    var mCategorylistItem: StoreDetailDataItem? = null
    var mStorelistItem: StorelistItem? = null
    var mSubCategorylistItem: StoreDetailDataItem? = null
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

                /*
                    Download ApnaOnlines App Now!
                    Vendor :  rajpoot
                    Vendor ID :  8956
                    App Link
                */

                val url =
                    "https://play.google.com/store/apps/details?id=com.mamits.apnaonlines.userv"

                val intent = Intent(Intent.ACTION_SEND)
                val shareBody =
                    "Download ApnaOnlines App Now!" + "\n" + "Vendor :  " + mStorelistItem?.name + "\n" +
                            "Vendor ID :  " + mStorelistItem?.vendor_code + "\n" + url
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

        view.rl_call?.setOnClickListener {
            if (mStoreDetailDataItem != null && !mStoreDetailDataItem?.whatsapp_no.equals("")) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${mStoreDetailDataItem?.whatsapp_no}")
                activity?.startActivity(intent)
            }
        }

        view.rl_whatsapp?.setOnClickListener {
            if (mStoreDetailDataItem != null && !mStoreDetailDataItem?.whatsapp_no.equals("")) {
                val url = "https://api.whatsapp.com/send?phone=${
                    if (mStoreDetailDataItem?.whatsapp_no?.startsWith("+91")!!) mStoreDetailDataItem?.whatsapp_no!! else "+91" + mStoreDetailDataItem?.whatsapp_no!!
                }"
                try {
                    val pm: PackageManager = activity?.packageManager!!
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    activity?.startActivity(i)
                } catch (e: PackageManager.NameNotFoundException) {
                    activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            }
        }

        arguments?.let {
            if (it?.containsKey("obj") && it?.containsKey("subobj")) {
                mCategorylistItem = arguments?.getParcelable<StoreDetailDataItem>("obj")
                mSubCategorylistItem = arguments?.getParcelable<StoreDetailDataItem>("subobj")
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
                val commonRequestObj = getCommonRequestObj(
                    apiKey = getApiKey(),
                    latitude = LocationUtils.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils.getCurrentLocation()?.lng!!,
                    store_id = store_id
                )
                viewModel.getStoreDetail(commonRequestObj).observe(viewLifecycleOwner, Observer {
                    it?.run {
                        if (status) {
                            switchView(1, "")
                            if (this.data != null && this.data.isNotEmpty()) {
                                mStoreDetailDataItem = this.data[0]
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
        if (mGetStoreDetailResponse.data != null && mGetStoreDetailResponse.data.size!! > 0) {
            ImageGlideUtils.loadUrlImage(
                requireActivity(),
                mGetStoreDetailResponse?.data?.get(0)?.storelogo,
                mView?.image_store!!
            )

            if (mGetStoreDetailResponse.data[0].qrcode != "") {
                ImageGlideUtils.loadUrlImage(
                    requireActivity(),
                    mGetStoreDetailResponse.data[0].qrcode,
                    mView?.image_qr_code!!
                )
            }

            mView?.text_store_name?.text = mGetStoreDetailResponse.data.get(0).name
            mView?.text_store_location!!.text =
                "${mGetStoreDetailResponse?.data?.get(0)?.distance} Km away from you"
            if (mGetStoreDetailResponse.data.get(0).is_available != null && mGetStoreDetailResponse?.data.get(
                    0
                ).is_available!! == "1"
            ) {
                if (mGetStoreDetailResponse?.data.get(0).openstatus != null && mGetStoreDetailResponse?.data?.get(
                        0
                    )?.openstatus!! == "1"
                ) {
                    mView?.text_store_status?.text = "Open"
                    mView?.image_store_status?.setImageResource(R.drawable.circle_green)
                } else {
                    mView?.text_store_status?.text = "Closed"
                    mView?.image_store_status?.setImageResource(R.drawable.circle_red)
                }
            } else {
                mView?.text_store_status?.text = "Closed"
                mView?.image_store_status?.setImageResource(R.drawable.circle_red)
            }

            if (mGetStoreDetailResponse.data.get(0).ratting != null) {
                //                        ratingbar_store.rating = it?.data?.get(0)?.ratting?.toFloat()
                mView?.ratingbar_rating?.rating =
                    mGetStoreDetailResponse?.data?.get(0)?.ratting?.toFloat()
                //                        text_rating.text = it?.data?.get(0)?.ratting
                mView?.text_total_users?.text =
                    "Total ${mGetStoreDetailResponse?.data?.get(0)?.totalrating} users"
                var totalRating = 0f

                if (mGetStoreDetailResponse?.data?.get(0)?.totalrating != null && mGetStoreDetailResponse?.data?.get(
                        0
                    )?.totalrating?.isDigitsOnly()!!
                ) {
                    totalRating = mGetStoreDetailResponse?.data?.get(0)?.totalrating?.toFloat()!!
                }
                var maxRatings = 0f
                if (mGetStoreDetailResponse?.data?.get(0)?.rating != null) {
                    maxRatings =
                        maxRatings + (mGetStoreDetailResponse?.data?.get(0)?.rating?.five?.toFloat()!! * 5)
                    val param5 = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.five?.toFloat()!!
                    )
                    mView?.text_users_5?.text =
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.five + " Rate"
                    val param5grey = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        totalRating - (mGetStoreDetailResponse?.data?.get(0)?.rating?.five?.toFloat()!!)
                    )

                    maxRatings =
                        maxRatings + (mGetStoreDetailResponse?.data?.get(0)?.rating?.four?.toFloat()!! * 4)
                    val param4 = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.four?.toFloat()!!
                    )
                    mView?.text_users_4?.text =
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.four + " Rate"
                    val param4grey = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        totalRating - (mGetStoreDetailResponse?.data?.get(0)?.rating?.four?.toFloat()!!)
                    )

                    maxRatings =
                        maxRatings + (mGetStoreDetailResponse?.data?.get(0)?.rating?.three?.toFloat()!! * 3)
                    val param3 = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.three?.toFloat()!!
                    )
                    mView?.text_users_3?.text =
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.three + " Rate"
                    val param3grey = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        totalRating - (mGetStoreDetailResponse?.data?.get(0)?.rating?.three?.toFloat()!!)
                    )

                    maxRatings =
                        maxRatings + (mGetStoreDetailResponse?.data?.get(0)?.rating?.two?.toFloat()!! * 2)
                    val param2 = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.two?.toFloat()!!
                    )
                    mView?.text_users_2?.text =
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.two + " Rate"
                    val param2grey = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        totalRating - (mGetStoreDetailResponse?.data?.get(0)?.rating?.two?.toFloat()!!)
                    )

                    maxRatings =
                        maxRatings + (mGetStoreDetailResponse?.data?.get(0)?.rating?.one?.toFloat()!! * 1)
                    val param1 = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.one?.toFloat()!!
                    )
                    mView?.text_users_1?.text =
                        mGetStoreDetailResponse?.data?.get(0)?.rating?.one + " Rate"
                    val param1grey = LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        totalRating - (mGetStoreDetailResponse?.data?.get(0)?.rating?.one?.toFloat()!!)
                    )
                    mView?.ll_rating?.setLayoutParams(param5)
                    mView?.ll_grey?.setLayoutParams(param5grey)
                    mView?.ll_rating_4?.setLayoutParams(param4)
                    mView?.ll_grey_4?.setLayoutParams(param4grey)
                    mView?.ll_rating_3?.setLayoutParams(param3)
                    mView?.ll_grey_3?.setLayoutParams(param3grey)
                    mView?.ll_rating_2?.setLayoutParams(param2)
                    mView?.ll_grey_2?.setLayoutParams(param2grey)
                    mView?.ll_rating_1?.setLayoutParams(param1)
                    mView?.ll_grey_1?.setLayoutParams(param1grey)

                    //Rating
                    var rating =
                        if (totalRating > 0) (maxRatings / totalRating).toDouble() else 0.0
                    var df = DecimalFormat("#.#");
                    var formattedRating = df.format(rating);

                    mView?.text_rating?.text = formattedRating
                    mView?.ratingbar_store?.rating = rating.toFloat()
                    mView?.ratingbar_rating?.rating = rating.toFloat()
                }
            }
            mView?.text_store_desc?.text =
                Html.fromHtml(mGetStoreDetailResponse?.data?.get(0)?.description)

            //Services
            if (mGetStoreDetailResponse?.data?.get(0)?.category != null && mGetStoreDetailResponse?.data?.get(
                    0
                )?.category?.size > 0
            ) {
                view?.rv_services?.layoutManager =
                    GridLayoutManager(
                        activity, 3
                    )
                view?.rv_services?.adapter = ServicesAdapter(
                    activity,
                    this@StoreDetailFragment,
                    mGetStoreDetailResponse?.data?.get(0)?.category
                )
            }

            val adapter =
                StoreDetailBannerPagerAdapter(
                    requireActivity(),
                    childFragmentManager,
                    mGetStoreDetailResponse?.data?.get(0)?.banner_image!!
                )
            mView?.viewpager_banner_storedetail?.adapter = adapter
            mView?.viewpager_banner_storedetail?.startAutoScroll(3000)

        }
    }


    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_service -> {
                if (obj is CategorylistItem) {
                    obj.storeId = mStoreDetailDataItem?.id!!
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.SUB_SERVICE_PAGE,
                        Constants.WITH_NAV_DRAWER,
                        mStoreDetailDataItem,
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
            category: StoreDetailDataItem,
            subcategory: StoreDetailDataItem,
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