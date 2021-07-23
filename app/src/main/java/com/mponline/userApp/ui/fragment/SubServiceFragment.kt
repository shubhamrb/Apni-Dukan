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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.response.*
import com.mponline.userApp.ui.adapter.ServicesAdapter
import com.mponline.userApp.ui.adapter.BannerPagerAdapter
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.adapter.SubServiceAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_sub_services.view.*
import kotlinx.android.synthetic.main.fragment_sub_services.view.ll_container
import kotlinx.android.synthetic.main.item_sub_store.view.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class SubServiceFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var categoryObj: CategorylistItem? = null
    var subCategoryObj: CategorylistItem? = null
    var getProdByCategory: GetProductByCategoryResponse? = null
    var isFromStore = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_sub_services, container, false)

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

        arguments?.let {
            isFromStore = false
            if (it?.containsKey("obj") && it?.containsKey("obj2")) {
                categoryObj = arguments?.getParcelable<CategorylistItem>("obj")
                subCategoryObj = arguments?.getParcelable<CategorylistItem>("obj2")
                if(subCategoryObj!=null){
                    categoryObj?.let {
                        callSubCategory(it, subCategoryObj!!)
                    }
                    if(categoryObj?.name!=null && subCategoryObj?.name!=null){
                        view?.text_list_title?.text = "${categoryObj?.name} | ${subCategoryObj?.name}"
                    }else{

                    }
                }else{
                    categoryObj?.let {
                        if(!it?.storeId?.isNullOrEmpty()){
                            isFromStore = true
                            callProductsByStore(it?.id, it?.storeId)
                        }
                    }
                }
            }else if(it?.containsKey("obj")) {
                subCategoryObj = arguments?.getParcelable<CategorylistItem>("obj")
                subCategoryObj?.let {
                    if(it?.storeId?.isNullOrEmpty()){
                        callSubCategory(null, subCategoryObj!!)
                    }else{
                        callProductsByStore(it?.id, it?.storeId)
                    }
                }
            } else{

            }
        }


        view?.relative_frag?.setOnClickListener {  }

    }

    private fun callSubCategory(category: CategorylistItem?, subcategory: CategorylistItem) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                category_id = if(category!=null) category?.id!! else "",
                subcategory_id = subcategory?.id!!
            )
            viewModel?.getProductByCategory(commonRequestObj)?.observe(this@SubServiceFragment, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        getProdByCategory = it!!
                        setDataToUI(data?.productList!!)
                    } else {
                        switchView(0, "")
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                           message
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
    private fun callProductsByStore(categoryId:String, storeId:String) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                category = categoryId,
                store_id = storeId
            )
            viewModel?.getProdByStore(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        setDataToUI(data!!)
                    } else {
                        switchView(0, "")
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            message
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

    fun setDataToUI(arraylist:ArrayList<ProductListItem>, isFromStore:Boolean = false) {
        arraylist?.let {
            mView?.rv_sub_service?.setHasFixedSize(true)
            mView?.rv_sub_service?.layoutManager =
                LinearLayoutManager(
                    activity,
                    RecyclerView.VERTICAL,
                    false
                )
            mView?.rv_sub_service?.adapter = SubServiceAdapter(
                activity,
                this,
                it,
                isFromStore
            )
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR,"",null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.cv_sub_service->{
                if(!isFromStore){
                    //Go to storelist
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.STORE_PAGE_BY_PROD,
                        Constants.WITH_NAV_DRAWER,
                        obj, null
                    )
                }else{
                    //Go to storeDetail
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.STORE_DETAIL_PAGE_WITH_PROD,
                        Constants.WITH_NAV_DRAWER,
                        StorelistItem(id = if(subCategoryObj!=null)subCategoryObj?.storeId!! else categoryObj?.storeId!!),
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
        fun newInstance(
            context: Activity,
            subcategory: CategorylistItem?,
            category: CategorylistItem
        ): Fragment {
            val fragment = SubServiceFragment()
            val bundle = Bundle()
            bundle.putParcelable("obj", category)
            bundle.putParcelable("obj2", subcategory)
            fragment.arguments = bundle
            return fragment
        }
    }

}