package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.NotificationDataItem
import kotlinx.android.synthetic.main.item_sub_store.view.*
import kotlinx.android.synthetic.main.item_unread_notification.view.*


class NotificationAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var type:String,
    var mList:List<NotificationDataItem>? = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        holder.itemView.text_notification_time.text = mList?.get(position)?.updatedAt
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
