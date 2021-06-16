package com.mponline.userApp.ui.activity

import android.os.Bundle
import android.os.Environment
import android.print.PdfConverter
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.mponline.userApp.R
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
import java.io.File


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
        webview.getSettings().setJavaScriptEnabled(true)
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
                           var htmlStr = "<html><body><p>WHITE (default)</p></body></html>";//html?.replace("\n","")
                           CommonUtils.printLog("HTML_STR", "${htmlStr}")
                           webview?.loadData(htmlStr, "text/html", "UTF-8")
                           webview.visibility = View.VISIBLE
                           val converter = PdfConverter.getInstance()
                           val file = File(
                               Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(),
                               "APNAONLINE_INVOICE_${mOrderHistoryDataItem?.id}.pdf"
                           )
                           val htmlString =  htmlStr
                           converter.convert(this@InvoicePreviewActivity, htmlString, file)
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