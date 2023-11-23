package com.mamits.apnaonlines.userv.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.request.CommonRequestObj
import com.mamits.apnaonlines.userv.model.request.HeaderInfo
import com.mamits.apnaonlines.userv.model.response.DataItem
import com.mamits.apnaonlines.userv.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.userv.ui.adapter.CouponsAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseActivity
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.activity_offers.ll_container
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.fragment_change_pwd.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class ChangePwdActivity : BaseActivity(), OnItemClickListener {

    val viewModel: UserListViewModel by viewModels()
    var mOrderHistoryDataItem: OrderHistoryDataItem? = null
    var mType: String = ""
    var mMobile: String = ""
    private var offerAdapter: CouponsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_change_pwd)
        toolbar_title.text = "Update Password"
        image_back.setOnClickListener {
            finish()
        }
        if(intent?.hasExtra("mobile")!!){
            mMobile = intent.getStringExtra("mobile")!!
            edt_old_pwd.visibility = View.GONE
        }

        text_change_pwd.setOnClickListener {
            if(edt_new_pwd1.text.toString()?.isNullOrEmpty()){
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!,
                    "Please enter new password"
                )
            }else if(edt_confirm_pwd.text.toString()?.isNullOrEmpty()){
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!,
                    "Please confirm new password"
                )
            }else if(!edt_confirm_pwd.text.toString()?.equals(edt_new_pwd1.text.toString()!!)!!){
                CommonUtils.createSnackBar(
                    findViewById(android.R.id.content)!!,
                    "Password didn't match"
                )
            }else {
                callChangePwd()
            }
        }


    }

    private fun callChangePwd() {
        if (CommonUtils.isOnline(this)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                mobile = mMobile,
                password = edt_new_pwd1.text.toString().trim()
            )
            viewModel?.updatePwd(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    switchView(1, "")
                    if(status){
                        finish()
                    }
                    CommonUtils.createSnackBar(
                        findViewById(android.R.id.content)!!,
                        message
                    )
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }


    private fun callOfferList() {
        if (CommonUtils.isOnline(this!!)) {
            switchView(3, "")
            var commonRequestObj =
                if (mType?.equals("offer")) CommonRequestObj(
                    headerInfo = HeaderInfo(Authorization = "Bearer "+mPreferenceUtils?.getValue(
                        Constants.USER_TOKEN))
                ) else getCommonRequestObj(
                    apiKey = getApiKey(),
                    orderid = mOrderHistoryDataItem?.id!!
                )
            viewModel?.getCouponList(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        rv_offers?.setHasFixedSize(true)
                        rv_offers?.layoutManager =
                            LinearLayoutManager(
                                this@ChangePwdActivity,
                                RecyclerView.VERTICAL,
                                false
                            )
                        offerAdapter=CouponsAdapter(
                            this@ChangePwdActivity,
                            this@ChangePwdActivity,
                            mType
                        )
                        rv_offers?.adapter = offerAdapter
                        offerAdapter?.setList(data!!)
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
               /* if (obj is DataItem && obj?.store_id!=null) {
                    var intent: Intent = Intent()
                    intent.putExtra("from", "offer")
                    intent.putExtra("id", obj?.store_id)
                    setResult(RESULT_OK, intent);
                    finish();
                }*/
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