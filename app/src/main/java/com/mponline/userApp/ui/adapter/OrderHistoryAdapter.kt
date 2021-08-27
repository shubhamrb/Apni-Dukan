package com.mponline.userApp.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.internal.service.Common
import com.google.gson.Gson
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.TimerObj
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.activity.InvoicePreviewActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.util.CustomRunnable
import com.mponline.userApp.utils.DateUtils
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.item_order_history.view.*
import kotlinx.android.synthetic.main.layout_order_complete_list.view.*
import kotlinx.android.synthetic.main.layout_order_pending_list.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList


class OrderHistoryAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList: ArrayList<OrderHistoryDataItem> = ArrayList(),
    var isFromAccount: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val handler = Handler()
    private val VIEW_TIMER = 2
    private val VIEW_NORMAL = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TIMER) {
            return OrderHistoryTimerViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_order_history,
                    parent,
                    false
                ), 1
            )
        } else {
            return OrderHistoryViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_order_history,
                    parent,
                    false
                )
            )
        }
    }

    fun refreshAdapter(list: ArrayList<OrderHistoryDataItem>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        holder.itemView.linear_bottom_layout.visibility = if(!mList?.get(position)?.isBottomPartHidden) View.VISIBLE else View.GONE
        if(payloads!=null && payloads?.isNotEmpty() && payloads?.size>0){
            CommonUtils.printLog("PAYLOAD_DATA","${Gson().toJson(payloads)}")
            payloads?.forEach {
                if(it is TimerObj){
                    holder?.itemView?.text_hour?.text = it?.hour
                    holder?.itemView?.text_minute?.text = it?.min
                    holder?.itemView?.text_sec?.text = it?.sec
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OrderHistoryTimerViewHolder) {
            if (mList?.get(position)?.status == 2) {
                holder.itemView.text_status.text = "Accepted\nby vendor"
                if (Build.VERSION.SDK_INT < 23) {
                    holder.itemView.text_status.setTextAppearance(
                        context,
                        R.style.Text_mont_regular_accepted
                    );
                } else {
                    holder.itemView.text_status.setTextAppearance(R.style.Text_mont_regular_accepted);
                }
                holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
                holder.itemView.layout_order_pending.visibility = View.VISIBLE
                holder.itemView.layout_order_complete.visibility = View.GONE
                holder.itemView.text_view_invoice.visibility = View.GONE

                holder?.itemView?.text_hour?.text = mList?.get(position)?.timerObj?.hour
                holder?.itemView?.text_minute?.text = mList?.get(position)?.timerObj?.min
                holder?.itemView?.text_sec?.text = mList?.get(position)?.timerObj?.sec
                holder.itemView.image_status.setBackgroundResource(R.drawable.circle_green);

                holder.bind(context, mList?.get(position), position, listener)
            }
        } else if (holder is OrderHistoryViewHolder) {
//            holder.bind(context, mList?.get(position), position, listener)
            if (!mList?.get(position)?.products?.product_image?.isNullOrEmpty()!!) {
                ImageGlideUtils.loadUrlImage(
                    context!!,
                    mList?.get(position)?.products?.product_image!!,
                    holder.itemView.image_order_history
                )
            }
            holder?.itemView?.text_order_title?.text =
                mList?.get(position)?.products?.name + " (${mList?.get(position)?.orderId})"
            holder?.itemView?.text_vendor_name?.text = mList?.get(position)?.storedetail?.name
            holder?.itemView?.text_to_date?.text =
                "Application submitted as on \n${
                    DateUtils.changeDateFormat(
                        mList?.get(position)?.createdAt,
                        "yyyy-MM-dd HH:mm:ss",
                        "dd MMM yyyy"
                    )
                }"
            when (mList?.get(position)?.status) {
                1 -> {
                    holder.itemView.text_status.text = "Waiting for\nvendor accept"
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.itemView.text_status.setTextAppearance(
                            context,
                            R.style.Text_mont_regular_pending
                        );
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
                    holder.itemView.text_status.text = "Accepted\nby vendor"
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.itemView.text_status.setTextAppearance(
                            context,
                            R.style.Text_mont_regular_accepted
                        );
                    } else {
                        holder.itemView.text_status.setTextAppearance(R.style.Text_mont_regular_accepted);
                    }
                    holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
                    holder.itemView.layout_order_pending.visibility = View.VISIBLE
                    holder.itemView.layout_order_complete.visibility = View.GONE
                    holder.itemView.text_view_invoice.visibility = View.GONE

                    holder?.itemView?.text_hour?.text = mList?.get(position)?.timerObj?.hour
                    holder?.itemView?.text_minute?.text = mList?.get(position)?.timerObj?.min
                    holder?.itemView?.text_sec?.text = mList?.get(position)?.timerObj?.sec

//                    holder.bind(context, mList?.get(position), position, listener)
                    holder.itemView.image_status.setBackgroundResource(R.drawable.circle_green);
                }
                3 -> {
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.itemView.text_status.setTextAppearance(
                            context,
                            R.style.Text_mont_regular_cancelled
                        );
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
                        holder.itemView.text_status.setTextAppearance(
                            context,
                            R.style.Text_mont_regular_cancelled
                        );
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
                        holder.itemView.text_status.setTextAppearance(
                            context,
                            R.style.Text_mont_regular_completed
                        );
                    } else {
                        holder.itemView.text_status.setTextAppearance(R.style.Text_mont_regular_completed);
                    }
                    holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
                    holder.itemView.layout_order_pending.visibility = View.GONE
                    holder.itemView.layout_order_complete.visibility = View.VISIBLE
                    holder.itemView.text_view_invoice.visibility = View.VISIBLE
                    holder.itemView.image_status.setBackgroundResource(R.drawable.circle_green);
                    if (mList?.get(position)?.paymentFile != null && !mList?.get(position)?.paymentFile?.isNullOrEmpty()!!) {
                        holder.itemView.ll_download_files.visibility = View.VISIBLE
                    } else {
                        holder.itemView.ll_download_files.visibility = View.GONE
                    }
                    if (mList?.get(position)?.ratingStatus?.equals("1")!!) {
                        holder?.itemView?.ll_submit_rating?.visibility = View.GONE
                        if (mList?.get(position)?.userratting != null && (!mList?.get(position)?.userratting?.isNullOrEmpty()!!)) {
                            holder?.itemView?.ratingbar?.rating =
                                mList?.get(position)?.userratting?.toFloat()!!
                            holder?.itemView?.ratingbar?.setIsIndicator(true);
                            holder?.itemView?.text_rating_msg?.text = "Your ratings"
                        }
                    } else {
                        holder?.itemView?.text_rating_msg?.text = "Leave a rating"
                        holder?.itemView?.ratingbar?.setIsIndicator(false);
                        holder?.itemView?.ll_submit_rating?.visibility = View.VISIBLE
                    }
                }
            }
            holder.itemView.text_view_invoice.setOnClickListener {
                var intent: Intent = Intent(context, InvoicePreviewActivity::class.java)
                intent?.putExtra("order", mList?.get(position))
                context?.startActivity(intent)
            }
            holder.itemView.linear_upper_layout.setOnClickListener {
                if (mList?.get(position)?.status == 2 || mList?.get(position)?.status == 5) {
                    holder.itemView.linear_bottom_layout.visibility = View.VISIBLE
                    mList?.get(position)?.isBottomPartHidden = true
                }
            }
            holder.itemView.ll_download_files.setOnClickListener {
                listener?.onClick(position, holder.itemView.ll_download_files, mList?.get(position))
            }
            holder.itemView.text_make_payment.setOnClickListener {
                listener?.onClick(position, holder.itemView.text_make_payment, mList?.get(position))
            }
            holder.itemView.ll_hide_opt.setOnClickListener {
                holder.itemView.linear_bottom_layout.visibility = View.GONE
                mList?.get(position)?.isBottomPartHidden = false
            }
            holder.itemView.ll_submit_rating.setOnClickListener {
                if (holder.itemView.ratingbar.rating > 0) {
                    mList?.get(position)?.myrating = holder.itemView.ratingbar.rating.toString()!!
                    listener?.onClick(
                        position,
                        holder.itemView.ll_submit_rating,
                        mList?.get(position)
                    )
                }
            }
            holder.itemView.rl_chat.setOnClickListener {
                if (mList?.get(position)?.status == 2) {
                    listener?.onClick(position, holder.itemView.rl_chat, mList?.get(position))
                } else {

                }
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
        return if (isFromAccount && mList?.size!! > 5) 5 else mList?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        if (mList?.get(position)?.status == 2 && mList?.get(position)?.timerObj != null && mList?.get(
                position
            )?.timerObj?.totalMillis!! > 0
        ) {
            return VIEW_TIMER
        } else {
            return VIEW_NORMAL
        }
    }

    inner class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(
            context: Context?,
            orderHistoryDataItem: OrderHistoryDataItem,
            pos: Int,
            listener: OnItemClickListener
        ) {
            /*if(!orderHistoryDataItem?.isDataShown){
                if (!orderHistoryDataItem?.products?.product_image?.isNullOrEmpty()!!) {
                    ImageGlideUtils.loadUrlImage(
                        context!!,
                        orderHistoryDataItem?.products?.product_image!!,
                        itemView.image_order_history
                    )
                }
                itemView?.text_order_title?.text =
                    orderHistoryDataItem?.products?.name + " (${orderHistoryDataItem?.orderId})"
                itemView?.text_vendor_name?.text = orderHistoryDataItem?.storedetail?.name
                itemView?.text_to_date?.text =
                    "Application submitted as on \n${
                        DateUtils.changeDateFormat(
                            orderHistoryDataItem?.createdAt,
                            "yyyy-MM-dd HH:mm:ss",
                            "dd MMM yyyy"
                        )
                    }"
                when (orderHistoryDataItem?.status) {
                    1 -> {
                        itemView.text_status.text = "Waiting for\nvendor accept"
                        if (Build.VERSION.SDK_INT < 23) {
                            itemView.text_status.setTextAppearance(
                                context,
                                R.style.Text_mont_regular_pending
                            );
                        } else {
                            itemView.text_status.setTextAppearance(R.style.Text_mont_regular_pending);
                        }
                        itemView.linear_bottom_layout.visibility = View.GONE
                        itemView.layout_order_pending.visibility = View.GONE
                        itemView.layout_order_complete.visibility = View.GONE
                        itemView.text_view_invoice.visibility = View.GONE
                        itemView.image_status.setBackgroundResource(R.drawable.circle_yellow);
                    }
                    2 -> {
                        itemView.text_status.text = "Accepted\nby vendor"
                        if (Build.VERSION.SDK_INT < 23) {
                            itemView.text_status.setTextAppearance(
                                context,
                                R.style.Text_mont_regular_accepted
                            );
                        } else {
                            itemView.text_status.setTextAppearance(R.style.Text_mont_regular_accepted);
                        }
                        itemView.linear_bottom_layout.visibility = View.VISIBLE
                        itemView.layout_order_pending.visibility = View.VISIBLE
                        itemView.layout_order_complete.visibility = View.GONE
                        itemView.text_view_invoice.visibility = View.GONE

                        itemView?.text_hour?.text = orderHistoryDataItem?.timerObj?.hour
                        itemView?.text_minute?.text = orderHistoryDataItem?.timerObj?.min
                        itemView?.text_sec?.text = orderHistoryDataItem?.timerObj?.sec

//                    holder.bind(context, mList?.get(position), position, listener)
                        itemView.image_status.setBackgroundResource(R.drawable.circle_green);
                    }
                    3 -> {
                        if (Build.VERSION.SDK_INT < 23) {
                            itemView.text_status.setTextAppearance(
                                context,
                                R.style.Text_mont_regular_cancelled
                            );
                        } else {
                            itemView.text_status.setTextAppearance(R.style.Text_mont_regular_cancelled);
                        }
                        itemView.text_status.text = "Rejected"
                        itemView.linear_bottom_layout.visibility = View.GONE
                        itemView.layout_order_pending.visibility = View.GONE
                        itemView.layout_order_complete.visibility = View.GONE
                        itemView.text_view_invoice.visibility = View.GONE
                        itemView.image_status.setBackgroundResource(R.drawable.circle_red);
                    }
                    4 -> {
                        if (Build.VERSION.SDK_INT < 23) {
                            itemView.text_status.setTextAppearance(
                                context,
                                R.style.Text_mont_regular_cancelled
                            );
                        } else {
                            itemView.text_status.setTextAppearance(R.style.Text_mont_regular_cancelled);
                        }
                        itemView.text_status.text = "Cancelled"
                        itemView.linear_bottom_layout.visibility = View.GONE
                        itemView.layout_order_pending.visibility = View.GONE
                        itemView.layout_order_complete.visibility = View.GONE
                        itemView.text_view_invoice.visibility = View.GONE
                        itemView.image_status.setBackgroundResource(R.drawable.circle_red);
                    }
                    5 -> {
                        itemView.text_status.text = "Order has been\ncompleted"
                        if (Build.VERSION.SDK_INT < 23) {
                            itemView.text_status.setTextAppearance(
                                context,
                                R.style.Text_mont_regular_completed
                            );
                        } else {
                            itemView.text_status.setTextAppearance(R.style.Text_mont_regular_completed);
                        }
                        itemView.linear_bottom_layout.visibility = View.VISIBLE
                        itemView.layout_order_pending.visibility = View.GONE
                        itemView.layout_order_complete.visibility = View.VISIBLE
                        itemView.text_view_invoice.visibility = View.VISIBLE
                        itemView.image_status.setBackgroundResource(R.drawable.circle_green);
                        if (orderHistoryDataItem?.paymentFile != null && !orderHistoryDataItem?.paymentFile?.isNullOrEmpty()!!) {
                            itemView.ll_download_files.visibility = View.VISIBLE
                        } else {
                            itemView.ll_download_files.visibility = View.GONE
                        }
                        if (orderHistoryDataItem?.ratingStatus?.equals("1")!!) {
                            itemView?.ll_submit_rating?.visibility = View.GONE
                            if (orderHistoryDataItem?.userratting != null && (!orderHistoryDataItem?.userratting?.isNullOrEmpty()!!)) {
                                itemView?.ratingbar?.rating =
                                    mList?.get(position)?.userratting?.toFloat()!!
                                itemView?.ratingbar?.setIsIndicator(true);
                                itemView?.text_rating_msg?.text = "Your ratings"
                            }
                        } else {
                            itemView?.text_rating_msg?.text = "Leave a rating"
                            itemView?.ratingbar?.setIsIndicator(false);
                            itemView?.ll_submit_rating?.visibility = View.VISIBLE
                        }
                    }
                }
                orderHistoryDataItem?.isDataShown = true
            }
            itemView.text_view_invoice.setOnClickListener {
                var intent: Intent = Intent(context, InvoicePreviewActivity::class.java)
                intent?.putExtra("order", mList?.get(position))
                context?.startActivity(intent)
            }
            itemView.linear_upper_layout.setOnClickListener {
                if (orderHistoryDataItem?.status == 2 || orderHistoryDataItem?.status == 5) {
                    itemView.linear_bottom_layout.visibility = View.VISIBLE
                }
            }
            itemView.ll_download_files.setOnClickListener {
                listener?.onClick(position, itemView.ll_download_files, orderHistoryDataItem)
            }
            itemView.text_make_payment.setOnClickListener {
                listener?.onClick(position, itemView.text_make_payment, orderHistoryDataItem)
            }
            itemView.ll_hide_opt.setOnClickListener {
                itemView.linear_bottom_layout.visibility = View.GONE
            }
            itemView.ll_submit_rating.setOnClickListener {
                if (itemView.ratingbar.rating > 0) {
                    orderHistoryDataItem?.myrating = itemView.ratingbar.rating.toString()!!
                    listener?.onClick(
                        position,
                        itemView.ll_submit_rating,
                        orderHistoryDataItem
                    )
                }
            }
            itemView.rl_chat.setOnClickListener {
                if (orderHistoryDataItem?.status == 2) {
                    listener?.onClick(pos, itemView.rl_chat, orderHistoryDataItem)
                } else {

                }
            }
            itemView.rl_call.setOnClickListener {
                listener?.onClick(pos, itemView.rl_call, orderHistoryDataItem)
            }
            itemView.rl_whatsapp.setOnClickListener {
                listener?.onClick(pos, itemView.rl_whatsapp, orderHistoryDataItem)
            }
            itemView.text_view_details.setOnClickListener {
                listener?.onClick(pos, itemView.text_view_details, orderHistoryDataItem)
            }*/
        }
    }

    inner class OrderHistoryTimerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var timer: CountDownTimer? = null
        var timerMap = HashMap<Int, CountDownTimer>()

        constructor(view: View, viewType: Int) : this(view) {
        }

        fun bind(
            context: Context?,
            orderHistoryDataItem: OrderHistoryDataItem,
            pos: Int,
            listener: OnItemClickListener
        ) {
            itemView.linear_bottom_layout.visibility = if(!mList?.get(position)?.isBottomPartHidden) View.VISIBLE else View.GONE
            if(!orderHistoryDataItem?.isDataShown){
                if (!orderHistoryDataItem?.products?.product_image?.isNullOrEmpty()!!) {
                    ImageGlideUtils.loadUrlImage(
                        context!!,
                        orderHistoryDataItem?.products?.product_image!!,
                        itemView.image_order_history
                    )
                }
                itemView?.text_order_title?.text =
                    orderHistoryDataItem?.products?.name + " (${orderHistoryDataItem?.orderId})"
                itemView?.text_vendor_name?.text = orderHistoryDataItem?.storedetail?.name
                itemView?.text_to_date?.text =
                    "Application submitted as on \n${
                        DateUtils.changeDateFormat(
                            orderHistoryDataItem?.createdAt,
                            "yyyy-MM-dd HH:mm:ss",
                            "dd MMM yyyy"
                        )
                    }"
                mList?.get(pos)?.isDataShown = true
//                notifyItemChanged(pos, "")
            }
            if (orderHistoryDataItem?.status == 2) {
                if (orderHistoryDataItem?.timerObj != null && orderHistoryDataItem?.timerObj?.totalMillis!! > 0) {
                    timer = timerMap?.get(pos)
                    if (timer == null) {
                        CommonUtils.printLog("TIMER_NEW", "${pos}")
                        timer = object :
                            CountDownTimer(orderHistoryDataItem?.timerObj?.totalMillis!!, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                CommonUtils.printLog("TIMER_TICKTICK", "${pos}")
                                runBlocking {
                                    var remainingTimeObj = async {
                                        var timessf =
                                            DateUtils.getTimeObjFromMillis(millisUntilFinished)
                                        CommonUtils.printLog("TIMER_TICKTICK-->", "${timessf?.sec}")
                                        mList?.get(pos)?.timerObj = timessf
                                        notifyItemChanged(pos, timessf)
//                                        notifyItemChanged(pos)
                                    }
                                    var res = remainingTimeObj?.await()
                                }
                            }

                            override fun onFinish() {
                            }
                        }.start()
                        timerMap?.put(pos, timer!!);
                    } else {
                        CommonUtils.printLog("TIMER_OLD", "${pos}")
//                    timer?.start()
                        timerMap?.put(pos, timer!!);
                    }
                }
            }
            itemView.linear_upper_layout.setOnClickListener {
                if (orderHistoryDataItem?.status == 2 || orderHistoryDataItem?.status == 5) {
                    itemView.linear_bottom_layout.visibility = View.VISIBLE
                    mList?.get(pos)?.isBottomPartHidden = true
                }
            }
            itemView.text_make_payment.setOnClickListener {
                listener?.onClick(pos, itemView.text_make_payment, orderHistoryDataItem)
            }
            itemView.ll_hide_opt.setOnClickListener {
                itemView.linear_bottom_layout.visibility = View.GONE
                mList?.get(pos)?.isBottomPartHidden = false
            }
            itemView.rl_chat.setOnClickListener {
                if (orderHistoryDataItem?.status == 2) {
                    listener?.onClick(pos, itemView.rl_chat, orderHistoryDataItem)
                } else {

                }
            }
            itemView.rl_call.setOnClickListener {
                listener?.onClick(pos, itemView.rl_call, orderHistoryDataItem)
            }
            itemView.rl_whatsapp.setOnClickListener {
                listener?.onClick(pos, itemView.rl_whatsapp, orderHistoryDataItem)
            }
            itemView.text_view_details.setOnClickListener {
                listener?.onClick(pos, itemView.text_view_details, orderHistoryDataItem)
            }
        }
    }


}
