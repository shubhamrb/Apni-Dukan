package com.mponline.userApp.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.BannerlistItem
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.item_banner.view.*
import kotlinx.android.synthetic.main.item_banner.view.image_banner
import kotlinx.android.synthetic.main.item_banner_store.view.*

class StoreDetailBannerPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            val view = inflater.inflate(R.layout.item_banner_store, container, false)
            arguments?.let {
                val data: String? = arguments?.getString("obj")
                data?.run {
                   ImageGlideUtils.loadUrlImage(activity!!, data, view?.image_store_banners!!)
                }
                var mOnItemClickListener: OnItemClickListener? = activity as OnItemClickListener
            }
            return view
    }

    companion object {
        fun newInstance(
            context: Activity,
            pos: Int,
            img: String
        ): Fragment {
            val fragment = StoreDetailBannerPagerFragment()
            val bundle = Bundle()
            bundle.putInt("pos", pos)
            bundle.putString("obj", img)
            fragment.arguments = bundle
            return fragment
        }
    }


}
