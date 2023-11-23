package com.mamits.apnaonlines.userv.ui.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.response.OrderDetailItem
import kotlinx.android.synthetic.main.item_form_detail.view.*


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
       /* if(mList?.get(position)?.value?.isNullOrEmpty()!! && (mList?.get(position)?.filedata==null)){
            CommonUtils.printLog("VISI","1")
            holder.itemView.ll_form_content.visibility = View.GONE
        }else{
            CommonUtils.printLog("VISI","2")
            holder.itemView.ll_form_content.visibility = View.VISIBLE
        }*/
        if (mList?.get(position)?.filedata!=null && mList?.get(position)?.filedata?.type!=null && (!mList?.get(position)?.filedata?.type?.isNullOrEmpty()!!)) {
            holder.itemView.text_form_val.text =
                context?.resources?.getString(R.string.u_view_details_u)
            if (Build.VERSION.SDK_INT < 23) {
                holder.itemView.text_form_val.setTextAppearance(context, R.style.Text_mont_regular_cancelled);
            } else {
                holder.itemView.text_form_val.setTextAppearance(R.style.Text_mont_regular_cancelled);
            }
        }else{
            if(mList?.get(position)?.value== null || mList?.get(position)?.value?.isNullOrEmpty()!!){
                holder.itemView.ll_content.visibility = View.GONE
            }else{
                holder.itemView.ll_content.visibility = View.VISIBLE
            }
            holder.itemView.text_form_val.text =  mList?.get(position)?.value
        }
        if (mList?.get(position)?.filedata!=null && mList?.get(position)?.filedata?.type!=null && (!mList?.get(position)?.filedata?.type?.isNullOrEmpty()!!)) {
            holder.itemView.text_form_val.setOnClickListener {
                /*var intent: Intent = Intent(context, FilePreviewActivity::class.java)
                intent?.putExtra("file", mList?.get(position)?.filedata?.url)
                context?.startActivity(intent)*/
                listener?.onClick(position, holder.itemView.text_form_val, mList?.get(position))

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
