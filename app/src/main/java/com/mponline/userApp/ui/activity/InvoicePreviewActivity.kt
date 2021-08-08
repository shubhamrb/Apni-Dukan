package com.mponline.userApp.ui.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.print.PdfConverter
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.mponline.userApp.listener.OnItemClickListener
import com.mponline.userApp.model.response.OrderHistoryDataItem
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_file_preview.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_progress.*
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.widget.Button;
import android.widget.Toast;
import com.mponline.userApp.R

@AndroidEntryPoint
class InvoicePreviewActivity : BaseActivity(), OnItemClickListener {

    var mOrderHistoryDataItem: OrderHistoryDataItem? = null
    val viewModel: UserListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_preview)
        toolbar_title.text = ""
        if (intent?.hasExtra("order")!!) {
            mOrderHistoryDataItem = intent?.getParcelableExtra("order")
            if (mOrderHistoryDataItem != null) {
                callInvoice()
            }
        }

        image_download.setOnClickListener {
            PrintTheWebPage(webview, mOrderHistoryDataItem?.id!!)
        }
    }

    private fun callInvoice() {
        if (CommonUtils.isOnline(this!!)) {
            switchView(3, "")
            var commonRequestObj =
                getCommonRequestObj(
                    apiKey = getApiKey(),
                    orderid = mOrderHistoryDataItem?.id!!
                )
            viewModel?.getInvoice(commonRequestObj)?.observe(this, Observer {
                it?.run {
                    if (status) {
                       if(html!=null && !html?.isNullOrEmpty()){
                           var htmlStr = html?.replace("\n","").replace("\\","")//"<html><body><p>WHITE (default)</p></body></html>";//html?.replace("\n","")
                           CommonUtils.printLog("HTML_STR", "${htmlStr}")
                           webview.visibility = View.VISIBLE
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
//                           settings.setAppCacheEnabled(false)
//                           settings.setAppCachePath(applicationContext.cacheDir.path)
//                           settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//                           webview.setWebViewClient(WebViewClient())
                           val converter = PdfConverter.getInstance()
//                           val file = File(
//                               Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(),
//                               "APNAONLINE_INVOICE_${mOrderHistoryDataItem?.id}.pdf"
//                           )
//                           webview?.loadData(htmlStr, "text/html; charset=utf-8", "UTF-8")
                           webview.loadDataWithBaseURL(null, htmlStr, "text/html", "utf-8", null);

                           val htmlString =  htmlStr
//                           converter.convert(this@InvoicePreviewActivity, htmlString, file)
                           webview.setWebViewClient(object : WebViewClient() {
                               override fun onPageFinished(view: WebView?, url: String?) {
                                   super.onPageFinished(view, url)
                                   switchView(1, "")
                               }
                           })
                       }
                    } else {
                        switchView(0, "")
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

    // object of print job
//    var printJob: PrintJob? = null

    // a boolean to check the status of printing
    var printBtnPressed = false

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun PrintTheWebPage(webView: WebView, orderid:String) {

        // set printBtnPressed true
        printBtnPressed = true

        // Creating  PrintManager instance
        val printManager = this
            .getSystemService(Context.PRINT_SERVICE) as PrintManager

        // setting the name of job
        val jobName = "APNAONLINE_INVOICE_${orderid}"

        // Creating  PrintDocumentAdapter instance
        val printAdapter = webView.createPrintDocumentAdapter(jobName)
        assert(printManager != null)
        var printJob = printManager.print(
            jobName, printAdapter,
            PrintAttributes.Builder().build()
        )
        if (printJob != null && printBtnPressed) {
            if (printJob?.isCompleted()) {
                // Showing Toast Message
                Toast.makeText(this, "Completed", Toast.LENGTH_SHORT).show();
            } else if (printJob.isStarted()) {
                // Showing Toast Message
                Toast.makeText(this, "isStarted", Toast.LENGTH_SHORT).show();

            } else if (printJob.isBlocked()) {
                // Showing Toast Message
                Toast.makeText(this, "isBlocked", Toast.LENGTH_SHORT).show();

            } else if (printJob.isCancelled()) {
                // Showing Toast Message
                Toast.makeText(this, "isCancelled", Toast.LENGTH_SHORT).show();

            } else if (printJob.isFailed()) {
                // Showing Toast Message
                Toast.makeText(this, "isFailed", Toast.LENGTH_SHORT).show();

            } else if (printJob.isQueued()) {
                // Showing Toast Message
                Toast.makeText(this, "isQueued", Toast.LENGTH_SHORT).show();

            }
            // set printBtnPressed false
            printBtnPressed = false;
        }
    }

    override fun onClick(pos: Int, view: View, obj: Any?) {

    }

    fun switchView(i: Int, msg: String) {
        when (i) {
            0 -> {
                relative_progress?.visibility = View.GONE
                webview?.visibility = View.GONE
                relative_empty?.visibility = View.VISIBLE
            }
            1 -> {
                relative_progress?.visibility = View.GONE
                webview?.visibility = View.VISIBLE
                relative_empty?.visibility = View.GONE
                image_download?.visibility = View.VISIBLE
            }
            2 -> {
                webview?.visibility = View.GONE
                relative_progress?.visibility = View.GONE
                relative_empty?.visibility = View.VISIBLE
            }
            3 -> {
                relative_empty?.visibility = View.GONE
                relative_progress?.visibility = View.VISIBLE
                webview?.visibility = View.GONE
            }
        }
    }

}