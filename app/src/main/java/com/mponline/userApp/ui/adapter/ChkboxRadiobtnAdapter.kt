package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import kotlinx.android.synthetic.main.item_chkbox.view.*
import kotlinx.android.synthetic.main.item_radiobtn.view.*
import kotlinx.android.synthetic.main.item_stores.view.*


class ChkboxRadiobtnAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList: List<String>,
    var type: String,
    var selectedItems:String,
    var parentPos:Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var VIEWTYPE_CHKBOX = 1;
    var VIEWTYPE_RADIOBTN = 2;
    init {
         VIEWTYPE_CHKBOX = 1;
         VIEWTYPE_RADIOBTN = 2;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEWTYPE_CHKBOX) {
            return ChkBoxViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_chkbox,
                    parent,
                    false
                )
            )
        } else {
            return RadioBtnViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.item_radiobtn,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChkBoxViewHolder) {
            holder.itemView.chkbox_item.text = mList?.get(position)?.trim()
            holder.itemView.chkbox_item.isChecked = selectedItems?.contains(mList?.get(position)?.trim())
            holder.itemView.chkbox_item.setOnCheckedChangeListener { _, isChecked ->
                listener?.onClick(parentPos, holder.itemView.chkbox_item, mList?.get(position))
            }
        } else if (holder is RadioBtnViewHolder) {
            holder.itemView.rbtn_item.text = mList?.get(position)?.trim()
            holder.itemView.rbtn_item.isChecked = selectedItems?.contains(mList?.get(position)?.trim())
            holder.itemView.rbtn_item.setOnCheckedChangeListener { compoundButton, b ->
                listener?.onClick(parentPos, holder.itemView.rbtn_item, mList?.get(position))
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

    override fun getItemViewType(position: Int): Int {
        if (type == "chkbox") {
            return VIEWTYPE_CHKBOX
        } else {
            return VIEWTYPE_RADIOBTN
        }
    }

    class ChkBoxViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class RadioBtnViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
