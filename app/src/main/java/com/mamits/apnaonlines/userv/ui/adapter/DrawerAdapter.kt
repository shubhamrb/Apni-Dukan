package com.mamits.apnaonlines.userv.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.DrawerModel
import kotlinx.android.synthetic.main.item_drawer.view.*

class DrawerAdapter(private val context: Context, arrayList: ArrayList<DrawerModel>, var listener: OnItemClickListener) : RecyclerView.Adapter<DrawerAdapter.ViewHolder>() {

    internal var arrayList = ArrayList<DrawerModel>()
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
        this.arrayList = arrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_drawer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.title.setText(arrayList[position].getNames())
        holder.ivicon.setImageResource(arrayList[position].getImages())

        holder.itemView.setOnClickListener {
            listener?.onClick(position, holder?.itemView?.name, arrayList?.get(position))
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var ivicon: ImageView

        init {
            title = itemView.findViewById(R.id.name) as TextView
            ivicon = itemView.findViewById(R.id.ivicon) as ImageView
        }
    }
}