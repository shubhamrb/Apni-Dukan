package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.CustomFieldObj
import kotlinx.android.synthetic.main.item_fileupload.view.*


class CustomFileAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList: ArrayList<CustomFieldObj>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_fileupload,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (!mList?.get(position)?.selectedFilePath?.isNullOrEmpty()!!) {
            holder.itemView.ll_selected_file.visibility = View.VISIBLE
            holder.itemView.text_filename.text = mList?.get(position)?.selectedFilePath
        } else {
            holder.itemView.ll_selected_file.visibility = View.GONE
        }

        holder.itemView.text_file_label.text = mList?.get(position)?.hintName
        holder.itemView.text_file_upload.setOnClickListener {
            listener.onClick(position, holder.itemView.text_file_upload, mList?.get(position))
        }
        holder.itemView.image_file_close.setOnClickListener {
            listener.onClick(position, holder.itemView.image_file_close, mList?.get(position))
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

    fun onRefreshAdapter(list: ArrayList<CustomFieldObj>) {
        mList = list
        notifyDataSetChanged()
    }

    class NotificationListImgsViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
