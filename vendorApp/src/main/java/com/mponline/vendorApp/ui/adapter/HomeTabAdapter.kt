package com.mponline.vendorApp.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mponline.vendorApp.ui.fragment.HomeFragment
import com.mponline.vendorApp.ui.fragment.OrderFragment
import com.mponline.vendorApp.ui.fragment.ServicesFragment


class HomeTabAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentStatePagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return HomeFragment()
            }
            1 -> {
                return OrderFragment()
            }
            2 -> {
                // val movieFragment = MovieFragment()
                return HomeFragment()
            }
            3 -> {
                // val movieFragment = MovieFragment()
                return ServicesFragment()
            }
            4 -> {
                // val movieFragment = MovieFragment()
                return HomeFragment()
            }
            5 -> {
                // val movieFragment = MovieFragment()
                return HomeFragment()
            }
            else ->{
                return HomeFragment()
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}