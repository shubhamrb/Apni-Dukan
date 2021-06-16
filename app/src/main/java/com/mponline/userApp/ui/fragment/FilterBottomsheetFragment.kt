package com.mponline.userApp.ui.fragment

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.request.UserAuthRequestObj
import com.mponline.userApp.model.response.FilterDataSelectedObj
import com.mponline.userApp.ui.activity.MainActivity
import com.mponline.userApp.ui.activity.RegisterActivity
import com.mponline.userApp.ui.adapter.FilterAdapter
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.PreferenceUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_bottom_filter.*


@AndroidEntryPoint
class FilterBottomsheetFragment : BottomSheetDialogFragment(), OnItemClickListener {
    private var mListener: FilterListener? = null
    val viewModel: UserListViewModel by viewModels()
    var mPreferenceUtils: PreferenceUtils = PreferenceUtils()
    var mUserMobile = ""
    var progressDialog: ProgressDialog? = null
    var locationList:ArrayList<String> = arrayListOf()
    var priceList:ArrayList<String> = arrayListOf()
    var ratingList:ArrayList<String> = arrayListOf()
    var mFilterDataSelectedObj: FilterDataSelectedObj? = FilterDataSelectedObj()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initProgressDialog()
        locationList?.add("All")
        locationList?.add("Within 5km")
        locationList?.add("Within 10km")
        locationList?.add("Within 15km")

        priceList?.add("All")
        priceList?.add("Low to high")
        priceList?.add("High to low")

        ratingList?.add("All")
        ratingList?.add("No rating")
        ratingList?.add("1 star")
        ratingList?.add("2 star")
        ratingList?.add("3 star")
        ratingList?.add("4 star")
        ratingList?.add("5 star")

        arguments?.let {

        }
        view?.run {

            text_apply_filter?.setOnClickListener {
                if(mFilterDataSelectedObj!=null){
                    mListener?.onApplyFilter(mFilterDataSelectedObj)
                    dismiss()
                }
            }
            image_filter_cancel?.setOnClickListener {
                dismiss()
            }

            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            rv_location_filter.layoutManager = layoutManager
            rv_location_filter.adapter =
                FilterAdapter(
                    context,
                    this@FilterBottomsheetFragment,
                    "location",
                    locationList
                )

            val layoutManager2 = FlexboxLayoutManager(context)
            layoutManager2.flexDirection = FlexDirection.ROW
            layoutManager2.justifyContent = JustifyContent.FLEX_START
            rv_price_filter.layoutManager = layoutManager2
            rv_price_filter.adapter =
                FilterAdapter(
                    context,
                    this@FilterBottomsheetFragment,
                    "price",
                    priceList
                )

            val layoutManager3 = FlexboxLayoutManager(context)
            layoutManager3.flexDirection = FlexDirection.ROW
            layoutManager3.justifyContent = JustifyContent.FLEX_START
            rv_rating_filter.layoutManager = layoutManager3
            rv_rating_filter.adapter =
                FilterAdapter(
                    context,
                    this@FilterBottomsheetFragment,
                    "rating",
                    ratingList
                )
        }
    }

    private fun initProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setMessage(getString(R.string.please_wait))
        }
    }

    fun progressDialogShow() {
        try {
            if (progressDialog == null) {
                return
            }

            if (!progressDialog!!.isShowing) {
                progressDialog!!.setCancelable(false)
                progressDialog!!.setCanceledOnTouchOutside(false)
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            e?.printStackTrace()
        }
    }

    fun getApiKey():String{
        return Constants.DUMMY_API_KEY
    }

    fun progressDialogDismiss() {
        if (progressDialog == null) {
            return
        }
        if (progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as FilterListener
        } else {
            mListener = context as FilterListener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface FilterListener {
        fun onApplyFilter(obj:Any?)
    }


    companion object {
        fun newInstance(mobile:String): FilterBottomsheetFragment =
            FilterBottomsheetFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }

    override fun onClick(pos: Int, view: View, obj: Any?, type:String) {
        if(obj is String){
            when(type){
                "location"->{
                    mFilterDataSelectedObj?.mlocation = obj
                }
                "price"->{
                    mFilterDataSelectedObj?.mprice = obj
                }
                "rating"->{
                    mFilterDataSelectedObj?.mrating = obj
                }
            }
        }
    }

}
