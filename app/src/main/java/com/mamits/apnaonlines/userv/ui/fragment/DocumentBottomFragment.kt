package com.mamits.apnaonlines.userv.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.response.DocumentData
import com.mamits.apnaonlines.userv.ui.adapter.DocumentAdapter
import kotlinx.android.synthetic.main.fragment_camera_gallery_dialog.list

// TODO: Customize parameter argument names

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    DocumentBottomFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [DocumentBottomFragment.Listener].
 */
class DocumentBottomFragment : BottomSheetDialogFragment(), OnItemClickListener {
    private var mListener: Listener? = null
    private var documentAdapter: DocumentAdapter? = null
    var mDocList: ArrayList<DocumentData>? = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_document_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mDocList?.clear()
        mDocList = arguments?.getSerializable("data") as ArrayList<DocumentData>?

        list.setHasFixedSize(true)
        list.layoutManager =
            LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
        documentAdapter = DocumentAdapter(
            activity,
            true,
            this
        )
        list.adapter = documentAdapter

        mDocList.let {
            if (it != null) {
                if (it.size >= 0) {
                    documentAdapter?.setList(mDocList!!)
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onCameraGalleryClicked(position: Int)
        fun onDocumentClicked(url: String, selectedDoc: String)
    }

    companion object {
        // TODO: Customize parameters
        fun newInstance(mDocList: ArrayList<DocumentData>?): DocumentBottomFragment =
            DocumentBottomFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", mDocList)
                }
            }
    }

    override fun onClick(pos: Int, view: View, url: Any?, file_name: String) {
        dismiss()
        mListener?.onDocumentClicked(url.toString(), file_name)
    }

    override fun onClick(pos: Int, view: View, file_name: Any?) {

    }

}
