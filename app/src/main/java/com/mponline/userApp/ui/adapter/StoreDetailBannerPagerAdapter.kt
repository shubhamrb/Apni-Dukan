package com.mponline.userApp.ui.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mponline.userApp.model.response.BannerlistItem
import com.mponline.userApp.ui.fragment.BannerPagerFragment
import com.mponline.userApp.ui.fragment.StoreDetailBannerPagerFragment

class StoreDetailBannerPagerAdapter(
    val activity: Activity, fm: FragmentManager, var mList:ArrayList<String>
) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return mList?.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        return StoreDetailBannerPagerFragment.newInstance(activity, position, mList?.get(position))
    }

}


