package com.mponline.vendorApp.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mponline.vendorApp.ui.fragment.*
import com.mponline.vendorApp.utils.Constants


class OrderTabAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    internal var totalTabs: Int
) : FragmentStatePagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return OrderDetailListFragment.newInstance(myContext, Constants.ALL_ORDERS, null)
            }
            1 -> {
                return OrderDetailListFragment.newInstance(
                    myContext,
                    Constants.PENDING_ORDERS,
                    null
                )
            }
            2 -> {
                return OrderDetailListFragment.newInstance(
                    myContext,
                    Constants.COMPLETED_ORDERS,
                    null
                )
            }
            3 -> {
                return OrderDetailListFragment.newInstance(
                    myContext,
                    Constants.DECLINED_ORDERS,
                    null
                )
            }
            else -> {
                return OrderDetailListFragment.newInstance(
                    myContext,
                    Constants.DECLINED_ORDERS,
                    null
                )
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}