package com.mponline.userApp.ui.activity

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mponline.userApp.R
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.ImageGlideUtils
import kotlinx.android.synthetic.main.activity_file_preview.*
import kotlinx.android.synthetic.main.activity_img_preview.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.item_service.view.*


class FilePreviewActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_preview)
        toolbar_title.text = ""
        image_back?.setOnClickListener {
            finish()
        }

        var imgPath = intent?.getStringExtra("file")

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

    }
}