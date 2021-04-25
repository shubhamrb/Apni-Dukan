package com.mponline.vendorApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.mponline.vendorApp.R
import com.mponline.vendorApp.listener.OnItemClickListener
import com.mponline.vendorApp.listener.OnSwichFragmentListener
import com.mponline.vendorApp.ui.adapter.OrderTabAdapter
import com.mponline.vendorApp.ui.base.BaseActivity
import com.mponline.vendorApp.ui.fragment.DrawerFragment
import com.mponline.vendorApp.utils.Constants
import kotlinx.android.synthetic.main.activity_orderdetail_list.*
import kotlinx.android.synthetic.main.common_toolbar.*


class OrderDetailListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
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

    var mType:String = ""
    var setPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderdetail_list)

        if(intent?.hasExtra("type")!!){
            mType = intent?.getStringExtra("type")!!
            when(mType){
                Constants.ALL_ORDERS->{
                    setPosition =0
                }
                Constants.PENDING_ORDERS->{
                    setPosition =1
                }
                Constants.COMPLETED_ORDERS->{
                    setPosition =2
                }
                Constants.DECLINED_ORDERS->{
                    setPosition =3
                }
            }
        }

        var fragment_navigation_drawer =
            supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as DrawerFragment
        fragment_navigation_drawer!!.setUpDrawer(
            R.id.fragment_navigation_drawer,
            findViewById<View>(R.id.drawer_layout) as DrawerLayout,
            toolbar!!
        )

        toolbar.setNavigationOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }


        tabLayout!!.addTab(tabLayout!!.newTab().setText("All"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Pending"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Completed"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Declined"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = OrderTabAdapter(this, supportFragmentManager, tabLayout!!.tabCount)
        viewPager!!.adapter = adapter

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        viewPager?.setCurrentItem(setPosition)
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