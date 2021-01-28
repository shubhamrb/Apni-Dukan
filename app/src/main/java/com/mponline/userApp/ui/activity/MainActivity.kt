package com.mponline.userApp.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.ui.base.BaseActivity
import com.recyclemybin.utils.Constants
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.drawer_layout.*
import kotlinx.android.synthetic.main.drawer_layout.view.*

class MainActivity :BaseActivity(),  NavigationView.OnNavigationItemSelectedListener, OnSwichFragmentListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }

    override fun onSwitchFragment(tag: String, type:String, obj: Any?, extras: Any?) {
        when(tag){
            Constants.HOME_PAGE->{

            }
        }
    }

    override fun onSwitchFragmentParent(tag: String, type:String, obj: Any?, extras: Any?) {
        when(tag){

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_layout)

        setNavigationDrawer()

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){

            }
            true
        }
    }

    private fun setNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.ic_drawer)
//        toggle.drawerArrowDrawable.color = Color.WHITE
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        toolbar.setNavigationOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }

        nav_view.setNavigationItemSelectedListener(this@MainActivity)

//        refreshDrawer()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }



}