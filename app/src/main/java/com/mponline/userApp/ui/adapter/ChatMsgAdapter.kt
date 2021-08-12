package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.ChatListDataItem
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.DateUtils
import com.mponline.userApp.utils.ImageGlideUtils
import com.mponline.userApp.utils.PreferenceUtils
import kotlinx.android.synthetic.main.activity_img_preview.*
import kotlinx.android.synthetic.main.item_msg_incomming.view.*
import kotlinx.android.synthetic.main.item_msg_outgoing.view.*


class ChatMsgAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList: List<ChatListDataItem>? = ArrayList(),
    var mPreferenceUtils: PreferenceUtils
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1
    var VIEW_INCOMMING = 1
    var VIEW_OUTGOING = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType != VIEW_INCOMMING) {
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
            holder?.itemView?.text_msg_incomming?.text = mList?.get(position)?.message
            holder?.itemView?.text_msg_time_incommming?.text = DateUtils.changeDateFormat(mList?.get(position)?.updatedAt, "yyyy-MM-dd hh:mm:ss", "dd MMM yyyy hh:mm a")
            if(mList?.get(position)?.attachment?.isNullOrEmpty()!!){
                holder.itemView.rl_incomming_file_part.visibility = View.GONE
            }else{
                holder.itemView.rl_incomming_file_part.visibility = View.VISIBLE
                holder.itemView.text_filename_incomming.text = CommonUtils.getFileName(mList?.get(position)?.attachment!!)
                holder.itemView.text_filesize_incomming.text = mList?.get(position)?.fileType!!
                if(mList?.get(position)?.attachment?.endsWith("jpg",true)!! || mList?.get(position)?.attachment?.endsWith("jpeg",true)!! || mList?.get(position)?.attachment?.endsWith("png",true)!!){
                   if(mList?.get(position)?.attachment?.contains("http://")!! || mList?.get(position)?.attachment?.contains("https://")!!){
                       ImageGlideUtils.loadLocalImage(context!!, mList?.get(position)?.attachment!!, holder.itemView.image_file_preview_incomming!!)
                   }else{
                       ImageGlideUtils.loadLocalImage(context!!, mList?.get(position)?.attachment!!, holder.itemView.image_file_preview_incomming!!)
                   }
                }else{
                    holder.itemView.image_file_preview_incomming.setImageResource(R.drawable.ic_file_preview)
                }
            }
            holder.itemView.rl_incomming_file_part.setOnClickListener {
                listener?.onClick(position, holder.itemView.image_download_file_incomming, mList?.get(position))
            }
        } else if (holder is OutgoingMsgViewHolder) {
            holder?.itemView?.text_msg?.text = mList?.get(position)?.message
            holder?.itemView?.text_outgoing_msg_time?.text = DateUtils.changeDateFormat(mList?.get(position)?.updatedAt, "yyyy-MM-dd hh:mm:ss", "dd MMM yyyy hh:mm a")
            if(mList?.get(position)?.attachment?.isNullOrEmpty()!!){
                holder.itemView.rl_outgoing_file_part.visibility = View.GONE
            }else{
                holder.itemView.rl_outgoing_file_part.visibility = View.VISIBLE
                holder.itemView.text_filename.text = CommonUtils.getFileName(mList?.get(position)?.attachment!!)
                holder.itemView.text_filesize.text = mList?.get(position)?.fileType!!
                if(mList?.get(position)?.attachment?.endsWith("jpg",true)!! || mList?.get(position)?.attachment?.endsWith("jpeg",true)!! || mList?.get(position)?.attachment?.endsWith("png",true)!!){
                    ImageGlideUtils.loadLocalImage(context!!, mList?.get(position)?.attachment!!, holder.itemView.image_file_preview!!)
                }else{
                    holder.itemView.image_file_preview.setImageResource(R.drawable.ic_file_preview)
                }
            }
            holder.itemView.rl_outgoing_file_part.setOnClickListener {
                listener?.onClick(position, holder.itemView.image_download_file, mList?.get(position))
            }
        } else {

        }
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }


    override fun getItemCount(): Int {
        return mList?.size!!
    }

    fun refreshList(list: List<ChatListDataItem>){
        mList = list
        notifyItemRangeChanged(0, list?.size)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (!mList?.get(position)?.fromUser?.equals(mPreferenceUtils?.getValue(Constants.USER_ID)!!)!!) {
            return VIEW_OUTGOING
        } else {
            return VIEW_INCOMMING
        }
    }

    class IncommingMsgViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class OutgoingMsgViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
