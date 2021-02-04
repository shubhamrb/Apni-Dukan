package com.mponline.userApp.ui.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mponline.userApp.ui.fragment.BannerPagerFragment

class BannerPagerAdapter(
    val activity: Activity, fm: FragmentManager
) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return 5
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        return BannerPagerFragment.newInstance(activity, position)
    }

}


