package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mponline.userApp.R

class BannerPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            val view = inflater.inflate(R.layout.item_banner, container, false)
//            arguments?.let {
//                val pos: Int = arguments!!.getInt("pos")
//                val data: StartUpScreenModel? = arguments?.getParcelable<StartUpScreenModel>("data")
//                data?.run {
//                    view.text_startup_msg.text = data?.title
//                    view.image_startup.setImageResource(this.image)
//
//                }
//            }
            return view
    }

    companion object {
        fun newInstance(
            context: Activity,
            pos: Int
        ): Fragment {
            val fragment = BannerPagerFragment()
            val bundle = Bundle()
            bundle.putInt("pos", pos)
            fragment.arguments = bundle
            return fragment
        }
    }


}
