package com.mponline.userApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.LocationUtils
import com.mponline.userApp.model.request.CommonRequestObj
import com.mponline.userApp.model.request.FormDataItem
import com.mponline.userApp.model.request.HeaderInfo
import com.mponline.userApp.model.response.DataItem
import com.mponline.userApp.model.response.OrderDetailItem
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.adapter.CouponsAdapter
import com.mponline.userApp.ui.adapter.NotificationAdapter
import com.mponline.userApp.ui.adapter.OrderDetailAdapter
import com.mponline.userApp.ui.adapter.OrderHistoryAdapter
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.item_coupons.view.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class OffersActivity : BaseActivity(), OnItemClickListener {

    val viewModel: UserListViewModel by viewModels()
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null
    var mType: String = ""

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
                callOfferList()
                toolbar_title.text = "Coupons"
            }
        }
        if (intent?.hasExtra("type")!!) {
            mType = intent?.getStringExtra("type")!!
            if (mOrderHistoryDataItem == null && mType?.equals("offer")) {
                callOfferList()
                toolbar_title.text = "Offers"
            }
        }
    }

    private fun callOfferList() {
        if (CommonUtils.isOnline(this!!)) {
            switchView(3, "")
            var commonRequestObj =
                if (mType?.equals("offer")) CommonRequestObj(
                    headerInfo = HeaderInfo(Authorization = "Bearer "+mPreferenceUtils?.getValue(
                        Constants.USER_TOKEN)),
                    latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils?.getCurrentLocation()?.lng!!
                ) else getCommonRequestObj(
                    apiKey = getApiKey(),
                    latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                    longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                    orderid = mOrderHistoryDataItem?.id!!
                )
            viewModel?.getCouponList(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        rv_offers?.setHasFixedSize(true)
                        rv_offers?.layoutManager =
                            LinearLayoutManager(
                                this@OffersActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        rv_offers?.adapter = CouponsAdapter(
                            this@OffersActivity,
                            this@OffersActivity,
                            mType,
                            data
                        )
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