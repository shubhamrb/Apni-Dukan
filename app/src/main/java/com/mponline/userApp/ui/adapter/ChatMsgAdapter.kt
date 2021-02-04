package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import kotlinx.android.synthetic.main.item_msg_incomming.view.*
import kotlinx.android.synthetic.main.item_msg_outgoing.view.*
import kotlinx.android.synthetic.main.item_sub_store.view.*


class ChatMsgAdapter(
    var context: Context?,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1
    var VIEW_INCOMMING = 1
    var VIEW_OUTGOING = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_INCOMMING) {
            return IncommingMsgViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_msg_incomming,
                    parent,
                    false
                )
            )
        } else {
            return OutgoingMsgViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_msg_outgoing,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IncommingMsgViewHolder) {
            if(position == 0){
                holder.itemView.rl_incomming_file_part.visibility = View.GONE
            }
        } else if (holder is OutgoingMsgViewHolder) {
            if(position == 4){
                holder.itemView.rl_outgoing_file_part.visibility = View.GONE
            }
        } else {

        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    override fun getItemCount(): Int {
        return 10
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 1 && position == 4) {
            return VIEW_OUTGOING
        } else {
            return VIEW_INCOMMING
        }
    }

    class IncommingMsgViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class OutgoingMsgViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
