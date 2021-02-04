package com.mponline.userApp.ui.fragment

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
import com.mponline.userApp.R

const val ARG_ITEM_COUNT = "item_count"

class OtpBottomsheetFragment : BottomSheetDialogFragment() {
    private var mListener: OtpListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottom_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as OtpListener
        } else {
            mListener = context as OtpListener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface OtpListener {
        fun onOtpverify(obj:Any?)
        fun onResendOtp(obj:Any?)
    }


    companion object {

        fun newInstance(itemCount: Int): OtpBottomsheetFragment =
            OtpBottomsheetFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                    }
                }

    }

}
