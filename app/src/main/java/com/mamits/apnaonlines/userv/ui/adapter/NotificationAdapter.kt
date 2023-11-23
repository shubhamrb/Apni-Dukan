package com.mamits.apnaonlines.userv.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.response.NotificationDataItem
import kotlinx.android.synthetic.main.item_unread_notification.view.*


class NotificationAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var type:String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mList:ArrayList<NotificationDataItem>? = ArrayList()

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
               if(type?.equals("unread")) R.layout.item_unread_notification else R.layout.item_read_notification,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.text_noti_title.text = mList?.get(position)?.message
        holder.itemView.text_noti_type.text = mList?.get(position)?.notiType
        holder.itemView.text_notification_time.text = mList?.get(position)?.createdAt
        holder.itemView.setOnClickListener {
            listener?.onClick(position, holder.itemView.text_noti_title, mList?.get(position))
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    fun setList(list:ArrayList<NotificationDataItem>) {
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
