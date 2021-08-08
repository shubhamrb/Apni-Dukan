package com.mponline.userApp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.mponline.userApp.R
import com.mponline.userApp.ui.base.BaseActivity
import com.mponline.userApp.util.CommonUtils
import com.mponline.userApp.utils.Constants
import com.mponline.userApp.utils.ImageGlideUtils
import com.mponline.userApp.viewmodel.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_file_preview.*
import kotlinx.android.synthetic.main.activity_img_preview.*
import kotlinx.android.synthetic.main.common_toolbar_normal.*
import kotlinx.android.synthetic.main.fragment_change_pwd.*
import kotlinx.android.synthetic.main.item_service.view.*
import kotlinx.android.synthetic.main.layout_progress.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EnquirySupportWebviewActivity : BaseActivity() {

    var mType = ""
    private val file_type = "*/*"    // file types to be allowed for upload
    private val multiple_files = false         // allowing multiple file upload
    internal var webView: WebView? = null
    private var cam_file_data: String? = null        // for storing camera file information
    private var file_data: ValueCallback<Uri>? =
        null       // data/header received after file selection
    private var file_path: ValueCallback<Array<Uri>>? = null     // received file(s) temp. location

    private val file_req_code = 1
    val viewModel: UserListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_preview)
        toolbar_title.text = ""
        image_back?.setOnClickListener {
            finish()
        }
        imgview.visibility = View.GONE
        if (intent.hasExtra("type")) {
            mType = intent.getStringExtra("type")
            if(mType?.equals("help")){
                toolbar_title.text = "Help & Support"
                callHelpSupportWebview()
            }else{
                toolbar_title.text = "Enquiry"
                callEnquiryWebview()
            }
        }
        imgview.visibility = View.GONE
        webview.visibility = View.VISIBLE
        /*val settings: WebSettings = webview.getSettings()
        settings.builtInZoomControls = true
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        // wv.setBackgroundColor(0);
        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);*/
    }

    private fun callHelpSupportWebview() {
        if (CommonUtils.isOnline(this)) {
            viewModel?.getHelpSupportWebview("Bearer "+mPreferenceUtils?.getValue(Constants.USER_TOKEN))?.observe(this, androidx.lifecycle.Observer {
                loadLoanAggrementWebPage(it)
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }
    private fun callEnquiryWebview() {
        if (CommonUtils.isOnline(this)) {
            viewModel?.getEnquiryWebview("Bearer "+mPreferenceUtils?.getValue(Constants.USER_TOKEN))?.observe(this, androidx.lifecycle.Observer {
                loadLoanAggrementWebPage(it)
            })
        } else {
            CommonUtils.createSnackBar(
                findViewById(android.R.id.content)!!,
                resources?.getString(R.string.no_net)!!
            )
        }
    }


    fun loadLoanAggrementWebPage(mLink: String?) {
        webView = findViewById<View>(R.id.webview) as WebView
        assert(webView != null)
        val webSettings = webView?.getSettings()
        webSettings?.javaScriptEnabled = true
        webSettings?.allowFileAccess = true

        if (Build.VERSION.SDK_INT >= 21) {
            webSettings?.mixedContentMode = 0
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else if (Build.VERSION.SDK_INT >= 19) {
            webview?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            webview?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        webview?.setWebViewClient(Callback())
//        webview?.loadData(mLink, "text/html", "UTF-8")
        webview.loadDataWithBaseURL(null, mLink, "text/html", "utf-8", null);
        webview?.setWebChromeClient(object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                CommonUtils.printLog("WEBVIEW:", consoleMessage?.message()!!)
                return super.onConsoleMessage(consoleMessage)
            }

            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@EnquirySupportWebviewActivity)
                newWebView.settings.javaScriptEnabled = true
                view.addView(newWebView)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView

                newWebView.webViewClient = object : WebViewClient() {

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                    }
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        url: String
                    ): Boolean {

                        view.loadUrl(url)
                        CommonUtils.printLog("test", "window shouldOverrideUrlLoading $url")
                        return true
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        CommonUtils.printLog("test", "window onPageFinished $url")
                        super.onPageFinished(view, url)
                    }
                }
                resultMsg.sendToTarget()
                return true
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: WebChromeClient.FileChooserParams
            ): Boolean {

                if (file_permission() && Build.VERSION.SDK_INT >= 21) {
                    file_path = filePathCallback
                    var takePictureIntent: Intent? = null
                    var takeVideoIntent: Intent? = null

                    var includeVideo = false
                    var includePhoto = false

                    /*-- checking the accept parameter to determine which intent(s) to include --*/
                    paramCheck@ for (acceptTypes in fileChooserParams.acceptTypes) {
                        val splitTypes =
                            acceptTypes.split(", ?+".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray() // although it's an array, it still seems to be the whole value; split it out into chunks so that we can detect multiple values
                        for (acceptType in splitTypes) {
                            when (acceptType) {
                                "*/*" -> {
                                    includePhoto = true
                                    includeVideo = true
                                    break@paramCheck
                                }
                                "image/*" -> includePhoto = true
                                "video/*" -> includeVideo = true
                            }
                        }
                    }

                    if (fileChooserParams.acceptTypes.size == 0) {   //no `accept` parameter was specified, allow both photo and video
                        includePhoto = true
                        includeVideo = true
                    }

                    if (includePhoto) {
                        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(this@EnquirySupportWebviewActivity.getPackageManager()) != null) {
                            var photoFile: File? = null
                            try {
                                photoFile = create_image()
                                takePictureIntent.putExtra("PhotoPath", cam_file_data)
                            } catch (ex: IOException) {
                                CommonUtils.printLog("IMGFILE", "Image file creation failed$ex")
                            }

                            if (photoFile != null) {
                                cam_file_data = "file:" + photoFile.absolutePath
                                takePictureIntent.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile)
                                )
                            } else {
                                cam_file_data = null
                                takePictureIntent = null
                            }
                        }
                    }

                    if (includeVideo) {
                        takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                        if (takeVideoIntent.resolveActivity(this@EnquirySupportWebviewActivity.getPackageManager()) != null) {
                            var videoFile: File? = null
                            try {
                                videoFile = create_video()
                            } catch (ex: IOException) {
                                CommonUtils.printLog("INCLUDEVID", "Video file creation failed$ex")
                            }

                            if (videoFile != null) {
                                cam_file_data = "file:" + videoFile.absolutePath
                                takeVideoIntent.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(videoFile)
                                )
                            } else {
                                cam_file_data = null
                                takeVideoIntent = null
                            }
                        }
                    }

                    val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    contentSelectionIntent.type = file_type
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }

                    var intentArray: Array<Intent>? = null
                    if (takePictureIntent != null && takeVideoIntent != null) {
                        intentArray = arrayOf(takePictureIntent, takeVideoIntent)
                    } else if (takePictureIntent != null) {
                        intentArray = arrayOf(takePictureIntent)
                    } else if (takeVideoIntent != null) {
                        intentArray = arrayOf(takeVideoIntent)
                    } else {
                        intentArray = arrayOf<Intent>()
                    }

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    startActivityForResult(chooserIntent, file_req_code)
                    return true
                } else {
                    return false
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= 21) {
            var results: Array<Uri>? = null

            /*-- if file request cancelled; exited camera. we need to send null value to make future attempts workable --*/
            if (resultCode == Activity.RESULT_CANCELED) {
                if (requestCode == file_req_code) {
                    file_path?.onReceiveValue(null)
                    return
                }
            }

            /*-- continue if response is positive --*/
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == file_req_code) {
                    if (null == file_path) {
                        return
                    }

                    var clipData: ClipData?
                    var stringData: String?
                    try {
                        clipData = intent!!.clipData
                        stringData = intent.dataString
                    } catch (e: Exception) {
                        clipData = null
                        stringData = null
                    }

                    if (clipData == null && stringData == null && cam_file_data != null) {
                        results = arrayOf(Uri.parse(cam_file_data))
                    } else {
                        if (clipData != null) { // checking if multiple files selected or not
                            val numSelectedFiles = clipData.itemCount!!
                            results =
                                Array<Uri>(numSelectedFiles) { Uri.parse("") }//arrayOfNulls<Uri>(numSelectedFiles)
                            for (i in 0 until clipData.itemCount) {
                                results[i] = clipData.getItemAt(i).uri
                            }
                        } else {
                            results = arrayOf(Uri.parse(stringData))
                        }
                    }
                }
            }
            file_path?.onReceiveValue(results)
            file_path = null
        } else {
            if (requestCode == file_req_code) {
                if (null == file_data) return
                val result =
                    if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
                file_data?.onReceiveValue(result)
                file_data = null
            }
        }
    }

    /*-- callback reporting if error occurs --*/
    inner class Callback : WebViewClient() {
        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            Toast.makeText(applicationContext, "Failed loading page!", Toast.LENGTH_SHORT).show()
        }

        override fun onPageFinished(view: WebView, url: String) {
            //This method will be executed each time a page finished loading.
            //The only we do is dismiss the progressDialog, in case we are showing any.
//            progressDialogDismiss()
            CommonUtils.printLog("Perfios>>>>>", "DONE_loading " + url)
            relative_progress?.visibility = View.GONE
            progressDialogDismiss()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView,
            authorizationUrl: String
        ): Boolean {
            //This method will be called when the Auth proccess redirect to our RedirectUri.
            //We will check the url looking for our RedirectUri.
            view?.loadUrl(authorizationUrl)
            return true
        }

    }

    /*-- checking and asking for required file permissions --*/
    fun file_permission(): Boolean {
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this@EnquirySupportWebviewActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                1
            )
            return false
        } else {
            return true
        }
    }

    /*-- creating new image file here --*/
    @Throws(IOException::class)
    private fun create_image(): File {
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    /*-- creating new video file here --*/
    @Throws(IOException::class)
    private fun create_video(): File {
        @SuppressLint("SimpleDateFormat")
        val file_name = SimpleDateFormat("yyyy_mm_ss").format(Date())
        val new_name = "file_" + file_name + "_"
        val sd_directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(new_name, ".3gp", sd_directory)
    }

    /*-- back/down key handling --*/
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (webview.canGoBack()) {
                    webview.goBack()
                } else {
                    finish()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}