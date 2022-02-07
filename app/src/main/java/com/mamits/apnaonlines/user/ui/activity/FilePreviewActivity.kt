package com.mamits.apnaonlines.user.ui.activity

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
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mamits.apnaonlines.user.R
import com.mamits.apnaonlines.user.ui.base.BaseActivity
import com.mamits.apnaonlines.user.util.CommonUtils
import com.mamits.apnaonlines.user.util.ImageGlideUtils
import kotlinx.android.synthetic.main.activity_file_preview.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import java.io.File


class FilePreviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_preview)
        toolbar_title.text = ""
        image_back?.setOnClickListener {
            finish()
        }

        var imgPath = intent?.getStringExtra("file")
        if(intent?.hasExtra("from")!!){
            toolbar_title.text = "Click Here To Download"
            image_download.visibility = View.VISIBLE
        }

        if(!imgPath?.isNullOrEmpty()!! && (imgPath?.endsWith(".jpg",true) || imgPath?.endsWith(".jpeg",true) || imgPath?.endsWith(".png",true))){
            imgview.visibility = View.VISIBLE
            webview.visibility = View.GONE
            ImageGlideUtils.loadLocalImage(this, imgPath, imgview)
        }else{
            imgview.visibility = View.GONE
            webview.visibility = View.VISIBLE
            webview.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                    view.loadUrl(url)
                    return false
                }
            })
            val settings: WebSettings = webview.getSettings()
            settings.builtInZoomControls = true
//                           settings.domStorageEnabled = true
            settings.javaScriptEnabled = true
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setJavaScriptEnabled(true);
            settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            // wv.setBackgroundColor(0);
            webview.setVerticalScrollBarEnabled(false);
            webview.setHorizontalScrollBarEnabled(false);
            if(imgPath?.endsWith(".pdf")){
                var url = "https://docs.google.com/gview?embedded=true&url=${imgPath}"
                CommonUtils.printLog("PDF_URL","${url}")
                webview.loadUrl(url)
            }else{
                webview.loadUrl(imgPath)
            }
        }

        image_download.setOnClickListener {
            donwloadFile(imgPath)
        }

    }

    fun donwloadFile(url:String){
        if (isCameraStoragePermissionGranted()) {
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
        var downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        CommonUtils.createSnackBar(
            findViewById(android.R.id.content)!!,
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
                            findViewById(android.R.id.content)!!,
                            "Donwload successfully"
                        )
                    } else {
                        CommonUtils.createSnackBar(
                            findViewById(android.R.id.content)!!,
                            "Donwload failed!"
                        )
                        CommonUtils.printLog("LK_AGGRE_DONWLOADED", "download failed " + c.getInt(columnIndex))
                    }
                }
            }
        }
        applicationContext?.registerReceiver(receiver,  IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

}