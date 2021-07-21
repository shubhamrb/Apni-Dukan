package com.mponline.userApp.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.activity.FilePreviewActivity
import com.mponline.userApp.ui.activity.InvoicePreviewActivity
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.DateUtils
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.item_order_history.view.*
import kotlinx.android.synthetic.main.layout_order_complete_list.view.*
import kotlinx.android.synthetic.main.layout_order_pending_list.view.*
import java.util.*
import kotlin.collections.ArrayList


class OrderHistoryAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList: List<OrderHistoryDataItem> = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OrderHistoryViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_order_history,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is OrderHistoryViewHolder){
            if (!mList?.get(position)?.products?.product_image?.isNullOrEmpty()!!) {
                ImageGlideUtils.loadUrlImage(
                    context!!,
                    mList?.get(position)?.products?.product_image!!,
                    holder.itemView.image_order_history
                )
            }
            holder?.itemView?.text_order_title?.text = mList?.get(position)?.products?.name+" (${mList?.get(position)?.orderId})"
            holder?.itemView?.text_to_date?.text =
                "Application submitted as on \n${DateUtils.changeDateFormat(mList?.get(position)?.createdAt, "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy")}"
            when (mList?.get(position)?.status) {
                1 -> {
                    holder.itemView.text_status.text = "Waiting for\nkiosk to accept"
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.itemView.text_status.setTextAppearance(context, R.style.Text_mont_regular_pending);
                    } else {
                        holder.itemView.text_status.setTextAppearance(R.style.Text_mont_regular_pending);
                    }
                    holder.itemView.linear_bottom_layout.visibility = View.GONE
                    holder.itemView.layout_order_pending.visibility = View.GONE
                    holder.itemView.layout_order_complete.visibility = View.GONE
                    holder.itemView.text_view_invoice.visibility = View.GONE
                    holder.itemView.image_status.setBackgroundResource(R.drawable.circle_yellow);
                }
                2 -> {
                    holder.itemView.text_status.text = "Accepted\nby kiosk"
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.itemView.text_status.setTextAppearance(context, R.style.Text_mont_regular_accepted);
                    } else {
                        holder.itemView.text_status.setTextAppearance(R.style.Text_mont_regular_accepted);
                    }
                    holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
                    holder.itemView.layout_order_pending.visibility = View.VISIBLE
                    holder.itemView.layout_order_complete.visibility = View.GONE
                    holder.itemView.text_view_invoice.visibility = View.GONE
                    var currDateTime = DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss")
                    var estimatedDateTime = DateUtils.addHrMinuteToDateStr(mList?.get(position)?.acceptedAt!!, if(mList?.get(position)?.timeType?.equals("hour")!!) true else false, mList?.get(position)?.orderCompletionTime)
                    var timerObj = DateUtils.checkTimeDifference(currDateTime, estimatedDateTime)
                    holder?.itemView?.text_hour?.text = timerObj?.hour
                    holder?.itemView?.text_minute?.text = timerObj?.min
                    holder?.itemView?.text_sec?.text = timerObj?.sec
                    if (holder.timer == null) {
                        holder?.timer?.cancel()
                        object : CountDownTimer(timerObj?.totalMillis, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                var remainingTimeObj = DateUtils.getTimeObjFromMillis(millisUntilFinished)
                                holder?.itemView?.text_hour?.text = remainingTimeObj?.hour
                                holder?.itemView?.text_minute?.text = remainingTimeObj?.min
                                holder?.itemView?.text_sec?.text = remainingTimeObj?.sec
                            }

                            override fun onFinish() {
                                holder?.itemView?.text_hour?.text = "0"
                                holder?.itemView?.text_minute?.text = "0"
                                holder?.itemView?.text_sec?.text = "0"
                            }
                        }.start()
                    }
                    holder.itemView.image_status.setBackgroundResource(R.drawable.circle_green);
                }
                3 -> {
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.itemView.text_status.setTextAppearance(context, R.style.Text_mont_regular_cancelled);
                    } else {
                        holder.itemView.text_status.setTextAppearance(R.style.Text_mont_regular_cancelled);
                    }
                    holder.itemView.text_status.text = "Rejected"
                    holder.itemView.linear_bottom_layout.visibility = View.GONE
                    holder.itemView.layout_order_pending.visibility = View.GONE
                    holder.itemView.layout_order_complete.visibility = View.GONE
                    holder.itemView.text_view_invoice.visibility = View.GONE
                    holder.itemView.image_status.setBackgroundResource(R.drawable.circle_red);
                }
                4 -> {
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.itemView.text_status.setTextAppearance(context, R.style.Text_mont_regular_cancelled);
                    } else {
                        holder.itemView.text_status.setTextAppearance(R.style.Text_mont_regular_cancelled);
                    }
                    holder.itemView.text_status.text = "Cancelled"
                    holder.itemView.linear_bottom_layout.visibility = View.GONE
                    holder.itemView.layout_order_pending.visibility = View.GONE
                    holder.itemView.layout_order_complete.visibility = View.GONE
                    holder.itemView.text_view_invoice.visibility = View.GONE
                    holder.itemView.image_status.setBackgroundResource(R.drawable.circle_red);
                }
                5 -> {
                    holder.itemView.text_status.text = "Order has been\ncompleted"
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.itemView.text_status.setTextAppearance(context, R.style.Text_mont_regular_completed);
                    } else {
                        holder.itemView.text_status.setTextAppearance(R.style.Text_mont_regular_completed);
                    }
                    holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
                    holder.itemView.layout_order_pending.visibility = View.GONE
                    holder.itemView.layout_order_complete.visibility = View.VISIBLE
                    holder.itemView.text_view_invoice.visibility = View.VISIBLE
                    holder.itemView.image_status.setBackgroundResource(R.drawable.circle_green);
                    if(mList?.get(position)?.ratingStatus?.equals("1")!!){
                        holder?.itemView?.ll_submit_rating?.visibility = View.GONE
                        if(mList?.get(position)?.userratting!=null && (!mList?.get(position)?.userratting?.isNullOrEmpty()!!)){
                            holder?.itemView?.ratingbar?.rating = mList?.get(position)?.userratting?.toFloat()!!
                            holder?.itemView?.text_rating_msg?.text = "Your ratings"
                        }
                    }else{
                        holder?.itemView?.text_rating_msg?.text = "Leave a rating"
                        holder?.itemView?.ll_submit_rating?.visibility = View.VISIBLE
                    }
                }
            }
            holder.itemView.text_view_invoice.setOnClickListener {
                var intent:Intent = Intent(context, InvoicePreviewActivity::class.java)
                intent?.putExtra("order", mList?.get(position))
                context?.startActivity(intent)
            }
            holder.itemView.linear_upper_layout.setOnClickListener {
                if (mList?.get(position)?.status == 2 || mList?.get(position)?.status == 5) {
                    holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
                }
            }
            holder.itemView.ll_download_files.setOnClickListener {
                var intent:Intent = Intent(context, FilePreviewActivity::class.java)
                intent?.putExtra("file", mList?.get(position)?.paymentFile)
                context?.startActivity(intent)
            }
            holder.itemView.text_make_payment.setOnClickListener {
                listener?.onClick(position, holder.itemView.text_make_payment, mList?.get(position))
            }
            holder.itemView.ll_hide_opt.setOnClickListener {
                holder.itemView.linear_bottom_layout.visibility = View.GONE
            }
            holder.itemView.ll_submit_rating.setOnClickListener {
                if(holder.itemView.ratingbar.rating>0) {
                    mList?.get(position)?.myrating = holder.itemView.ratingbar.rating.toString()!!
                    listener?.onClick(
                        position,
                        holder.itemView.ll_submit_rating,
                        mList?.get(position)
                    )
                }
            }
            holder.itemView.rl_chat.setOnClickListener {
                listener?.onClick(position, holder.itemView.rl_chat, mList?.get(position))
            }
            holder.itemView.rl_call.setOnClickListener {
                listener?.onClick(position, holder.itemView.rl_call, mList?.get(position))
            }
            holder.itemView.rl_whatsapp.setOnClickListener {
                listener?.onClick(position, holder.itemView.rl_whatsapp, mList?.get(position))
            }
            holder.itemView.text_view_details.setOnClickListener {
                listener?.onClick(position, holder.itemView.text_view_details, mList?.get(position))
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

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var timer: CountDownTimer? = null
        fun FeedViewHolder(itemView:View) {

        }
    }


}
