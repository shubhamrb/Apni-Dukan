package com.mponline.userApp.ui.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.StorelistItem
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.item_stores.view.*


class StoresAdapter(
    var context: Context?,
    val listener: OnItemClickListener,
    var mList:ArrayList<StorelistItem> = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationListImgsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_stores,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(!mList?.get(position)?.storelogo?.isNullOrEmpty()){
            ImageGlideUtils.loadUrlImage(context!!, mList?.get(position)?.storelogo, holder.itemView.image_store)
        }else if(!mList?.get(position)?.image?.isNullOrEmpty()){
            ImageGlideUtils.loadUrlImage(context!!, mList?.get(position)?.image, holder.itemView.image_store)
        }
        holder.itemView.text_store_name.text = mList?.get(position)?.name!!
        holder.itemView.text_store_loc.text = "${if(mList?.get(position)?.distance!! %1 == 0.0) mList?.get(position)?.distance?.toInt() else mList?.get(position)?.distance}KM away from you"
        holder.itemView.ratingbar_store.rating = if(mList?.get(position)?.ratting!=null && !mList?.get(position)?.ratting?.isNullOrEmpty() && !mList?.get(position)?.ratting?.equals("zero")) mList?.get(position)?.ratting?.toFloat() else 0f
        if(mList?.get(position)?.isAvailable!=null && !mList?.get(position)?.isAvailable?.isNullOrBlank()){
            if(mList?.get(position)?.isAvailable?.equals("1")){
                if(mList?.get(position)?.openstatus?.equals("1")){
                    holder.itemView.image_status.setImageResource(R.drawable.circle_green)
                    holder.itemView.text_store_status.text = "Open"
                }else{
                    holder.itemView.image_status.setImageResource(R.drawable.circle_red)
                    holder.itemView.text_store_status.text = "Close"
                }
            }else{
                holder.itemView.image_status.setImageResource(R.drawable.circle_red)
                holder.itemView.text_store_status.text = "Close"
            }
        }
       /* if(mList?.get(position)?.is_available!=null && !mList?.get(position)?.is_available?.isNullOrBlank()){
            if(mList?.get(position)?.is_available?.equals("1")) holder.itemView.image_status.setImageResource(R.drawable.circle_green) else holder.itemView.image_status.setImageResource(R.drawable.circle_red)
            holder.itemView.text_store_status.text = if(mList?.get(position)?.is_available?.equals("1")) "Open" else "Close"
        }*/
        if(mList?.get(position)?.price!=null && mList?.get(position)?.price > 0.0/*(!mList?.get(position)?.price?.isNullOrEmpty()) && (!mList?.get(position)?.price?.equals("0.00"))*/){
            holder.itemView.text_price.visibility = View.VISIBLE
            holder.itemView.text_price.text = "\u20B9 ${mList?.get(position)?.price}"
        }else{
            holder.itemView.text_price.visibility = View.GONE
        }
        holder.itemView.cv_store.setOnClickListener {
            listener?.onClick(position, holder.itemView.cv_store, mList?.get(position))
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

    class NotificationListImgsViewHolder(view: View) : RecyclerView.ViewHolder(view)


}
