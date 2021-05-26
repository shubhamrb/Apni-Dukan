package com.mponline.userApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnImgPreviewListener
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.ImgPreviewPojo
import com.mponline.userApp.model.PrePlaceOrderPojo
import com.mponline.userApp.model.response.CategorylistItem
import com.mponline.userApp.model.response.ProductListItem
import com.mponline.userApp.model.response.StoreDetailDataItem
import com.mponline.userApp.model.response.StorelistItem
import com.mponline.userApp.ui.adapter.SearchHomeAdapter
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.ui.base.FusedLocationActivity
import com.mponline.userApp.ui.fragment.*
import com.mponline.userApp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnSwichFragmentListener, OnItemClickListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mOnImgPreviewListener: OnImgPreviewListener? = null

    override fun onStartNewActivity(listener: OnImgPreviewListener, imgPath: String) {
        super.onStartNewActivity(listener, imgPath)
        if(listener!=null && imgPath!=null){
            mOnImgPreviewListener = listener
            var intent:Intent = Intent(this@MainActivity, ImgPreviewActivity::class.java)
            intent?.putExtra("img", imgPath)
            startActivityForResult(intent, Constants.RESULT_IMG_PREVIEW)
        }
    }

    override fun onSwitchFragment(tag: String, type: String, obj: Any?, extras: Any?) {
        app_bar_common.visibility = View.VISIBLE
        when (tag) {
            Constants.HOME_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.replace(R.id.rl_container_drawer, HomeFragment())
                ft.commit()
            }
            Constants.SERVICE_PAGE -> {
                if(obj!=null && obj is CategorylistItem){
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(R.id.rl_container_drawer, ServiceFragment.newInstance(this@MainActivity, obj))
                    ft.addToBackStack(Constants.SERVICE_PAGE)
                    ft.commit()
                }
            }
            Constants.STORE_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, StoresFragment.newInstance(this@MainActivity))
                ft.addToBackStack(Constants.STORE_PAGE)
                ft.commit()
            }
            Constants.STORE_PAGE_BY_PROD -> {
                if(obj!=null && obj is ProductListItem){
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(R.id.rl_container_drawer, StoresFragment.newInstance(this@MainActivity, obj))
                    ft.addToBackStack(Constants.STORE_PAGE_BY_PROD)
                    ft.commit()
                }
            }
            Constants.SUB_SERVICE_PAGE -> {
                if(obj!=null && obj is CategorylistItem && extras!=null && extras is CategorylistItem){
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(R.id.rl_container_drawer, SubServiceFragment.newInstance(this@MainActivity, obj, extras))
                    ft.addToBackStack(Constants.SUB_SERVICE_PAGE)
                    ft.commit()
                }
            }
            Constants.INSTRUCTION_PAGE -> {
                if(obj!=null && extras !=null && obj is StoreDetailDataItem && extras is ProductListItem){
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(R.id.rl_container_drawer, InstructionFragment.newInstance(this, obj, extras))
                    ft.addToBackStack(Constants.INSTRUCTION_PAGE)
                    ft.commit()
                }
            }
            Constants.CUSTOM_FOEMS_PAGE -> {
                if(obj!=null && obj is PrePlaceOrderPojo){
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(R.id.rl_container_drawer, CustomFormFragment.newInstance(this, obj))
                    ft.addToBackStack(Constants.CUSTOM_FOEMS_PAGE)
                    ft.commit()
                }
            }
            Constants.ORDER_HISTORY_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, OrderHistoryFragment())
                ft.addToBackStack(Constants.ORDER_HISTORY_PAGE)
                ft.commit()
            }
            Constants.PAYMENT_DETAIL_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, PaymentDetailFragment())
                ft.addToBackStack(Constants.PAYMENT_DETAIL_PAGE)
                ft.commit()
            }
            Constants.PAYMENT_SUMMARY_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, PaymentSummaryFragment())
                ft.addToBackStack(Constants.PAYMENT_SUMMARY_PAGE)
                ft.commit()
            }
            Constants.STORE_DETAIL_PAGE -> {
                if(obj!=null && obj is StorelistItem){
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(R.id.rl_container_drawer, StoreDetailFragment.newInstance(this@MainActivity, obj))
                    ft.addToBackStack(Constants.STORE_DETAIL_PAGE_WITH_PROD)
                    ft.commit()
                }
            }
            Constants.STORE_DETAIL_PAGE_WITH_PROD -> {
                if(obj!=null && obj is StorelistItem && extras!=null && extras is ProductListItem){
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(R.id.rl_container_drawer, StoreDetailFragment.newInstance(this@MainActivity, obj, extras))
                    ft.addToBackStack(Constants.STORE_DETAIL_PAGE_WITH_PROD)
                    ft.commit()
                }
            }
            Constants.CHAT_MSG_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, ChatMsgFragment.newInstance(this@MainActivity, obj!!))
                ft.addToBackStack(Constants.CHAT_MSG_PAGE)
                ft.commit()
            }
            Constants.COUPON_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, CouponFragment())
                ft.addToBackStack(Constants.COUPON_PAGE)
                ft.commit()
            }
            Constants.CLOSE_NAV_DRAWER -> {
               if(drawer_layout.isDrawerOpen(GravityCompat.START)){
                   drawer_layout.closeDrawer(GravityCompat.START)
               }
            }
        }
    }

    override fun onSwitchFragmentParent(tag: String, type: String, obj: Any?, extras: Any?) {
        app_bar_common.visibility = View.GONE
        when (tag) {
            Constants.DOWNLOAD_LIST_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, DownloadListFragment())
                ft.addToBackStack(Constants.DOWNLOAD_LIST_PAGE)
                ft.commit()
            }
            Constants.CHAT_HOME_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, ChatHomeFragment())
                ft.addToBackStack(Constants.CHAT_HOME_PAGE)
                ft.commit()
            }
            Constants.MY_ACCOUNT_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, AccountFragment())
                ft.addToBackStack(Constants.MY_ACCOUNT_PAGE)
                ft.commit()
            }
        }
    }

    override fun onSwichToolbar(tag: String, type: String, obj: Any?) {
        when(tag){
            Constants.HIDE_NAV_DRAWER_TOOLBAR->{
                app_bar_common.visibility = View.GONE
            }
            Constants.SHOW_NAV_DRAWER_TOOLBAR->{
                app_bar_common.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setNavigationDrawer()

        image_notification.setOnClickListener {
            startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            supportFragmentManager?.popBackStack();
            when (it.itemId) {
                R.id.nav_home ->{
                    onSwitchFragment(Constants.HOME_PAGE, "", null, null)
                }
                R.id.nav_chat ->{
                    onSwitchFragmentParent(Constants.CHAT_HOME_PAGE, "", null, null)
                }
                R.id.nav_history ->{
                    onSwitchFragment(Constants.ORDER_HISTORY_PAGE, "", null, null)
                }
                R.id.nav_nearby ->{
                    onSwitchFragment(Constants.STORE_PAGE, "", null, null)
                }
                R.id.nav_acc ->{
                    onSwitchFragmentParent(Constants.MY_ACCOUNT_PAGE, "", null, null)
                }
            }
            true
        }

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.rl_container_drawer, HomeFragment())
        ft.commit()

        var fragment_navigation_drawer =
            supportFragmentManager.findFragmentById(R.id.fragment_navigation_drawer) as DrawerFragment
        fragment_navigation_drawer!!.setUpDrawer(
            R.id.fragment_navigation_drawer,
            findViewById<View>(R.id.drawer_layout) as DrawerLayout,
            toolbar!!
        )

        toolbar.setNavigationOnClickListener { drawer_layout.openDrawer(GravityCompat.START) }

        supportFragmentManager.addOnBackStackChangedListener {
            val f = supportFragmentManager.findFragmentById(R.id.rl_container_drawer)
            supportFragmentManager.fragments.lastOrNull()?.onStart()
//            when (f?.tag) {
//                Constants.HOME_PAGE -> {
//                    supportFragmentManager.fragments.lastOrNull()?.onResume()
//                }
//                Constants.SERVICE_PAGE -> {
//                    supportFragmentManager.fragments.lastOrNull()?.onResume()
//                }
//            }
        }
    }

    /*private fun setNavigationDrawer() {
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

//        nav_view.setNavigationItemSelectedListener(this@MainActivity)

//        refreshDrawer()
        rv_search?.setHasFixedSize(true)
        rv_search?.layoutManager =
            LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
            )
        rv_search?.adapter = SearchHomeAdapter(
            this,
            this
        )

        rv_search.visibility = View.GONE

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length!! >0){
                    rv_search.visibility = View.VISIBLE
                }else{
                    rv_search.visibility = View.GONE
                }
            }
        })
    }*/

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
        if(resultCode == RESULT_OK){
            when(requestCode){
                Constants.RESULT_IMG_PREVIEW->{
                    if(data?.hasExtra("img")!! && data?.hasExtra("txt")!!){
                        var imgPreviewPojo:ImgPreviewPojo = ImgPreviewPojo(filePath = data?.getStringExtra("img"), caption = data?.getStringExtra("txt"))
                        mOnImgPreviewListener?.onImgPreview(imgPreviewPojo)
                    }
                }
            }
        }
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }




}