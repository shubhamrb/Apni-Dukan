package com.mponline.userApp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnImgPreviewListener
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.ImgPreviewPojo
import com.mponline.userApp.model.PrePlaceOrderPojo
import com.mponline.userApp.model.response.*
import com.mponline.userApp.ui.adapter.SearchHomeAdapter
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.ui.fragment.*
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.fragment_chat_home.view.*
import kotlinx.android.synthetic.main.item_search.view.*


@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnSwichFragmentListener, OnItemClickListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mOnImgPreviewListener: OnImgPreviewListener? = null
    val viewModel: UserListViewModel by viewModels()

    override fun onStartNewActivity(listener: OnImgPreviewListener, imgPath: String) {
        super.onStartNewActivity(listener, imgPath)
        if (listener != null && imgPath != null) {
            mOnImgPreviewListener = listener
            var intent: Intent = Intent(this@MainActivity, ImgPreviewActivity::class.java)
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
                if (obj != null && obj is CategorylistItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        ServiceFragment.newInstance(this@MainActivity, obj)
                    )
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
                if (obj != null && obj is ProductListItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        StoresFragment.newInstance(this@MainActivity, obj)
                    )
                    ft.addToBackStack(Constants.STORE_PAGE_BY_PROD)
                    ft.commit()
                }
            }
            Constants.SUB_SERVICE_PAGE -> {
                if (obj != null && obj is CategorylistItem && extras != null && extras is CategorylistItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        SubServiceFragment.newInstance(this@MainActivity, obj, extras)
                    )
                    ft.addToBackStack(Constants.SUB_SERVICE_PAGE)
                    ft.commit()
                }else if (obj == null && extras != null && extras is CategorylistItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        SubServiceFragment.newInstance(this@MainActivity, obj, extras)
                    )
                    ft.addToBackStack(Constants.SUB_SERVICE_PAGE)
                    ft.commit()
                }
            }
            Constants.INSTRUCTION_PAGE -> {
                if (obj != null && extras != null && obj is StoreDetailDataItem && extras is ProductListItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        InstructionFragment.newInstance(this, obj, extras)
                    )
                    ft.addToBackStack(Constants.INSTRUCTION_PAGE)
                    ft.commit()
                }
            }
            Constants.CUSTOM_FOEMS_PAGE -> {
                if (obj != null && obj is PrePlaceOrderPojo) {
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
                if(obj is OrderHistoryDataItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(R.id.rl_container_drawer, PaymentSummaryFragment.newInstance(this@MainActivity, obj))
                    ft.addToBackStack(Constants.PAYMENT_SUMMARY_PAGE)
                    ft.commit()
                }
            }
            Constants.STORE_DETAIL_PAGE -> {
                if (obj != null && obj is StorelistItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        StoreDetailFragment.newInstance(this@MainActivity, obj)
                    )
                    ft.addToBackStack(Constants.STORE_DETAIL_PAGE_WITH_PROD)
                    ft.commit()
                }
            }
            Constants.STORE_DETAIL_PAGE_WITH_PROD -> {
                if (obj != null && obj is StorelistItem && extras != null && extras is ProductListItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        StoreDetailFragment.newInstance(this@MainActivity, obj, extras)
                    )
                    ft.addToBackStack(Constants.STORE_DETAIL_PAGE_WITH_PROD)
                    ft.commit()
                }else if(obj != null && obj is StorelistItem){

                }
            }
            Constants.CHAT_MSG_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(
                    R.id.rl_container_drawer,
                    ChatMsgFragment.newInstance(this@MainActivity, obj!!)
                )
                ft.addToBackStack(Constants.CHAT_MSG_PAGE)
                ft.commit()
            }
            Constants.CHAT_MSG_PAGE_FROM_DETAIL -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(
                    R.id.rl_container_drawer,
                    ChatMsgFragment.newInstance(this@MainActivity, obj!!, extras!!)
                )
                ft.addToBackStack(Constants.CHAT_MSG_PAGE)
                ft.commit()
            }
            Constants.COUPON_PAGE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, CouponFragment())
                ft.addToBackStack(Constants.COUPON_PAGE)
                ft.commit()
            }
            Constants.UPDATE_PROFILE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, UpdateProfileFragment())
                ft.addToBackStack(Constants.COUPON_PAGE)
                ft.commit()
            }
            Constants.CHANGE_PWD -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, ChangePwdFragment())
                ft.addToBackStack(Constants.COUPON_PAGE)
                ft.commit()
            }
            Constants.CLOSE_NAV_DRAWER -> {
                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
            }
        }
    }
    override fun onSwitchFragmentFromDrawer(tag: String, type: String, obj: Any?, extras: Any?) {
        app_bar_common.visibility = View.VISIBLE
        val menu: Menu = bottom_navigation.getMenu()
        when (tag) {
          Constants.STORE_PAGE -> {
              menu?.getItem(3)?.setChecked(true)
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, StoresFragment.newInstance(this@MainActivity))
                ft.addToBackStack(Constants.STORE_PAGE)
                ft.commit()
            }
            Constants.ORDER_HISTORY_PAGE -> {
                menu?.getItem(2)?.setChecked(true)
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, OrderHistoryFragment())
                ft.addToBackStack(Constants.ORDER_HISTORY_PAGE)
                ft.commit()
            }
            Constants.MY_ACCOUNT_PAGE -> {
                menu?.getItem(4)?.setChecked(true)
                onSwitchFragmentParent(Constants.MY_ACCOUNT_PAGE, "", null, null)
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
            Constants.UPDATE_PROFILE -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, UpdateProfileFragment())
                ft.addToBackStack(Constants.UPDATE_PROFILE)
                ft.commit()
            }
            Constants.CHANGE_PWD -> {
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, ChangePwdFragment())
                ft.addToBackStack(Constants.CHANGE_PWD)
                ft.commit()
            }
        }
    }

    override fun onSwichToolbar(tag: String, type: String, obj: Any?) {
        when (tag) {
            Constants.HIDE_NAV_DRAWER_TOOLBAR -> {
                app_bar_common.visibility = View.GONE
            }
            Constants.SHOW_NAV_DRAWER_TOOLBAR -> {
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
                R.id.nav_home -> {
                    onSwitchFragment(Constants.HOME_PAGE, "", null, null)
                }
                R.id.nav_chat -> {
                    onSwitchFragmentParent(Constants.CHAT_HOME_PAGE, "", null, null)
                }
                R.id.nav_history -> {
                    onSwitchFragment(Constants.ORDER_HISTORY_PAGE, "", null, null)
                }
                R.id.nav_nearby -> {
                    onSwitchFragment(Constants.STORE_PAGE, "", null, null)
                }
                R.id.nav_acc -> {
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

        rv_search.visibility = View.GONE

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length!! > 0) {
                    callHomeSearch(s.toString().trim())
                } else {
                    rv_search.visibility = View.GONE
                }
            }
        })

    }

    fun setDrawerInfo(){

    }

    override fun onBackPressed() {
        CommonUtils.printLog("onBackPressed", "")
        val fragList = supportFragmentManager.fragments
        val menu: Menu = bottom_navigation.getMenu()
        var isAvailable = false
        val homeFragment: HomeFragment? =
            supportFragmentManager.findFragmentByTag(Constants.HOME_PAGE) as HomeFragment?
        if(homeFragment!=null && homeFragment?.isVisible){
            app_bar_common.visibility = View.VISIBLE
            menu?.getItem(0)?.setChecked(true)
        }
//        fragList?.forEach {
//            if(it!=null && it?.getUs){
//                if(it is HomeFragment){
//                    CommonUtils.printLog("FRAG_CHANGED", "${it?.tag}")
////                    app_bar_common.visibility = View.VISIBLE
////                    menu?.getItem(0)?.setChecked(true)
//                }else if(it is ChatHomeFragment){
//                    CommonUtils.printLog("FRAG_CHANGED", "${it?.tag}")
//                    app_bar_common.visibility = View.VISIBLE
////                    menu?.getItem(1)?.setChecked(menu?.getItem(1)?.itemId == R.id.nav_chat)
//                }else if(it is OrderHistoryFragment){
//                    CommonUtils.printLog("FRAG_CHANGED", "${it?.tag}")
//                    app_bar_common.visibility = View.VISIBLE
////                    menu?.getItem(2)?.setChecked(menu?.getItem(2)?.itemId == R.id.nav_history)
//                }else if(it is AccountFragment){
//                    CommonUtils.printLog("FRAG_CHANGED", "${it?.tag}")
//                    app_bar_common.visibility = View.VISIBLE
////                    menu?.getItem(4)?.setChecked(menu?.getItem(4)?.itemId == R.id.nav_acc)
//                }
//            }
//        }
        super.onBackPressed()
    }

    private fun callHomeSearch(searchKey: String) {
        if (CommonUtils.isOnline(this)) {
            var commonRequestObj = getCommonRequestObj(
                search = searchKey,
                apiKey = getApiKey()
            )
            viewModel?.homeSearch(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                        if (data != null && data?.size!! > 0) {
                            rv_search.visibility = View.VISIBLE
                            rv_search?.setHasFixedSize(true)
                            rv_search?.layoutManager =
                                LinearLayoutManager(
                                    this@MainActivity,
                                    RecyclerView.VERTICAL,
                                    false
                                )
                            rv_search?.adapter = SearchHomeAdapter(
                                this@MainActivity,
                                this@MainActivity,
                                data
                            )
                        } else {
                            rv_search.visibility = View.GONE
                        }
                        if (edt_search?.text?.toString()?.trim()?.isNullOrEmpty()!!) {
                            rv_search.visibility = View.GONE
                        }
                    } else {
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            message
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    override fun onStart() {
        super.onStart()
    }

    /* private fun setNavigationDrawer() {
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
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constants.RESULT_IMG_PREVIEW -> {
                    if (data?.hasExtra("img")!! && data?.hasExtra("txt")!!) {
                        var imgPreviewPojo: ImgPreviewPojo = ImgPreviewPojo(
                            filePath = data?.getStringExtra("img"),
                            caption = data?.getStringExtra("txt")
                        )
                        mOnImgPreviewListener?.onImgPreview(imgPreviewPojo)
                    }
                }
            }
        }
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        rv_search.visibility = View.GONE
        when (view?.id) {
            R.id.text_service_name -> {
                if (obj is HomeSearchData) {
                   when(obj?.type){
                       "category"->{
                           onSwitchFragment(
                               Constants.SERVICE_PAGE,
                               Constants.WITH_NAV_DRAWER,
                               CategorylistItem(id = obj?.value!!, name = obj?.name),
                               null
                           )
                       }
                       "subcategory"->{
                           onSwitchFragment(
                               Constants.SUB_SERVICE_PAGE,
                               Constants.WITH_NAV_DRAWER,
                               null,
                               CategorylistItem(id = obj?.value!!, name = obj?.name)
                           )
                       }
                       "product"->{
                           onSwitchFragment(
                               Constants.STORE_PAGE_BY_PROD,
                               Constants.WITH_NAV_DRAWER,
                               ProductListItem(id = obj?.value!!, name = obj?.name), null
                           )
                       }
                       "store"->{
                           onSwitchFragment(
                               Constants.STORE_DETAIL_PAGE,
                               Constants.WITH_NAV_DRAWER,
                               StorelistItem(id = obj?.value!!, name = obj?.name),
                               null
                           )
                       }
                   }
                }
            }
        }
    }


}