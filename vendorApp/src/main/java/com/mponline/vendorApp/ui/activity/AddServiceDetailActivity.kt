package com.mponline.vendorApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.mponline.vendorApp.R
import com.mponline.vendorApp.listener.OnItemClickListener
import com.mponline.vendorApp.listener.OnSwichFragmentListener
import com.mponline.vendorApp.ui.adapter.HomeTabAdapter
import com.mponline.vendorApp.ui.adapter.ServicesAdapter
import com.mponline.vendorApp.ui.base.BaseActivity
import com.mponline.vendorApp.ui.fragment.DrawerFragment
import com.mponline.vendorApp.utils.Constants
import kotlinx.android.synthetic.main.activity_add_service_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.fragment_services.view.*


class AddServiceDetailActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnSwichFragmentListener, OnItemClickListener {

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
                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
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
        setContentView(R.layout.activity_add_service_detail)


        var fragment_navigation_drawer =
            supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as DrawerFragment
        fragment_navigation_drawer!!.setUpDrawer(
            R.id.fragment_navigation_drawer,
            findViewById<View>(R.id.drawer_layout) as DrawerLayout,
            toolbar!!
        )

        toolbar.setNavigationOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }


        rv_service?.setHasFixedSize(true)
        rv_service?.layoutManager =
            GridLayoutManager(
                this, 3
            )
        rv_service?.adapter = ServicesAdapter(
            this,
            this
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }


}