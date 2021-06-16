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

class BannerPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            val view = inflater.inflate(R.layout.item_banner, container, false)
            arguments?.let {
                val pos: Int = arguments!!.getInt("pos")
                val data: BannerlistItem? = arguments?.getParcelable<BannerlistItem>("obj")
                data?.run {
                   ImageGlideUtils.loadUrlImage(activity!!, image, view?.image_banner!!)
                }
                var mOnItemClickListener: OnItemClickListener? = activity as OnItemClickListener

                view?.rl_banner?.setOnClickListener {
                    if(mOnItemClickListener!=null){
                        mOnItemClickListener?.onClick(pos, view?.rl_banner!!, data)
                    }
                }
            }
            return view
    }

    companion object {
        fun newInstance(
            context: Activity,
            pos: Int,
            mBannerlistItem: BannerlistItem
        ): Fragment {
            val fragment = BannerPagerFragment()
            val bundle = Bundle()
            bundle.putInt("pos", pos)
            bundle.putParcelable("obj", mBannerlistItem)
            fragment.arguments = bundle
            return fragment
        }
    }


}
