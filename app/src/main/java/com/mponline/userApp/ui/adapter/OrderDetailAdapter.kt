package com.mponline.userApp.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.request.FormDataItem
import com.mponline.userApp.model.response.NotificationDataItem
import com.mponline.userApp.model.response.OrderDetailItem
import com.mponline.userApp.ui.activity.FilePreviewActivity
import com.mponline.userApp.ui.activity.FormPreviewActivity
import kotlinx.android.synthetic.main.item_form_detail.view.*
import kotlinx.android.synthetic.main.item_order_history.view.*


class OrderDetailAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList: ArrayList<OrderDetailItem>? = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_form_detail,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.text_form_key.text = mList?.get(position)?.name
        if (mList?.get(position)?.filedata!=null && mList?.get(position)?.filedata?.type!=null && (!mList?.get(position)?.filedata?.type?.isNullOrEmpty()!!)) {
            holder.itemView.text_form_val.text =
                context?.resources?.getString(R.string.u_view_details_u)
            if (Build.VERSION.SDK_INT < 23) {
                holder.itemView.text_form_val.setTextAppearance(context, R.style.Text_mont_regular_cancelled);
            } else {
                holder.itemView.text_form_val.setTextAppearance(R.style.Text_mont_regular_cancelled);
            }
        }else{
            holder.itemView.text_form_val.text =  mList?.get(position)?.value
        }
        if (mList?.get(position)?.filedata!=null && mList?.get(position)?.filedata?.type!=null && (!mList?.get(position)?.filedata?.type?.isNullOrEmpty()!!)) {
            holder.itemView.text_form_val.setOnClickListener {
                var intent: Intent = Intent(context, FilePreviewActivity::class.java)
                intent?.putExtra("file", mList?.get(position)?.filedata?.url)
                context?.startActivity(intent)
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
