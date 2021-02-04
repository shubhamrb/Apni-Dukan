package com.mponline.vendorApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.mponline.vendorApp.R
import com.mponline.vendorApp.listener.OnItemClickListener
import com.mponline.vendorApp.listener.OnSwichFragmentListener
import com.mponline.vendorApp.ui.base.BaseActivity
import com.mponline.vendorApp.ui.fragment.DrawerFragment
import com.mponline.vendorApp.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_toolbar.*


class RegisterActivity : BaseActivity(){

    override fun onNetworkChange(isConnected: Boolean) {

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

    }


}