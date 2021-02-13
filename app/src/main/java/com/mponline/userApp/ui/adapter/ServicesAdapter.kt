package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.CategorylistItem
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.item_service.view.*


class ServicesAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList:ArrayList<CategorylistItem> = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_service,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        ImageGlideUtils.loadUrlImage(context!!, mList?.get(position)?.image, holder?.itemView?.image_service)
        holder.itemView.text_service.text = mList?.get(position)?.name
        holder.itemView?.setOnClickListener {
            listener?.onClick(position, holder?.itemView?.cv_service, mList?.get(position))
        }
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
