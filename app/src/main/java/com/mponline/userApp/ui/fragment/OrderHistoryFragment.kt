package com.mponline.userApp.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mponline.userApp.R
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.listener.OnSwichFragmentListener
import com.mponline.userApp.model.LocationUtils
import com.mponline.userApp.model.TimerObj
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.activity.FormPreviewActivity
import com.mponline.userApp.ui.adapter.*
import com.mponline.userApp.ui.base.BaseFragment
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.DateUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import kotlinx.android.synthetic.main.fragment_order_history.view.ll_container
import kotlinx.android.synthetic.main.fragment_order_history.view.relative_frag
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_order_complete_list.view.*
import kotlinx.android.synthetic.main.layout_order_pending_list.view.*
import kotlinx.android.synthetic.main.layout_progress.*
import java.io.File
import java.util.HashMap


@AndroidEntryPoint
class OrderHistoryFragment : BaseFragment(), OnItemClickListener {
    override fun onNetworkChange(isConnected: Boolean) {

    }

    var mView: View? = null
    val viewModel: UserListViewModel by viewModels()
    var mSwichFragmentListener: OnSwichFragmentListener? = null
    var isPostApplnSubmit = false
    var mOrderlist:ArrayList<OrderHistoryDataItem> = arrayListOf()
    var mAdapter:OrderHistoryAdapter? = null
    var downloadID:Long = 0
    var timerMap = HashMap<Int, CountDownTimer>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater!!.inflate(R.layout.fragment_order_history, container, false)

