package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.item_order_history.view.*
import kotlinx.android.synthetic.main.layout_order_complete_list.view.*
import kotlinx.android.synthetic.main.layout_order_pending_list.view.*


class OrderHistoryAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList: List<OrderHistoryDataItem> = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_order_history,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!mList?.get(position)?.storedetail?.image?.isNullOrEmpty()) {
            ImageGlideUtils.loadUrlImage(
                context!!,
                mList?.get(position)?.storedetail?.image,
                holder.itemView.image_order_history
            )
        }
        holder?.itemView?.text_order_title?.text = mList?.get(position)?.products?.name
        holder?.itemView?.text_to_date?.text =
            "Application submitted as on ${mList?.get(position)?.createdAt}"
        when (mList?.get(position)?.status) {
            1 -> {
                holder.itemView.text_status.text = "Pending"
            }
            2 -> {
                holder.itemView.text_status.text = "Accepted"
                holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
            }
            3 -> {
                holder.itemView.text_status.text = "Rejected"
            }
            4 -> {
                holder.itemView.text_status.text = "Cancelled"
            }
            5 -> {
                holder.itemView.text_status.text = "Completed"
            }
        }
        if (mList?.get(position)?.status == 2) {

        }
        holder.itemView.linear_upper_layout.setOnClickListener {
            if (mList?.get(position)?.status == 2 || mList?.get(position)?.status == 5) {
                holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
            }
        }
        holder.itemView.ll_download_files.setOnClickListener {
            if (context is OnSwichFragmentListener) {
                var mOnSwichFragmentListener = context as OnSwichFragmentListener
                mOnSwichFragmentListener.onSwitchFragmentParent(
                    Constants.DOWNLOAD_LIST_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    null,
                    null
                )
            }
        }
        holder.itemView.text_make_payment.setOnClickListener {
            if (context is OnSwichFragmentListener) {
                var mOnSwichFragmentListener = context as OnSwichFragmentListener
                mOnSwichFragmentListener.onSwitchFragment(
                    Constants.PAYMENT_SUMMARY_PAGE,
                    Constants.WITH_NAV_DRAWER,
                    null,
                    null
                )
            }
        }
        holder.itemView.ll_hide_opt.setOnClickListener {
            holder.itemView.linear_bottom_layout.visibility = View.GONE
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    override fun getItemCount(): Int {
        return mList?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotificationListImgsViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
