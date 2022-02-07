package com.mamits.apnaonlines.user.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.model.CustomFieldObj
import kotlinx.android.synthetic.main.custom_spinner_layout.view.*

class CustomeSpinnerAdapter(ctx: Context,
                       moods: List<CustomFieldObj.ValueObj>) :
    ArrayAdapter<CustomFieldObj.ValueObj>(ctx, 0, moods) {

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {

        val mood = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.custom_spinner_layout,
            parent,
            false
        )

        view.textview_spn.text = mood?.value

        return view
    }

}


//class CustomeSpinnerAdapter(context: Context, var items: ArrayList<CustomFieldObj.ValueObj>)
//    : ArrayAdapter<CustomFieldObj.ValueObj>(context, 0, items) {
//
//    val inflater: LayoutInflater = LayoutInflater.from(context)
//
//    // If required, get the ID from your Model. If your desired return value can't be converted to long use getItem(int) instead
////    override fun getItemId(position: Int): Long {
////        return position.toLong()
////    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var view: View? = convertView
//        if (view == null) {
//            view = LayoutInflater.from(context).inflate(R.layout.custom_spinner_layout, parent!!, false)
//        }
//        (view?.findViewById(R.id.textview_spn) as TextView).text = getItem(position)?.value
//        return view
//    }
//
//    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
//        var view: View? = convertView
//        if (view == null) {
//            view = inflater.inflate(R.layout.custom_spinner_layout, parent, false)
//        }
//        (view?.findViewById(R.id.textview_spn) as TextView).text = getItem(position)?.value
//        return view
//    }
//}