package com.mponline.userApp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnImgPreviewListener
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnLocationFetchListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.*
import com.mponline.userApp.model.response.*
import com.mponline.userApp.ui.adapter.SearchHomeAdapter
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.ui.base.FusedLocationActivity
import com.mponline.userApp.ui.fragment.*
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.common_toolbar.toolbar
import kotlinx.android.synthetic.main.fragment_chat_home.view.*
import kotlinx.android.synthetic.main.item_search.view.*
import kotlinx.android.synthetic.main.layout_toolbar_search.*
import java.text.DateFormat
import java.util.*


@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnSwichFragmentListener, OnItemClickListener {

    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mOnImgPreviewListener: OnImgPreviewListener? = null
    var mOnLocationFetchListener: OnLocationFetchListener? = null
    val viewModel: UserListViewModel by viewModels()
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var mLocationCallback: LocationCallback? = null
    var mCurrentLocation: Location? = null
    var mRequestingLocationUpdates = false
    var mSearchActive = false
    private var mLastUpdateTime: String? = ""

    override fun onStartNewActivity(listener: OnImgPreviewListener, imgPath: String) {
        super.onStartNewActivity(listener, imgPath)
        if (listener != null && imgPath != null) {
            mOnImgPreviewListener = listener
            var intent: Intent = Intent(this@MainActivity, ImgPreviewActivity::class.java)
            intent?.putExtra("img", imgPath)
            startActivityForResult(intent, Constants.RESULT_IMG_PREVIEW)
        }
    }

    override fun onStartLocationAccess(listener: OnLocationFetchListener) {
        super.onStartLocationAccess(listener)
        if (listener != null) {
            mOnLocationFetchListener = listener
            checkForLocation()
        }
    }

    override fun onStart() {
        CommonUtils?.printLog("MAINACTIVITY_START","called")
        super.onStart()
        /*if(!mPreferenceUtils?.getValue(Constants.USER_SELECTED_LOCATION)?.isNullOrEmpty()){
            mRequestingLocationUpdates = true
            CommonUtils?.printLog("MAINACTIVITY_START","called_IF ${mRequestingLocationUpdates}")
            val listType = object : TypeToken<LocationObj>() {}.type
            var mLocationObj = Gson().fromJson<LocationObj>(mPreferenceUtils?.getValue(Constants.USER_SELECTED_LOCATION)!!, listType)
            if(mLocationObj!=null){
                LocationUtils.setCurrentLocation(mLocationObj)
                text_locationName?.text = mLocationObj?.city
                onLocationSuccess()
            }
        }else{
            CommonUtils?.printLog("MAINACTIVITY_START","called_ELSE")
            createLocationCallback()
            createLocationRequest()
            buildLocationSettingsRequest()
        }*/
    }
    override fun onRestart() {
        super.onRestart()
    }

    override fun onSwitchFragment(tag: String, type: String, obj: Any?, extras: Any?) {
        app_bar_common.visibility = View.VISIBLE
        val menu: Menu = bottom_navigation.getMenu()
        when (tag) {
            Constants.HOME_PAGE -> {
                menu?.getItem(0)?.setChecked(true)
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
                menu?.getItem(3)?.setChecked(true)
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
                } else if (obj == null && extras != null && extras is CategorylistItem) {
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
                menu?.getItem(2)?.setChecked(true)
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, OrderHistoryFragment())
                ft.addToBackStack(Constants.ORDER_HISTORY_PAGE)
                ft.commit()
            }
            Constants.PAYMENT_DETAIL_PAGE -> {
                if (obj != null && obj is OrderHistoryDataItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        PaymentDetailFragment.newInstance(this@MainActivity, obj)
                    )
                    ft.addToBackStack(Constants.PAYMENT_DETAIL_PAGE)
                    ft.commit()
                }
            }
            Constants.PAYMENT_SUMMARY_PAGE -> {
                if (obj is OrderHistoryDataItem) {
                    val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                    ft.add(
                        R.id.rl_container_drawer,
                        PaymentSummaryFragment.newInstance(this@MainActivity, obj)
                    )
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
                } else if (obj != null && obj is StorelistItem) {

                }
            }
            Constants.CHAT_MSG_PAGE -> {
                menu?.getItem(1)?.setChecked(true)
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
        val menu: Menu = bottom_navigation.getMenu()
        when (tag) {
            Constants.DOWNLOAD_LIST_PAGE -> {
                app_bar_common.visibility = View.GONE
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, DownloadListFragment())
                ft.addToBackStack(Constants.DOWNLOAD_LIST_PAGE)
                ft.commit()
            }
            Constants.CHAT_HOME_PAGE -> {
                menu?.getItem(1)?.setChecked(true)
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, ChatHomeFragment())
                ft.addToBackStack(Constants.CHAT_HOME_PAGE)
                ft.commit()
            }
            Constants.MY_ACCOUNT_PAGE -> {
                menu?.getItem(4)?.setChecked(true)
                app_bar_common.visibility = View.GONE
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, AccountFragment())
                ft.addToBackStack(Constants.MY_ACCOUNT_PAGE)
                ft.commit()
            }
            Constants.UPDATE_PROFILE -> {
                app_bar_common.visibility = View.GONE
                val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                ft.add(R.id.rl_container_drawer, UpdateProfileFragment())
                ft.addToBackStack(Constants.UPDATE_PROFILE)
                ft.commit()
            }
            Constants.CHANGE_PWD -> {
                app_bar_common.visibility = View.GONE
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

    //LOCATION RELATED FUN
    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult != null) {
                    mCurrentLocation = locationResult.lastLocation
                    mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
                    CommonUtils.printLog("FetchedCurrLocation", Gson().toJson(mCurrentLocation))
                    onLocationSuccess()
                }
            }
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval =
            FusedLocationActivity.UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval =
            FusedLocationActivity.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mRequestingLocationUpdates = true
        mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(this) {
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()
                )
            }
            .addOnFailureListener(this) { e ->
                val statusCode = (e as ApiException).statusCode
                onLocationCancelled()
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(), and check the
                        // result in onActivityResult().
                        val rae = e as ResolvableApiException
                        rae.startResolutionForResult(
                            this@MainActivity,
                            FusedLocationActivity.REQUEST_CHECK_SETTINGS
                        )
                    } catch (sie: IntentSender.SendIntentException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings."
                        CommonUtils.printLog(FusedLocationActivity.TAG, errorMessage)
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
    }

    fun stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            return
        }
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            .addOnCompleteListener(this) {
                mRequestingLocationUpdates = false;
            }
    }

    fun onLocationCancelled(){
        stopLocationUpdates()
    }

    fun onLocationSuccess(isPlacesApi:Boolean = false){
        if(mCurrentLocation!=null && !isPlacesApi){
            mCurrentLocation?.let {
                val geocoder: Geocoder
                var addresses: List<Address> = arrayListOf()
                geocoder = Geocoder(this, Locale.getDefault())
                CommonUtils.printLog(
                    "CURRENT_LOCATION_success",
                    "${it?.latitude}, ${it?.longitude}"
                )
                if (mCurrentLocation != null) {
                    try {
                        addresses = geocoder.getFromLocation(
                            mCurrentLocation?.latitude!!,
                            mCurrentLocation?.longitude!!,
                            1
                        )
                    }catch (e:Exception){
                        CommonUtils.createSnackBar(findViewById(android.R.id.content), "Something went wrong, please check your network connection & try again")
                    }
                }
                if (addresses != null && addresses?.size!! > 0) {
                    val address: String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    val city: String = addresses[0].getLocality()
                    val state: String = addresses[0].getAdminArea()
                    val country: String = addresses[0].getCountryName()
                    val postalCode: String = addresses[0].getPostalCode()
                    val knownName: String = addresses[0].getFeatureName()
                    val add: String = addresses[0].getAddressLine(0)
                    CommonUtils.printLog("ADDRESSSSS","${knownName}, ${city}, ${add}")
                    var locationObj = LocationObj(
                        lat = it?.latitude?.toString()!!, lng = it?.longitude?.toString(),
                        address = add, city = city, state = state
                    )
                    LocationUtils.setCurrentLocation(locationObj)
                    text_locationName?.text = if(add!=null && !add?.isNullOrEmpty()) locationObj?.address else locationObj?.city
//                    mPreferenceUtils?.setValue(Constants.USER_SELECTED_LOCATION, Gson().toJson(locationObj))
                    if (mOnLocationFetchListener != null) {
                        mOnLocationFetchListener?.onLocationSuccess(locationObj)
                    }
                }
            }
        }
        stopLocationUpdates()
        onSwitchFragment(Constants.HOME_PAGE, "", null, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        CommonUtils?.printLog("MAINACTIVITY_ONCREATE","called")
        /*if(!mPreferenceUtils?.getValue(Constants.USER_SELECTED_LOCATION)?.isNullOrEmpty()){
            CommonUtils?.printLog("MAINACTIVITY_ONCREATE","called_IF")
            val listType = object : TypeToken<LocationObj>() {}.type
            var mLocationObj = Gson().fromJson<LocationObj>(mPreferenceUtils?.getValue(Constants.USER_SELECTED_LOCATION)!!, listType)
            if(mLocationObj!=null){
                LocationUtils.setCurrentLocation(mLocationObj)
                text_locationName?.text = mLocationObj?.city
                onLocationSuccess()
            }
        }else{*/
            mRequestingLocationUpdates = true
            CommonUtils?.printLog("MAINACTIVITY_ONCREATE","called_ELSE ${mRequestingLocationUpdates}")
            createLocationCallback()
            createLocationRequest()
            buildLocationSettingsRequest()
            checkForLocation()
//        }

        image_notification.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity, NotificationActivity::class.java), Constants.REQUEST_NOTIFICATION)
        }

        image_offer?.setOnClickListener {
            var intent: Intent = Intent(this, OffersActivity::class.java)
            intent?.putExtra("type", "offer")
            startActivityForResult(intent, Constants.REQUEST_OFFER)
        }

        ll_location.setOnClickListener {
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getString(R.string.api_key), Locale.UK);
            }
            var fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, Constants.REQUEST_AUTOCOMPLETE_PLACE)
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

        val fragmentManager: FragmentManager = supportFragmentManager
        fragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                CommonUtils.printLog("FRAGMENT_DESTROY", "${if(f is HomeFragment) "HOME" else "OTHER"},, REMAINIG-> ${supportFragmentManager?.backStackEntryCount}")
                for (entry in 0 until fm.backStackEntryCount) {
                    CommonUtils.printLog("FRAGMENT_FOUND-> ", "Found fragment: " + fm.getBackStackEntryAt(entry).id)
                }
                if(supportFragmentManager?.backStackEntryCount == 0){
                    app_bar_common.visibility = View.VISIBLE
                    val menu: Menu = bottom_navigation.getMenu()
                    menu?.getItem(0)?.setChecked(true)
                }
                super.onFragmentDestroyed(fm, f)
            }

            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                CommonUtils.printLog("FRAGMENT_RESUME", "${if(f is HomeFragment) "HOME" else "OTHER"}")
            }

            override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                super.onFragmentStarted(fm, f)
                CommonUtils.printLog("FRAGMENT_START", "${if(f is HomeFragment) "HOME" else "OTHER"}")
            }

        }, true)

        edt_toolbar_title_search.addTextChangedListener(object : TextWatcher {
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
        image_search.setOnClickListener {
            mSearchActive = true
            toolbar_search.visibility = View.VISIBLE
            val width = windowManager?.defaultDisplay?.width?.toFloat()
            val animation = TranslateAnimation(
                width!!,
                CommonUtils.convertPixelsToDp(0f, this@MainActivity),
                0f,
                0f
            ) // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
            animation.duration = 300 // animation duration
            animation.repeatCount = 0 // animation repeat count
            animation.repeatMode = 0 // repeat animation (left to right, right to
            toolbar_search.startAnimation(animation) // start animation
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    app_bar_common.visibility = View.GONE
                    CommonUtils.openKeyboard(this@MainActivity)
                    edt_toolbar_title_search.setText("")
                    edt_toolbar_title_search.requestFocus()
                }

                override fun onAnimationStart(animation: Animation?) {
                }
            })
        }
        image_close_search.setOnClickListener {
            edt_toolbar_title_search.setText("")
        }
        image_back_arrow_search.setOnClickListener {
            closeSearchToolbarview()
        }


        if(intent.hasExtra("from")){
            var from = intent?.getStringExtra("from")
            when(from){
                "notification"->{
                    onSwitchFragment(Constants.ORDER_HISTORY_PAGE, "", null, null)
                }
                "NOTI_home"->{
                    onSwitchFragment(Constants.HOME_PAGE, "", null, null)
                }
                "NOTI_chat"->{
                    onSwitchFragmentParent(Constants.CHAT_HOME_PAGE, "", null, null)
                }
                "NOTI_history"->{
                    onSwitchFragment(Constants.ORDER_HISTORY_PAGE, "", null, null)
                }
                "NOTI_nearby"->{
                    onSwitchFragment(Constants.STORE_PAGE, "", null, null)
                }
                "NOTI_account"->{
                    onSwitchFragmentParent(Constants.MY_ACCOUNT_PAGE, "", null, null)
                }
                "NOTI_enquiry"->{
                    //Launch webview with enquiry
                    var intent:Intent = Intent(this@MainActivity, EnquirySupportWebviewActivity::class.java)
                    intent?.putExtra("type", "enquiry")
                    startActivity(intent)
                }
                "offer"->{
                    var id = intent.getStringExtra("id")
                    onSwitchFragment(
                        Constants.STORE_DETAIL_PAGE,
                        Constants.WITH_NAV_DRAWER,
                        StorelistItem(id = id),
                        null
                    )
                }
            }
        }
