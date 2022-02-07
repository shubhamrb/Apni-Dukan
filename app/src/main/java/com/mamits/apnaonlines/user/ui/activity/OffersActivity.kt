package com.mamits.apnaonlines.user.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.listener.OnItemClickListener
import com.mamits.apnaonlines.user.model.LocationUtils
import com.mamits.apnaonlines.user.model.request.CommonRequestObj
import com.mamits.apnaonlines.user.model.request.HeaderInfo
import com.mamits.apnaonlines.user.model.response.DataItem
import com.mamits.apnaonlines.user.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.user.ui.adapter.CouponsAdapter
import com.mamits.apnaonlines.user.ui.adapter.StoresAdapter
import com.mamits.apnaonlines.user.ui.base.BaseActivity
import com.mamits.apnaonlines.user.util.CommonUtils
import com.mamits.apnaonlines.user.util.Constants
import com.mamits.apnaonlines.user.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.fragment_stores.view.*
import kotlinx.android.synthetic.main.item_coupons.view.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class OffersActivity : BaseActivity(), OnItemClickListener {

    val viewModel: UserListViewModel by viewModels()
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null
    var mType: String = ""
    private val START_PAGE = 0
    private var CURRENT_PAGE = START_PAGE
    private val LIMIT = 15
    private var mAdapter: CouponsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)
        toolbar_title.text = ""
        image_back.setOnClickListener {
            finish()
        }

        if (intent?.hasExtra("order")!!) {
            mOrderHistoryDataItem = intent?.getParcelableExtra("order")
            if (mOrderHistoryDataItem != null) {
                callOfferList(CURRENT_PAGE)
                toolbar_title.text = "Coupons"
            }
        }
        if (intent?.hasExtra("type")!!) {
            mType = intent?.getStringExtra("type")!!
            if (mOrderHistoryDataItem == null && mType?.equals("offer")) {
                callOfferList(CURRENT_PAGE)
                toolbar_title.text = "Offers"
            }
        }
        rv_offers?.setHasFixedSize(true)
        rv_offers?.layoutManager =
            LinearLayoutManager(
                this@OffersActivity,
                RecyclerView.VERTICAL,
                false
            )
        mAdapter=CouponsAdapter(
            this@OffersActivity,
            this@OffersActivity,
            mType
        )
        rv_offers?.adapter = mAdapter
        next_btn.setOnClickListener {
            CURRENT_PAGE++
            callOfferList(CURRENT_PAGE)
        }

    }

    private fun callOfferList(current_page: Int) {
        if (CommonUtils.isOnline(this!!)) {
            switchView(3, "")
            var commonRequestObj =
                if (mType?.equals("offer")) CommonRequestObj(
                    headerInfo = HeaderInfo(Authorization = "Bearer "+mPreferenceUtils?.getValue(
                        Constants.USER_TOKEN)),
                    latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                    start = current_page.toString(),
                    pagelength = LIMIT.toString()
                ) else getCommonRequestObj(
                    apiKey = getApiKey(),
                    latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                    orderid = mOrderHistoryDataItem?.id!!,
                    start = current_page.toString(),
                    pagelength = LIMIT.toString()
                )
            viewModel?.getCouponList(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        if (it?.next){
                            next_btn!!.visibility=View.VISIBLE
                        }else{
                            next_btn!!.visibility=View.GONE
                        }
                        mAdapter?.setList(data!!)
                    } else {
                        switchView(0, "")
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            message
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.text_coupon_title->{
                if (obj is DataItem && obj?.store_id!=null && !obj?.store_id?.isNullOrEmpty()) {
                    var intent: Intent = Intent()
                    intent.putExtra("from", "offer")
                    intent.putExtra("id", obj?.store_id)
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    var intent: Intent = Intent()
                    intent.putExtra("from", "NOTI_nearby")
                    intent.putExtra("id", "")
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
            else->{
                if (obj is DataItem) {
                    var intent: Intent = Intent()
                    intent?.putExtra("data", obj)
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
    }

    fun switchView(i: Int, msg: String) {
        when (i) {
            0 -> {
                relative_progress?.visibility = View.GONE
                ll_container?.visibility = View.GONE
                relative_empty?.visibility = View.VISIBLE
            }
            1 -> {
                relative_progress?.visibility = View.GONE
                ll_container?.visibility = View.VISIBLE
                relative_empty?.visibility = View.GONE
            }
            2 -> {
                ll_container?.visibility = View.GONE
                relative_progress?.visibility = View.GONE
                relative_empty?.visibility = View.VISIBLE
            }
            3 -> {
                relative_empty?.visibility = View.GONE
                relative_progress?.visibility = View.VISIBLE
                ll_container?.visibility = View.GONE
            }
        }
    }

}