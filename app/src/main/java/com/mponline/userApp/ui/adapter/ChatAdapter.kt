package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.OrderHistoryDataItem
import kotlinx.android.synthetic.main.item_chat.view.*
import kotlinx.android.synthetic.main.item_sub_store.view.*


class ChatAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList:List<OrderHistoryDataItem>? = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

        holder?.itemView?.text_chat_title?.text = mList?.get(position)?.products?.name
        holder?.itemView?.text_chat_subtitle?.text = mList?.get(position)?.products?.description
        holder?.itemView?.text_chat_datetime?.text = mList?.get(position)?.products?.updatedAt

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
