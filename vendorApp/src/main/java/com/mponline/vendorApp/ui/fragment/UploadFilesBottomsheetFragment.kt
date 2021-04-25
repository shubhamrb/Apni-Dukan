package com.mponline.vendorApp.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mponline.vendorApp.R
import com.mponline.vendorApp.listener.OnItemClickListener
import com.mponline.vendorApp.ui.activity.AddServiceDetailActivity
import com.mponline.vendorApp.ui.activity.MainActivity
import com.mponline.vendorApp.ui.adapter.SearchHomeAdapter
import com.mponline.vendorApp.ui.adapter.UploadFilesAdapter
import kotlinx.android.synthetic.main.layout_bottom_categorylist.*
import kotlinx.android.synthetic.main.layout_submit_files_bottomsheet.view.*


class UploadFilesBottomsheetFragment : BottomSheetDialogFragment(), OnItemClickListener {
    private var mListener: OnTaskSubmit? = null
    private var mContext:Context? = null
    var mView:View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_submit_files_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mView = view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mView?.run {
            rv_files.setHasFixedSize(true)
            rv_files.layoutManager = GridLayoutManager(context, 3)
            rv_files.adapter = UploadFilesAdapter(context, this@UploadFilesBottomsheetFragment)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as OnTaskSubmit
            mContext = context
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface OnTaskSubmit {
        fun onTaskSubmit(obj:Any?)
    }


    companion object {

        fun newInstance(itemCount: Int): UploadFilesBottomsheetFragment =
            UploadFilesBottomsheetFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                    }
                }

    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        mContext?.startActivity(Intent(mContext, AddServiceDetailActivity::class.java))
    }

}
