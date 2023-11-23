package com.mamits.apnaonlines.userv.ui.activity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.OnItemClickListener
import com.mamits.apnaonlines.userv.model.response.OrderDetailItem
import com.mamits.apnaonlines.userv.ui.adapter.OrderDetailAdapter
import com.mamits.apnaonlines.userv.ui.base.BaseActivity
import com.mamits.apnaonlines.userv.util.CommonUtils
import kotlinx.android.synthetic.main.activity_form_preview.*
import kotlinx.android.synthetic.main.activity_img_preview.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.fragment_order_history.view.*
import kotlinx.android.synthetic.main.item_form_detail.view.*
import kotlinx.android.synthetic.main.item_service.view.*
import java.io.File

class FormPreviewActivity : BaseActivity(), OnItemClickListener {

    var downloadID:Long = 0
    var from = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_preview)
        toolbar_title.text = ""
        image_back?.setOnClickListener {
           onBackPressed()
        }
        btn_home?.setOnClickListener {
           onBackPressed()
        }

        if(intent?.hasExtra("paymentdone")!!){
            from = intent?.getStringExtra("paymentdone")!!
            text_title?.text = "Payment Done"
        }
        var data = intent?.getParcelableArrayListExtra<OrderDetailItem>("data")
        if(!data?.isNullOrEmpty()!!){
            rv_form_detail?.setHasFixedSize(true)
            rv_form_detail?.layoutManager =
                LinearLayoutManager(
                    this,
                    RecyclerView.VERTICAL,
                    false
                )
            rv_form_detail?.adapter = OrderDetailAdapter(
                this,
                this,
                data!!
            )
        }

    }

    override fun onClick(pos: Int, view: View, obj: Any?) {
        when(view?.id){
            R.id.text_form_val->{
                if(obj is OrderDetailItem){
                    donwloadFile(obj?.filedata?.url!!)
                }
            }
        }
    }

    fun donwloadFile(url:String){
        if (isCameraStoragePermissionGranted()) {
            var extention = CommonUtils.getFileExtentionFromStrPath(url)
            if(extention?.contains(".png", true) || extention?.contains(".jpg", true) || extention?.contains(".jpeg", true)){
                beginDownload(url, CommonUtils.getFileExtentionFromStrPath(url))
            }else{
                beginDownload(url, CommonUtils.getFileExtentionFromStrPath(url))
            }
        } else {
            checkCameraStoragePermissions()
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
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        request.setMimeType("*/${type?.replace(".","")}");
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        CommonUtils.createSnackBar(
            findViewById(android.R.id.content)!!,
            "Downloading started..."
        )

        //Broadcast reciever for download complete msg
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE != action) {
                    return
                }
                unregisterReceiver(this)
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
                            findViewById(android.R.id.content)!!,
                            "Donwloaded successfully"
                        )
                    } else {
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            "Donwloading failed!"
                        )
                        CommonUtils.printLog("LK_AGGRE_DONWLOADED", "download failed " + c.getInt(columnIndex))
                    }
                }
            }
        }
        registerReceiver(receiver,  IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    override fun onBackPressed() {
        if(from?.equals("paymentdone")){
            var intent: Intent = Intent(this, MainActivity::class.java)
            intent.putExtra("from", "NOTI_history")
            startActivity(intent)
            finish()
        }else{
            super.onBackPressed()
        }
    }
}