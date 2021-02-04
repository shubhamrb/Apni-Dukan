package com.mponline.vendorApp.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mponline.vendorApp.R
import com.mponline.vendorApp.listener.OnSwichFragmentListener
import com.mponline.vendorApp.model.DrawerModel
import com.mponline.vendorApp.ui.adapter.DrawerAdapter
import com.mponline.vendorApp.ui.base.BaseFragment
import com.mponline.vendorApp.util.CommonUtils
import com.mponline.vendorApp.util.PreferenceUtils
import com.mponline.vendorApp.utils.Constants
import kotlinx.android.synthetic.main.fragment_drawer.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class DrawerFragment : BaseFragment() {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    private var views: View? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var drawerAdapter: DrawerAdapter? = null
    private var containerView: View? = null
    private var recyclerView: RecyclerView? = null
    var onSwichFragmentListener: OnSwichFragmentListener? = null
    private var names = arrayOf(
        "My Account",
        "Nearby Kiosk Services",
        "History"
    )

    private var images = intArrayOf(
        R.drawable.ic_arrow_back_ios,
        R.drawable.ic_arrow_back_ios,
        R.drawable.ic_arrow_back_ios
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context!=null){
            onSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        views = inflater!!.inflate(R.layout.fragment_drawer, container, false)
        mPreferenceUtils = PreferenceUtils.getInstance(context!!)

        recyclerView = views!!.findViewById<View>(R.id.listview) as RecyclerView
        drawerAdapter = DrawerAdapter(activity!!, populateList())

        recyclerView!!.adapter = drawerAdapter
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(activity!!, recyclerView!!, object : ClickListener {
            override fun onClick(view: View, position: Int) {
                openFragment(names[position])
                mDrawerLayout!!.closeDrawer(containerView!!)
            }

            override fun onLongClick(view: View?, position: Int) {

            }
        }))

//        var userType = mPreferenceUtils?.getValue(Constants.LOGIN_TYPE);
//        var userResStr = mPreferenceUtils?.getValue(Constants.USER_INFO);
//        val listType = object : TypeToken<UserDataObj>() {}.type
//        var mGetUserInfoResponse = Gson().fromJson<UserDataObj>(userResStr, listType)
//        var userName = if(userType?.equals(Constants.APARTMENT) || userType.equals(Constants.APARTMENT_ASSIGNEE)) mGetUserInfoResponse?.company_name!!+"\n\n"+mGetUserInfoResponse.name!!
//        else if((userType?.equals(Constants.CORPORATE)) || (userType.equals(Constants.CORPORATE_ASSIGNEE))) mGetUserInfoResponse?.company_name!!+"\n\n"+mGetUserInfoResponse.name!!
//        else mGetUserInfoResponse.name!!
//        views?.text_nav_username!!.text = userName;//mPreferenceUtils.getValue(Constants.USER_NAME)

        openFragment("")

        return views
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view?.image_drawer_close?.setOnClickListener {
            onSwichFragmentListener?.onSwitchFragment(Constants.CLOSE_NAV_DRAWER,"", null, null)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        drawerAdapter = DrawerAdapter(activity!!, populateList())
        recyclerView!!.adapter = drawerAdapter
    }

    private fun openFragment(navStr: String) {

        when (navStr) {
            "My Account"->{
//                val intent = Intent(activity, AddTrashRequestActivity::class.java)
//                startActivity(intent)
            }
            "Nearby Kiosk Services"->{
//                val intent = Intent(activity, NotificationActivity::class.java)
//                startActivity(intent)
            }
            "History"->{
//                val intent = Intent(activity, NgoSubscriptionListActivity::class.java)
//                startActivity(intent)
            }
            else -> {
            }
        }
    }



    fun setUpDrawer(fragmentId: Int, drawerLayout: DrawerLayout, toolbar: Toolbar) {
        containerView = activity?.findViewById(fragmentId)
        mDrawerLayout = drawerLayout
        mDrawerToggle = object :
            ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                activity?.invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                activity?.invalidateOptionsMenu()
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                toolbar.alpha = 1 - slideOffset / 2
            }
        }

        mDrawerToggle?.isDrawerIndicatorEnabled = false
        mDrawerToggle?.setHomeAsUpIndicator(R.drawable.ic_nav_drawer)
        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)
        mDrawerLayout!!.post { mDrawerToggle!!.syncState() }

    }

    private fun populateList(): ArrayList<DrawerModel> {

        val list = ArrayList<DrawerModel>()

        for (i in names.indices) {
                val drawerModel = DrawerModel()
                drawerModel.name = names[i]
                drawerModel.image = images[i]
                list.add(drawerModel)
        }
        CommonUtils.printLog("NAV_LIST", "${Gson().toJson(list)}")
        return list
    }

    interface ClickListener {
        fun onClick(view: View, position: Int)

        fun onLongClick(view: View?, position: Int)
    }

    internal class RecyclerTouchListener(
        context: Context,
        recyclerView: RecyclerView,
        private val clickListener: ClickListener?
    ) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child))
                    }
                }
            })
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            val child = rv.findChildViewUnder(e.x, e.y)
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child))
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

}