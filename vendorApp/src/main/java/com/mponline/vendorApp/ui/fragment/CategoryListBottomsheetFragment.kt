package com.mponline.vendorApp.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mponline.vendorApp.R
import com.mponline.vendorApp.listener.OnItemClickListener
import com.mponline.vendorApp.ui.activity.AddServiceDetailActivity
import com.mponline.vendorApp.ui.activity.MainActivity
import com.mponline.vendorApp.ui.adapter.SearchHomeAdapter
import kotlinx.android.synthetic.main.layout_bottom_categorylist.*


class CategoryListBottomsheetFragment : BottomSheetDialogFragment(), OnItemClickListener {
    private var mListener: OnCategoryListItemSelectListener? = null
    private var mContext:Context? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_categorylist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view?.run {
           rv_search?.setHasFixedSize(true)
            rv_search?.layoutManager =
                LinearLayoutManager(
                    mContext,
                    RecyclerView.VERTICAL,
                    false
                )
            rv_search?.adapter = SearchHomeAdapter(
                mContext,
                this@CategoryListBottomsheetFragment
            )

            rv_search.visibility = View.GONE

            edt_search.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(s.toString().length!! >0){
                        rv_search.visibility = View.VISIBLE
                    }else{
                        rv_search.visibility = View.GONE
                    }
                }
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as OnCategoryListItemSelectListener
            mContext = context
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface OnCategoryListItemSelectListener {
        fun onItemSelect(obj:Any?)
    }


    companion object {

        fun newInstance(itemCount: Int): CategoryListBottomsheetFragment =
            CategoryListBottomsheetFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                    }
                }

    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        mContext?.startActivity(Intent(mContext, AddServiceDetailActivity::class.java))
    }

}