        return mView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context!=null){
            mSwichFragmentListener = context as OnSwichFragmentListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            isPostApplnSubmit = it?.getBoolean("flag")
        }
        if(isPostApplnSubmit){
            view?.text_title?.setText("Form submitted successfully")
            view?.text_subtitle?.setText(activity?.resources?.getString(R.string.post_submit_desc))
            view?.text_subtitle?.visibility = View.VISIBLE
        }else{
            view?.text_title?.setText("Order History")
            view?.text_subtitle?.visibility = View.GONE
        }

        view?.relative_frag?.setOnClickListener {  }

        callOrderHistoryApi()
    }

    private fun callOrderHistoryApi() {
        if (CommonUtils.isOnline(activity!!)) {
            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                latitude = LocationUtils?.getCurrentLocation()?.lat!!,
                longitude = LocationUtils?.getCurrentLocation()?.lng!!
            )
            viewModel?.getOrderHistory(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                        if(data!=null && data?.size>0){
                            mOrderlist = this?.data!!
                            view?.rv_order_history?.setHasFixedSize(true)
                            view?.rv_order_history?.layoutManager =
                                LinearLayoutManager(
                                    activity,
                                    RecyclerView.VERTICAL,
                                    false
                                )
                            data?.forEachIndexed { index, order ->
                                if (order?.status == 2) {
                                    var currDateTime = DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss")
                                    var estimatedDateTime = DateUtils.addHrMinuteToDateStr(
                                        order?.acceptedAt!!,
                                        if (order?.timeType?.equals("hour")!!) true else false,
                                        order?.orderCompletionTime
                                    )
                                    var timerObj = DateUtils.checkTimeDifference(currDateTime, estimatedDateTime)
                                    data?.get(index)?.timerObj = timerObj
                                }
                            }
                            mAdapter = OrderHistoryAdapter(
                                activity,
                                this@OrderHistoryFragment,
                                data!!
                            )
                            view?.rv_order_history?.adapter = mAdapter
                            Handler().postDelayed(Runnable {
                                switchView(1, "")
                            },500)
                        }else{
                            switchView(0, "No Orders Found")
                        }
                    } else {
                        switchView(2, "No Network connection")
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            resources?.getString(R.string.no_net)!!
                        )
                    }
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    fun startTimerForOrders(pos:Int, timerObj:TimerObj){
        var timer = timerMap?.get(pos)
      /*  if (timer == null) {
            timer = object : CountDownTimer(timerObj?.totalMillis!!, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    var remainingTimeObj = DateUtils.getTimeObjFromMillis(millisUntilFinished)
                    itemView?.text_hour?.text = remainingTimeObj?.hour
                    itemView?.text_minute?.text = remainingTimeObj?.min
                    itemView?.text_sec?.text = remainingTimeObj?.sec
                }

                override fun onFinish() {
                    itemView?.text_hour?.text = "0"
                    itemView?.text_minute?.text = "0"
                    itemView?.text_sec?.text = "0"
                }
            }.start()
        }else{

        }*/
       /* if(timer==null){
             timer = object : CountDownTimer(orderHistoryDataItem?.timerObj?.totalMillis!!, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    var remainingTimeObj = DateUtils.getTimeObjFromMillis(millisUntilFinished)
                    itemView?.text_hour?.text = remainingTimeObj?.hour
                    itemView?.text_minute?.text = remainingTimeObj?.min
                    itemView?.text_sec?.text = remainingTimeObj?.sec
                }

                override fun onFinish() {
                    itemView?.text_hour?.text = "0"
                    itemView?.text_minute?.text = "0"
                    itemView?.text_sec?.text = "0"
                }
            }.start()
        }*/
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        mSwichFragmentListener?.onSwichToolbar(Constants.SHOW_NAV_DRAWER_TOOLBAR,"",null)
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.cv_store->{
                mSwichFragmentListener?.onSwitchFragment(Constants.STORE_PAGE, Constants.WITH_NAV_DRAWER, null, null)
            }
            R.id.ll_download_files->{
                if(obj is OrderHistoryDataItem){
                    donwloadFile(obj?.paymentFile!!)
                }
            }
            R.id.text_make_payment->{
                if(obj is OrderHistoryDataItem) {
                    if(obj?.paymentStatus == 1){
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            "You have already paid for this order!"
                        )
                    }else{
                        mSwichFragmentListener?.onSwitchFragment(
                            Constants.PAYMENT_SUMMARY_PAGE,
                            Constants.WITH_NAV_DRAWER,
                            obj,
                            null
                        )
                    }
                }
            }
            R.id.ll_submit_rating->{
                if(obj is OrderHistoryDataItem) {
                    callSaveRating(obj, pos)
                }
            }
            R.id.text_view_details->{
                if(obj is OrderHistoryDataItem){
                    var intent:Intent = Intent(activity!!, FormPreviewActivity::class.java)
                    intent?.putExtra("data", obj?.orderDetail!!)
                    activity?.startActivity(intent)
                }
            }
            R.id.rl_chat->{
                if(obj is OrderHistoryDataItem) {
                    mSwichFragmentListener?.onSwitchFragment(
                        Constants.CHAT_MSG_PAGE_FROM_DETAIL,
                        Constants.WITH_NAV_DRAWER,
                        obj?.id,
                        obj.storedetail?.userId
                    )
                }
            }
            R.id.rl_call->{
                if(obj is OrderHistoryDataItem) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${obj?.storedetail?.mobileNumber}")
                    activity?.startActivity(intent)
                }
            }
            R.id.rl_whatsapp->{
                if(obj is OrderHistoryDataItem) {
                    val url = "https://api.whatsapp.com/send?phone=${if(obj?.storedetail?.whatsappNo?.startsWith("+91")!!) obj?.storedetail?.whatsappNo!! else "+91"+obj?.storedetail?.whatsappNo!!}"
                    try {
                        val pm: PackageManager = activity?.packageManager!!
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        activity?.startActivity(i)
                    } catch (e: PackageManager.NameNotFoundException) {
                        activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                }
            }
        }
    }

    private fun callSaveRating(mOrderHistoryDataItem: OrderHistoryDataItem, pos:Int) {
        if (CommonUtils.isOnline(activity!!)) {
//            switchView(3, "")
            var commonRequestObj = getCommonRequestObj(
                apiKey = getApiKey(),
                orderid = mOrderHistoryDataItem?.id!!,
                storeid = mOrderHistoryDataItem?.storeId!!,
                rating = mOrderHistoryDataItem?.myrating!!
            )
            viewModel?.saveRating(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    mOrderlist?.get(pos)?.ratingStatus = "1"
                    mOrderlist?.get(pos)?.userratting = mOrderHistoryDataItem?.myrating!!
                    mAdapter?.refreshAdapter(mOrderlist)
                    CommonUtils.createSnackBar(
                        activity?.findViewById(android.R.id.content)!!,
                        message
                    )
                }
            })
        } else {
            CommonUtils.createSnackBar(
                activity?.findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }

    fun donwloadFile(url:String){
        if (isCameraStoragePermissionGranted(activity!!)) {
            var extention = CommonUtils.getFileExtentionFromStrPath(url)
            if(extention?.contains(".png", true) || extention?.contains(".jpg", true) || extention?.contains(".jpeg", true)){
                /*CommonUtils.downloadImgFromUrl(context!!, url, extention)
                CommonUtils.createSnackBar(
                    activity?.findViewById(android.R.id.content)!!,
                    "Donwload successfully"
                )*/
                beginDownload(url, CommonUtils.getFileExtentionFromStrPath(url))
            }else{
                beginDownload(url, CommonUtils.getFileExtentionFromStrPath(url))
            }
        } else {
            checkCameraStoragePermissions(requireActivity())
        }
    }

    @SuppressLint("NewApi")
    private fun beginDownload(donwloadUrl:String, type:String) {
        val file =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "ApnaOnline_"+System.currentTimeMillis()+"${type}"
            )
        CommonUtils.printLog("AGGREMENT_URL", "${donwloadUrl}")
        val request =
            DownloadManager.Request(Uri.parse(donwloadUrl))
                .setTitle("Apna Online File")// Title of the Download Notification
                .setDescription("Downloading...")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                .setRequiresCharging(false)// Set if charging is required to begin the download
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true)// Set if download is allowed on roaming network
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ApnaOnline_"+System.currentTimeMillis()+"${type}");
        val downloadManager = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        request.setMimeType("*/${type?.replace(".","")}");
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        CommonUtils.createSnackBar(
            activity?.findViewById(android.R.id.content)!!,
            "Download started..."
        )

        //Broadcast reciever for download complete msg
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE != action) {
                    return
                }
                context.applicationContext.unregisterReceiver(this)
                val query = DownloadManager.Query()
                query.setFilterById(downloadID)
                val c = downloadManager.query(query)
                if (c.moveToFirst()) {
                    val columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        val uriString =
                            c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        CommonUtils.printLog("LK_AGGRE_DONWLOADED", "downloaded file $uriString")
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            "Donwload successfully"
                        )
                    } else {
                        CommonUtils.createSnackBar(
                            activity?.findViewById(android.R.id.content)!!,
                            "Donwload failed!"
                        )
                        CommonUtils.printLog("LK_AGGRE_DONWLOADED", "download failed " + c.getInt(columnIndex))
                    }
                }
            }
        }
        activity?.applicationContext?.registerReceiver(receiver,  IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    fun switchView(i: Int, msg: String) {
        mView?.run {
            when (i) {
                0 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    ll_container?.visibility = View.GONE
                    text_empty?.text = msg
                }
                1 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.VISIBLE
                }
                2 -> {
                    relative_progress?.visibility = View.GONE
                    relative_empty?.visibility = View.VISIBLE
                    text_empty?.text = msg
                }
                3 -> {
                    relative_progress?.visibility = View.VISIBLE
                    relative_empty?.visibility = View.GONE
                    ll_container?.visibility = View.GONE
                }
            }
        }
    }


    companion object {
        fun newInstance(
            context: Activity,
            isPostApplnSubmit:Boolean
        ): Fragment {
            val fragment = OrderHistoryFragment()
            val bundle = Bundle()
            bundle.putBoolean("flag", isPostApplnSubmit)
            fragment.arguments = bundle
            return fragment
        }
    }


}