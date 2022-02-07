package com.mamits.apnaonlines.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.listener.OnItemClickListener
import com.mamits.apnaonlines.user.model.response.ProductListItem
import com.mamits.apnaonlines.user.util.ImageGlideUtils
import kotlinx.android.synthetic.main.item_home_product.view.*


class TopProductAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList:ArrayList<ProductListItem> = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_home_product,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        ImageGlideUtils.loadUrlImage(context!!, mList?.get(position)?.image, holder?.itemView?.image_service)
        holder.itemView.text_service.text = mList?.get(position)?.name
        holder.itemView?.setOnClickListener {
            listener?.onClick(position, holder?.itemView?.rl_product, mList?.get(position))
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
