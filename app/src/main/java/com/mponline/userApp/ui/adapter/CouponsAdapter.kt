package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.DataItem
import kotlinx.android.synthetic.main.item_coupons.view.*


class CouponsAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var type:String = "offer",
    val mList: ArrayList<DataItem>? = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_coupons,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder?.itemView?.text_percentage?.text = if(mList?.get(position)?.discount_type?.equals("1")!!) "${mList?.get(position)?.discount_amount!!}flat \noff" else "${mList?.get(position)?.discount_amount!!}%\noff"
        holder?.itemView?.text_coupon_title?.text = mList?.get(position)?.coupon
        holder?.itemView?.text_from_date?.text = Html.fromHtml(mList?.get(position)?.description)
        if(type?.equals("offer")){
            holder?.itemView?.text_apply?.visibility = View.GONE
        }else{
            holder?.itemView?.text_apply?.visibility = View.VISIBLE
        }
        holder?.itemView?.text_apply?.setOnClickListener {
            listener?.onClick(position, holder?.itemView?.text_apply, mList?.get(position))
        }
        holder?.itemView?.setOnClickListener {
            if(type?.equals("offer")) {
                listener?.onClick(position, holder?.itemView?.text_coupon_title, mList?.get(position))
            }
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
