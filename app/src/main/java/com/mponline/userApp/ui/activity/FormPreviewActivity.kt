package com.mponline.userApp.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.request.FormDataItem
import com.mponline.userApp.model.response.OrderDetailItem
import com.mponline.userApp.ui.adapter.OrderDetailAdapter
import com.mponline.userApp.ui.adapter.OrderHistoryAdapter
import com.mponline.userApp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_form_preview.*
import kotlinx.android.synthetic.main.activity_img_preview.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import kotlinx.android.synthetic.main.item_service.view.*

class FormPreviewActivity : BaseActivity(), OnItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_preview)
        toolbar_title.text = ""
        image_back?.setOnClickListener {
            finish()
        }
        var data = intent?.getParcelableArrayListExtra<OrderDetailItem>("data")
        if(!data?.isNullOrEmpty()!!){
            rv_form_detail?.setHasFixedSize(true)
            rv_form_detail?.layoutManager =
                LinearLayoutManager(
                    this,
                    RecyclerView.VERTICAL,
                    false
                )
            rv_form_detail?.adapter = OrderDetailAdapter(
                this,
                this,
                data!!
            )
        }

    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }
}