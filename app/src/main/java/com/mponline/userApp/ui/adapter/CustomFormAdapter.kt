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
import kotlinx.android.synthetic.main.item_edittext.view.*
import kotlinx.android.synthetic.main.item_service.view.*


class CustomFormAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList:ArrayList<CustomFieldObj>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_edittext,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder.itemView.edt_custom_field.hint = mList?.get(position)?.hintName
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

    class NotificationListImgsViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
