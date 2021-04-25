package com.mponline.vendorApp.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mponline.vendorApp.ui.fragment.*
import com.mponline.vendorApp.utils.Constants


class PaymentsTabAdapter(
    private val myContext: Context,
    fm: FragmentManager,
    internal var totalTabs: Int
) : FragmentStatePagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return PaymentDetailListFragment.newInstance(myContext, Constants.ALL_TXNS, null)
            }
            1 -> {
                return PaymentDetailListFragment.newInstance(
                    myContext,
                    Constants.DUE_TXNS,
                    null
                )
            }
            2 -> {
                return PaymentDetailListFragment.newInstance(
                    myContext,
                    Constants.RECIEVED_TXNS,
                    null
                )
            }
            3 -> {
                return PaymentDetailListFragment.newInstance(
                    myContext,
                    Constants.SETTLED_TXNS,
                    null
                )
            }
            else -> {
                return PaymentDetailListFragment.newInstance(
                    myContext,
                    Constants.ALL_TXNS,
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