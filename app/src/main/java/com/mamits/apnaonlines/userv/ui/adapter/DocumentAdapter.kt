package com.mamits.apnaonlines.userv.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.response.DocumentData
import com.mamits.apnaonlines.userv.ui.activity.FilePreviewActivity
import com.mamits.apnaonlines.userv.util.ImageGlideUtils
import kotlinx.android.synthetic.main.item_documents.view.btn_delete
import kotlinx.android.synthetic.main.item_documents.view.image_doc
import kotlinx.android.synthetic.main.item_documents.view.text_doc_name


class DocumentAdapter(
    var context: Context?,
    private val isDocSelector: Boolean,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mList: ArrayList<DocumentData> = ArrayList()
    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_documents,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mList.get(position).file.isNotEmpty()) {
            if (mList.get(position).file_name.contains("png")
                || mList.get(position).file_name.contains("jpg")
                || mList.get(position).file_name.contains("jpeg")
            ) {
                ImageGlideUtils.loadUrlImage(
                    context!!,
                    mList[position].file,
                    holder.itemView.image_doc
                )
            } else {
                holder.itemView.image_doc.setImageResource(R.drawable.black_file_24)
            }

        }
        holder.itemView.text_doc_name.text = mList[position].name

        if (!isDocSelector) {
            holder.itemView.btn_delete.visibility = View.VISIBLE
        } else {
            holder.itemView.btn_delete.visibility = View.GONE
        }
        holder.itemView.btn_delete.setOnClickListener {
            listener.onClick(position, holder.itemView.btn_delete, mList[position].id)
        }
        holder.itemView.setOnClickListener {
            if (isDocSelector) {
                listener.onClick(
                    position,
                    holder.itemView,
                    mList[position].file,
                    mList[position].file_name
                )
            }else{
                var intent: Intent = Intent(context, FilePreviewActivity::class.java)
                intent.putExtra("file", mList[position].file)
                intent.putExtra("from", "file")
                context?.startActivity(intent)
            }
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

    public fun setList(list: ArrayList<DocumentData>) {
        mList = list
        notifyDataSetChanged()

    }

    class NotificationListImgsViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
