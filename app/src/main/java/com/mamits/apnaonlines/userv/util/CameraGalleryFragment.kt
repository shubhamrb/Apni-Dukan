package com.mamits.apnaonlines.userv.util

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mamits.apnaonlines.userv.R
import kotlinx.android.synthetic.main.fragment_camera_gallery_dialog.*
import kotlinx.android.synthetic.main.fragment_camera_gallery_item.view.*

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    CameraGalleryFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 *
 * You activity (or fragment) needs to implement [CameraGalleryFragment.Listener].
 */
class CameraGalleryFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera_gallery_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = arguments?.getInt(ARG_ITEM_COUNT)?.let { CameraGalleryAdapter(it) }
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
    }

    private inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_camera_gallery_item, parent, false)) {

        internal val text: TextView = itemView.text_name
        internal val image: ImageView = itemView.image_icon
        internal val relative: RelativeLayout = itemView.relative_cam_gallery

        init {
            relative.setOnClickListener { it ->
                mListener?.let {
                    it.onCameraGalleryClicked(adapterPosition)
                    dismiss()
                }
            }
        }
    }

    private inner class CameraGalleryAdapter(private val mItemCount: Int) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == 0) {
                holder.text.text = getString(R.string.camera_name)
                holder.image.setImageResource(R.drawable.ic_camera_option)
            } else if (position == 1) {
                holder.text.text = getString(R.string.gallery)
                holder.image.setImageResource(R.drawable.ic_gallery)
            } else {
                holder.text.text = getString(R.string.my_docs)
                holder.image.setImageResource(R.drawable.ic_file_24)
            }
        }

        override fun getItemCount(): Int {
            return mItemCount
        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): CameraGalleryFragment =
                CameraGalleryFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                    }
                }

    }

}
