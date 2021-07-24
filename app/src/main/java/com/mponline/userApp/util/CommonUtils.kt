package com.mponline.userApp.util;

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mponline.userApp.BuildConfig
import com.mponline.userApp.utils.Constants

class  CommonUtils{

    companion object{
        var toast: Toast? = null;
        @SuppressLint("WrongConstant")
        fun createSnackBar(view: View, message: String) {
            hideSoftKeyboard(
                view
            )
//            ColoredSnackbar.info(Snackbar.make(view, message, MESSAGE_DURATION)).show()

            if (!message.equals("")) {
                toast?.run { this.cancel() }
                toast = Toast.makeText(view.context, message, Toast.LENGTH_LONG)
                toast?.run { this.show() }
            }
        }
        fun convertPixelsToDp(px: Float, context: Context): Float {
            return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
        fun openKeyboard(mActivity: Activity) {
            val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }

        fun hideKeyboardView(mAcivity: Activity?, view: View) {
            if (view != null) {
                val imm = mAcivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view!!.getWindowToken(), 0)
            }
        }

        fun showSoftKeyboard(context: Activity) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun hideSoftKeyboard(view: View) {
            val imm =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun printLog(tag: String, message: String) {
            if (BuildConfig.DEBUG) {
                Log.e(tag, message)
            }
        }

        fun loadImageWithGlide(context: Context, urlLink:String, imageView: ImageView){
            urlLink?.run {
                Glide.with(context)
                    .load(urlLink)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.color.)
//                .placeholder(R.color.gray)
                    .into(imageView)
            }
        }

        fun isOnline(context: Context): Boolean {
            val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connMgr?.activeNetworkInfo
            return networkInfo?.isConnected == true
        }

        fun isValidGPSName(name: String): Boolean {
            return name?.matches(Constants.REGEX_GPS_NAME.toRegex())
        }

        open fun convertDpToPixel(context: Context, dp: Int): Int {
            val scale = context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }

        fun getFileExtension(activity: Activity, uri: Uri): String {
            val contentResolver = activity.contentResolver
            val mimeTypeMap = MimeTypeMap.getSingleton()

            // Return file Extension
            return ".${mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))}"
        }

        //Animations
        fun outToLeftAnimation(): Animation? {
            val outtoLeft: Animation = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f
            )
            outtoLeft.duration = 1500
            outtoLeft.interpolator = AccelerateInterpolator()
            return outtoLeft
        }

        fun getFileName(filePath: String): String {
            if (!TextUtils.isEmpty(filePath)) {
                return filePath.substring(filePath.lastIndexOf("/") + 1)
            } else {
                return ""
            }
        }
        fun getFileExt(filePath: String?): String? {
            if (!TextUtils.isEmpty(filePath)) {
                var ext = filePath?.substring(filePath.lastIndexOf(".") + 1)
                CommonUtils.printLog("EXTENSION", ext!!)
                return ext
            } else {
                return ""
            }
        }

        fun showPermissionDialog(context: Activity) {
            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(context)

            // set message of alert dialog
            dialogBuilder.setMessage(
                "You have forcefully denied some of the required permissions " +
                        "for this action. Please open settings, go to permissions and allow them."
            )
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
                    var intent: Intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.getPackageName(), null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    dialog.cancel()
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Permissions Required")
            // show alert dialog
            alert.show()
        }



    }
}