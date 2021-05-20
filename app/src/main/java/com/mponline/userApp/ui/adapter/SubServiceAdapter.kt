package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.ProductListItem
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.item_sub_store.view.*


class SubServiceAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList:ArrayList<ProductListItem>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_sub_store,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(!mList?.get(position)?.image?.isNullOrEmpty()){
            ImageGlideUtils.loadUrlImage(context!!, mList?.get(position)?.image, holder.itemView.image_service)
        }
        holder.itemView.text_service_title.text = mList?.get(position)?.name
        holder.itemView.setOnClickListener {
            listener?.onClick(position, holder.itemView.cv_sub_service, mList?.get(position))
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
