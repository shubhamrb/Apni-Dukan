package com.mamits.apnaonlines.user.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.listener.OnItemClickListener
import com.mamits.apnaonlines.user.model.LocationUtils
import com.mamits.apnaonlines.user.ui.adapter.NotificationAdapter
import com.mamits.apnaonlines.user.ui.adapter.StoresAdapter
import com.mamits.apnaonlines.user.ui.base.BaseActivity
import com.mamits.apnaonlines.user.ui.fragment.*
import com.mamits.apnaonlines.user.util.CommonUtils
import com.mamits.apnaonlines.user.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bottom_navigation
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_instruction.view.*
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import kotlinx.android.synthetic.main.fragment_stores.view.*
import kotlinx.android.synthetic.main.item_unread_notification.view.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*

@AndroidEntryPoint
class NotificationActivity : BaseActivity(), OnItemClickListener {
    private var nAdapter: NotificationAdapter? = null
    private val START_PAGE = 0
    private var CURRENT_PAGE = START_PAGE
    private val LIMIT = 15
    override fun onNetworkChange(isConnected: Boolean) {

    }

    val viewModel: UserListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        image_back.setOnClickListener {
            onBackPressed()
        }
        toolbar_title.text = resources?.getString(R.string.notification)

        //UnRead
        rv_unread_notification?.setHasFixedSize(true)
        rv_unread_notification?.layoutManager =
            LinearLayoutManager(
                this@NotificationActivity,
                RecyclerView.VERTICAL,
                false
            )
        nAdapter=NotificationAdapter(
            this@NotificationActivity,
            this@NotificationActivity,"unread"
        )

        rv_unread_notification?.adapter = nAdapter
        btn_next.setOnClickListener {
            CURRENT_PAGE++
            callNotificationList(CURRENT_PAGE)
        }
        //Read
//                        rv_read_notification?.setHasFixedSize(true)
//                        rv_read_notification?.layoutManager =
//                            LinearLayoutManager(
//                                this@NotificationActivity,
//                                RecyclerView.VERTICAL,
//                                false
//                            )
//                        rv_read_notification?.adapter = NotificationAdapter(
//                            this@NotificationActivity,
//                            this@NotificationActivity, "read", data
//                        )

        callNotificationList(CURRENT_PAGE)

        bottom_navigation.setOnNavigationItemSelectedListener {
            supportFragmentManager?.popBackStack();
            var intent = Intent()
            when (it.itemId) {
                R.id.nav_home -> {
                    intent?.putExtra("from", "NOTI_home")
                }
                R.id.nav_chat -> {
                    intent?.putExtra("from", "NOTI_chat")
                }
                R.id.nav_history -> {
                    intent?.putExtra("from", "NOTI_history")
                }
                R.id.nav_nearby -> {
                    intent?.putExtra("from", "NOTI_nearby")
                }
                R.id.nav_acc -> {
                    intent?.putExtra("from", "NOTI_account")
                }
            }
            setResult(RESULT_OK)
            finish()
            true
        }

    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.text_noti_title->{
                var intent:Intent = Intent()
                intent.putExtra("from", "notification")
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
    private fun callNotificationList(current_page: Int) {
        if (CommonUtils.isOnline(this!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                longitude = LocationUtils?.getCurrentLocation()?.lng!!,
                start = current_page.toString(),
                pagelength = LIMIT.toString()
            )
            viewModel?.getNotificationList(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                        switchView(1, "")
                        if (it?.next){
                            btn_next!!.visibility=View.VISIBLE
                        }else{
                            btn_next!!.visibility=View.GONE
                        }
                        nAdapter?.setList(it.data!!)
                    } else {
                        switchView(0, "")
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            resources?.getString(R.string.no_net)!!
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

    fun switchView(i: Int, msg: String) {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    nestedscroll_notification?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    nestedscroll_notification?.visibility = View.VISIBLE
                    relative_empty?.visibility = View.GONE
                }
                2 -> {
                    nestedscroll_notification?.visibility = View.GONE
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                }
                3 -> {
                    relative_empty?.visibility = View.GONE
                    relative_progress?.visibility = View.VISIBLE
                    nestedscroll_notification?.visibility = View.GONE
                }
            }
    }



}