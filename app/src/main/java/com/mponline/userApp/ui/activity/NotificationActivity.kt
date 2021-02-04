package com.mponline.userApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.ui.adapter.NotificationAdapter
import com.mponline.userApp.ui.adapter.StoresAdapter
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.ui.fragment.*
import com.mponline.userApp.utils.Constants
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bottom_navigation
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_instruction.view.*


class NotificationActivity : BaseActivity(), OnItemClickListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        image_back.setOnClickListener {
            onBackPressed()
        }
        toolbar_title.text = resources?.getString(R.string.notification)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {

            }
            true
        }


        //UnRead
        rv_unread_notification?.setHasFixedSize(true)
        rv_unread_notification?.layoutManager =
            LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
            )
        rv_unread_notification?.adapter = NotificationAdapter(
            this,
            this, "unread"
        )

        //Read
        rv_read_notification?.setHasFixedSize(true)
        rv_read_notification?.layoutManager =
            LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
            )
        rv_read_notification?.adapter = NotificationAdapter(
            this,
            this, "read"
        )

    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }


}