//        checkForLocation()
    }

    fun closeSearchToolbarview(){
        app_bar_common.visibility = View.VISIBLE
        mSearchActive = false
//            animateViewVisibility(toolbar_search, false, 300)
        val width = windowManager?.defaultDisplay?.width?.toFloat()
        val animation = TranslateAnimation(
            CommonUtils.convertPixelsToDp(0f, this@MainActivity),
            width!!,
            0f,
            0f
        ) // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.duration = 200 // animation duration
        animation.repeatCount = 0 // animation repeat count
        animation.repeatMode = 0 // repeat animation (left to right, right to
        toolbar_search.startAnimation(animation) // start animation
        toolbar_search.visibility = View.GONE
        edt_toolbar_title_search.setText("")
        CommonUtils.hideKeyboardView(this@MainActivity, edt_toolbar_title_search)
    }

    @SuppressLint("MissingPermission")
    fun checkForLocation() {
        if (LocationUtils.getCurrentLocation() == null) {
            //Access New Location
            if (isLocationPermissionGranted()) {
                startLocationUpdates()
            } else {
                checkLocationPermissions()
            }
        } else {
            if (mOnLocationFetchListener != null) {
                mOnLocationFetchListener?.onLocationSuccess(LocationUtils.getCurrentLocation()!!)
                text_locationName?.text = LocationUtils.getCurrentLocation()?.city
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_LOC_PERMISSIONS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkForLocation()
                    startLocationUpdates()
                }else{
                    CommonUtils.showPermissionDialog(this@MainActivity)
                }
            }
        }
    }

    override fun onBackPressed() {
        CommonUtils.printLog("onBackPressed", "")
        val fragList = supportFragmentManager.fragments
        val menu: Menu = bottom_navigation.getMenu()
        var isAvailable = false
        val homeFragment: HomeFragment? =
            supportFragmentManager.findFragmentByTag(Constants.HOME_PAGE) as HomeFragment?
        if(mSearchActive){
            closeSearchToolbarview()
        }else if (homeFragment != null && homeFragment?.isVisible) {
            app_bar_common.visibility = View.VISIBLE
            menu?.getItem(0)?.setChecked(true)
            super.onBackPressed()
        }else{
            super.onBackPressed()
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
                       /* if (edt_search?.text?.toString()?.trim()?.isNullOrEmpty()!!) {
                            rv_search.visibility = View.GONE
                        }*/
                        if (edt_toolbar_title_search?.text?.toString()?.trim()?.isNullOrEmpty()!!) {
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
    fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                FusedLocationActivity.REQUEST_CHECK_SETTINGS -> when (resultCode) {
                    RESULT_OK ->                         // Nothing to do. startLocationupdates() gets called in onResume again.
                        if (checkPermissions()) {
                            startLocationUpdates()
                        }
                    RESULT_CANCELED -> //                        mRequestingLocationUpdates = false;
                        onLocationCancelled()
                }
                Constants.RESULT_IMG_PREVIEW -> {
                    if (data?.hasExtra("img")!! && data?.hasExtra("txt")!!) {
                        var imgPreviewPojo: ImgPreviewPojo = ImgPreviewPojo(
                            filePath = data?.getStringExtra("img"),
                            caption = data?.getStringExtra("txt")
                        )
                        mOnImgPreviewListener?.onImgPreview(imgPreviewPojo)
                    }
                }
                Constants.REQUEST_AUTOCOMPLETE_PLACE -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        CommonUtils.printLog(
                            "AUTOCOMPLETE_LOC",
                            "Place: ${place.name}"
                        )
                        if (place?.latLng != null) {
                            text_locationName?.text = place?.name
                            var locationObj = LocationObj(
                                lat = place?.latLng?.latitude?.toString()!!,
                                lng = place?.latLng?.longitude?.toString()!!,
                                address = place?.name!!,//if(place?.address?.isNullOrEmpty()!!) place?.name!! else place?.address!!,
                                city = place?.name!!
                            )
                            LocationUtils?.setSelectedLocation(
                                locationObj
                            )
//                            mPreferenceUtils?.setValue(Constants.USER_SELECTED_LOCATION, Gson().toJson(locationObj))
                            onLocationSuccess(true)
                        }
                    }!!
                }
                Constants.REQUEST_NOTIFICATION->{
                    handleNavigationToFrag(data)
                }
                Constants.REQUEST_OFFER->{
                    handleNavigationToFrag(data)
                }
            }
        }
    }

    fun handleNavigationToFrag(intent:Intent?){
        if(intent!=null && intent.hasExtra("from")){
            var from = intent?.getStringExtra("from")
            when(from){
                "notification"->{
                    onSwitchFragment(Constants.ORDER_HISTORY_PAGE, "", null, null)
                }
                "NOTI_home"->{
                    onSwitchFragment(Constants.HOME_PAGE, "", null, null)
                }
                "NOTI_chat"->{
                    onSwitchFragmentParent(Constants.CHAT_HOME_PAGE, "", null, null)
                }
                "NOTI_history"->{
                    onSwitchFragment(Constants.ORDER_HISTORY_PAGE, "", null, null)
                }
                "NOTI_nearby"->{
                    onSwitchFragment(Constants.STORE_PAGE, "", null, null)
                }
                "NOTI_account"->{
                    onSwitchFragmentParent(Constants.MY_ACCOUNT_PAGE, "", null, null)
                }
                "offer"->{
                    var id = intent.getStringExtra("id")
                    onSwitchFragment(
                        Constants.STORE_DETAIL_PAGE,
                        Constants.WITH_NAV_DRAWER,
                        StorelistItem(id = id),
                        null
                    )
                }
            }
        }
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        rv_search.visibility = View.GONE
        when (view?.id) {
            R.id.rl_banner -> {
                if (obj != null && obj is BannerlistItem) {
                    if(obj?.product_id!=null){
                        if (obj?.url?.equals("product")) {
                            onSwitchFragment(
                                Constants.STORE_PAGE_BY_PROD,
                                Constants.WITH_NAV_DRAWER,
                                ProductListItem(id = obj?.product_id!!, name = ""), null
                            )
                        } else if (obj?.url?.equals("category")) {
                            onSwitchFragment(
                                Constants.SERVICE_PAGE,
                                Constants.WITH_NAV_DRAWER,
                                CategorylistItem(id = obj?.product_id!!, name = ""),
                                null
                            )
                        }
                    }
                }
            }
            R.id.text_service_name -> {
                if (obj is HomeSearchData) {
                    when (obj?.type) {
                        "category" -> {
                            onSwitchFragment(
                                Constants.SERVICE_PAGE,
                                Constants.WITH_NAV_DRAWER,
                                CategorylistItem(id = obj?.value!!, name = obj?.name),
                                null
                            )
                        }
                        "subcategory" -> {
                            onSwitchFragment(
                                Constants.SUB_SERVICE_PAGE,
                                Constants.WITH_NAV_DRAWER,
                                null,
                                CategorylistItem(id = obj?.value!!, name = obj?.name)
                            )
                        }
                        "product" -> {
                            onSwitchFragment(
                                Constants.STORE_PAGE_BY_PROD,
                                Constants.WITH_NAV_DRAWER,
                                ProductListItem(id = obj?.value!!, name = obj?.name), null
                            )
                        }
                        "store" -> {
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