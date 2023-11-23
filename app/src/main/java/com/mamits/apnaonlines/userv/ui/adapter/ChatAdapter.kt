package com.mamits.apnaonlines.userv.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.response.OrderHistoryDataItem
import com.mamits.apnaonlines.userv.utils.DateUtils
import kotlinx.android.synthetic.main.item_chat.view.*


class ChatAdapter(
    var context: Context?,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mList:ArrayList<OrderHistoryDataItem>? = ArrayList()

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
               R.layout.item_chat ,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onClick(position, holder.itemView.rl_chat, mList?.get(position))
        }

        holder?.itemView?.text_letter?.text = mList?.get(position)?.products?.name?.toString()?.toCharArray()?.get(0)?.toString()?.toUpperCase()!!
        holder?.itemView?.text_chat_title?.text = Html.fromHtml(mList?.get(position)?.products?.name)
        holder?.itemView?.text_chat_subtitle?.text = "Order ID: ${mList?.get(position)?.orderId}"
        holder?.itemView?.text_chat_datetime?.text =  DateUtils.changeDateFormat(mList?.get(position)?.updatedAt, "yyyy-MM-dd hh:mm:ss", "dd MMM yyyy")+"\n"+ DateUtils.changeDateFormat(mList?.get(position)?.updatedAt, "yyyy-MM-dd hh:mm:ss", "hh:mm a")

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
    fun setList(list:ArrayList<OrderHistoryDataItem>){
        mList?.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotificationListImgsViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
