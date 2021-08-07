package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.PrePlaceOrderPojo
import com.mponline.userApp.model.response.*
import com.mponline.userApp.ui.adapter.CustomeSpinnerAdapter
import com.mponline.userApp.ui.adapter.InstructionAdapter
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_instruction.view.*
import kotlinx.android.synthetic.main.fragment_instruction.view.relative_frag
import kotlinx.android.synthetic.main.fragment_instruction.view.spn_opt
import kotlinx.android.synthetic.main.fragment_instruction.view.text_spn_title
import kotlinx.android.synthetic.main.item_spinner.view.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class InstructionFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    val viewModel: UserListViewModel by viewModels()
    var productListItem: ProductListItem? = null
    var storeDetailDataItem: StoreDetailDataItem? = null
    var mGetProductDetailResponse: GetProductDetailResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_instruction, container, false)

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

        arguments?.let {
            if (it?.containsKey("obj") && it?.containsKey("obj2")) {
                storeDetailDataItem = it?.getParcelable("obj")
                productListItem = it?.getParcelable("obj2")
                callProductDetail(
                    storeId = storeDetailDataItem?.id!!,
                    productId = productListItem?.id!!
                )
            }
        }

        view?.relative_frag?.setOnClickListener {

        }
        view?.text_proceed?.setOnClickListener {
            var mPrePlaceOrderPojo: PrePlaceOrderPojo = PrePlaceOrderPojo(
                storeDetailDataItem = storeDetailDataItem!!,
                mGetProductDetailResponse = mGetProductDetailResponse!!
            )
            mSwichFragmentListener?.onSwitchFragment(
                Constants.CUSTOM_FOEMS_PAGE,
                Constants.WITH_NAV_DRAWER,
                mPrePlaceOrderPojo,
                null
            )
        }
        //Step
        view?.rv_steps?.layoutManager =
            LinearLayoutManager(
                activity, RecyclerView.VERTICAL, false
            )
        view?.rv_steps?.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        view?.rv_steps?.adapter = InstructionAdapter(
            activity,
            this
        )

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR, "", null)
    }

    private fun callProductDetail(storeId: String, productId: String) {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                store_id = storeId,
                product_id = productId
            )
            viewModel?.getProductDetail(commonRequestObj)
                ?.observe(this@InstructionFragment, Observer {
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

    fun setDataToUI(getStoreDetailResponse: GetProductDetailResponse) {
        getStoreDetailResponse?.let {
            mGetProductDetailResponse = it
            mView?.run {
                text_title.text = Html.fromHtml(it?.data?.get(0)?.description)
                text_desc.text = Html.fromHtml(it?.data?.get(0)?.shortDescription)
                if (it?.data?.get(0)?.product_type?.equals("1")!!) {
                    var dropdownItemList = arrayListOf<String>()
                    var isAllNullPrice = true
                    it?.data?.get(0)?.variation_price?.forEach {
                        if(it?.value!=null){
                            isAllNullPrice = false
                        }
                        dropdownItemList?.add("${it?.name} ("+activity?.resources?.getString(R.string.rs)+"${if(it?.value!=null) it?.value else "Best price after acceptance"})")
                    }
                    if(isAllNullPrice){
                        text_price.visibility = View.VISIBLE
                        spn_opt.visibility = View.GONE
                        text_spn_title.text = "Price"
                        text_price.text = "After order Confirmation Get Best Price"
                        mGetProductDetailResponse?.data?.get(0)?.selectedPrice = "0.00"
                    }else{
                        text_price.visibility = View.GONE
                        spn_opt.visibility = View.VISIBLE
                        text_spn_title.text = "Select Price"
                        val adapter = ArrayAdapter(
                            context!!,
                            android.R.layout.simple_spinner_item,
                            dropdownItemList!!
                        )/*CustomeSpinnerAdapter(context!!, dropdownItemList)*/
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spn_opt.adapter = adapter
                        spn_opt.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    pos: Int,
                                    id: Long
                                ) {
                                    if(mGetProductDetailResponse?.data?.get(0)?.variation_price?.get(pos)?.value!=null){
                                        mGetProductDetailResponse?.data?.get(0)?.selectedPrice = mGetProductDetailResponse?.data?.get(0)?.variation_price?.get(pos)?.value
                                    }else{
                                        mGetProductDetailResponse?.data?.get(0)?.selectedPrice = "0.00"
                                    }
                                    CommonUtils.printLog(
                                        "ANS_DROPDOWN_LISTENER",
                                        "${ mGetProductDetailResponse?.data?.get(0)?.selectedPrice} -- > ${dropdownItemList?.get(pos)} ${pos}"
                                    )
                                }
                            }
                    }
                } else {
                    text_price.visibility = View.VISIBLE
                    spn_opt.visibility = View.GONE
                    text_spn_title.text = "Price"
                    text_price.text =
                        if (it?.data?.get(0)?.price?.isNullOrEmpty()!! || it?.data?.get(0)?.price == "0.00") "After order Confirmation Get Best Price" else it?.data?.get(
                            0
                        )?.price
                }
            }
        }
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when (view?.id) {
            R.id.cv_service -> {
                mSwichFragmentListener?.onSwitchFragment(
                    Constants.SUB_SERVICE_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    null,
                    null
                )
            }

        }
    }

    companion object {
        fun newInstance(
            context: Activity,
            mStorelistItem: StoreDetailDataItem,
            mProductListItem: ProductListItem
        ): Fragment {
            val fragment = InstructionFragment()
            val bundle = Bundle()
            bundle.putParcelable("obj", mStorelistItem)
            bundle.putParcelable("obj2", mProductListItem)
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