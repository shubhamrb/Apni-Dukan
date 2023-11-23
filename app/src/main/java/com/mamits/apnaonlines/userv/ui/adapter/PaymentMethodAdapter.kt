package com.mamits.apnaonlines.userv.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.listener.OnSwichFragmentListener
import com.mamits.apnaonlines.userv.util.Constants


class PaymentMethodAdapter(
    var context: Context?,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_payment_method,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            if(context is OnSwichFragmentListener){
                var mOnSwichFragmentListener = context as OnSwichFragmentListener
                mOnSwichFragmentListener.onSwitchFragment(Constants.PAYMENT_DETAIL_PAGE, Constants.WITH_NAV_DRAWER, null, null)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    override fun getItemCount(): Int {
        return 6
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NotificationListImgsViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
