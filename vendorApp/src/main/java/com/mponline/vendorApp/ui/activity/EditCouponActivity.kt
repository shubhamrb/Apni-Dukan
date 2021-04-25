package com.mponline.vendorApp.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.mponline.vendorApp.R
import com.mponline.vendorApp.listener.OnItemClickListener
import com.mponline.vendorApp.listener.OnSwichFragmentListener
import com.mponline.vendorApp.ui.adapter.ServicesFileDetailAdapter
import com.mponline.vendorApp.ui.adapter.ServicesTextDetailAdapter
import com.mponline.vendorApp.ui.base.BaseActivity
import com.mponline.vendorApp.ui.fragment.DrawerFragment
import com.mponline.vendorApp.ui.fragment.SetTimerBottomsheetFragment
import com.mponline.vendorApp.ui.fragment.UploadFilesBottomsheetFragment
import com.mponline.vendorApp.utils.Constants
import kotlinx.android.synthetic.main.activity_coupon_edit.*
import kotlinx.android.synthetic.main.common_detail_toolbar.*

class EditCouponActivity : BaseActivity(),  NavigationView.OnNavigationItemSelectedListener,
    OnSwichFragmentListener, OnItemClickListener,
    SetTimerBottomsheetFragment.OnTimerSubmitListener, UploadFilesBottomsheetFragment.OnTaskSubmit {

    override fun onNetworkChange(isConnected: Boolean) {

    }

    override fun onSwitchFragment(tag: String, type: String, obj: Any?, extras: Any?) {
        when (tag) {
//            Constants.HOME_PAGE -> {
//                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
//                ft.replace(R.id.rl_container_drawer, HomeFragment())
//                ft.commit()
//            }
            Constants.CLOSE_NAV_DRAWER -> {
                if(drawer_layout.isDrawerOpen(GravityCompat.START)){
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
            }
        }
    }

    override fun onSwitchFragmentParent(tag: String, type: String, obj: Any?, extras: Any?) {
        when (tag) {
//            Constants.MY_ACCOUNT_PAGE -> {
//                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
//                ft.add(R.id.rl_container_parent, AccountFragment())
//                ft.addToBackStack(Constants.MY_ACCOUNT_PAGE)
//                ft.commit()
//            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupon_edit)

        var fragment_navigation_drawer =
            supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as DrawerFragment
        fragment_navigation_drawer!!.setUpDrawer(
            R.id.fragment_navigation_drawer,
            findViewById<View>(R.id.drawer_layout) as DrawerLayout,
            toolbar!!
        )


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }

    override fun onTimerSubmit(hrs: String, mins: String, obj: Any?) {

    }

    override fun onTaskSubmit(obj: Any?) {

    }
}