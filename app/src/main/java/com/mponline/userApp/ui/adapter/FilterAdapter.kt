package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.NotificationDataItem
import kotlinx.android.synthetic.main.item_filter_chip.view.*
import kotlinx.android.synthetic.main.item_sub_store.view.*
import kotlinx.android.synthetic.main.item_unread_notification.view.*
import kotlinx.android.synthetic.main.item_unread_notification.view.text_noti_title


class FilterAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var type:String,
    var mList:List<String>? = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_filter_chip,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.text_filter_chip_name.text = mList?.get(position)
        if (position == mSelectedPosition){
            holder.itemView.text_filter_chip_name.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            holder.itemView.text_filter_chip_name.setBackgroundResource(R.drawable.rounded_stroke_primary_25)
        }else {
            holder.itemView.text_filter_chip_name.setTextColor(ContextCompat.getColor(context!!, R.color.black))
            holder.itemView.text_filter_chip_name.setBackgroundResource(R.drawable.rounded_stroke_normal_25)
        }

        holder?.itemView?.setOnClickListener {
            mSelectedPosition = position
            listener?.onClick(position, holder.itemView.text_filter_chip_name, mList?.get(position), type)
            notifyDataSetChanged()
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
