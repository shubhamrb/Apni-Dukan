package com.mamits.apnaonlines.userv.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.PaymentSummaryObj
import kotlinx.android.synthetic.main.item_payment_details.view.*


class PaymentDetailAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList:ArrayList<PaymentSummaryObj>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_payment_details,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder?.itemView?.text_service_name?.text = mList?.get(position)?.formDetailName
        holder?.itemView?.text_price?.text = context?.resources?.getString(R.string.rs)+" ${mList?.get(position)?.formDetailPrice}"
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    override fun getItemCount(): Int {
        return mList?.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotificationListImgsViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
