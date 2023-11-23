package com.mamits.apnaonlines.userv.ui.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mamits.apnaonlines.userv.model.response.BannerlistItem
import com.mamits.apnaonlines.userv.ui.fragment.BannerPagerFragment

class BannerPagerAdapter(
    val activity: Activity, fm: FragmentManager, var mList:ArrayList<BannerlistItem>
) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return mList?.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        return BannerPagerFragment.newInstance(activity, position, mList?.get(position))
    }

}


