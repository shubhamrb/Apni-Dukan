package com.mponline.userApp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import com.mponline.userApp.utils.Constants.Companion.APP_FILEPROVIDER
import com.mponline.userApp.utils.Constants.Companion.REQUEST_CAMERA
import com.mponline.userApp.utils.Constants.Companion.REQUEST_GALLERY
import com.mponline.userApp.utils.ImageGlideUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

class CameraGalleryUtils {

    var mCurrentPhotoPath: String = ""
    var mActivity: Activity = Activity()
    var activity: Activity = Activity()
    var imageView: ImageView? = null
    var textView: TextView? = null


    fun setContext(activity: Activity): CameraGalleryUtils {
        this.mActivity = activity
        this.activity = activity
        return this
    }

    fun setView(imageView: ImageView, textView: AppCompatTextView): CameraGalleryUtils {
        this.imageView = imageView
        this.textView = textView
        return this
    }

    fun setView(imageView: ImageView): CameraGalleryUtils {
        this.imageView = imageView
        this.textView = textView
        return this
    }

    @SuppressLint("SimpleDateFormat")
    fun createImageFile(activity: Activity = mActivity, extension: String = ".jpg"): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        var storageDir: File? = null
        if (extension?.contains(".jpg") || extension?.contains(".png")) {
            storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        } else {
            storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        }

        val image = File.createTempFile(
            imageFileName, /* prefix */
            if (extension?.contains(".jpg") || extension?.contains(".png")) ".jpg" else extension, /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    @SuppressLint("SimpleDateFormat")
    fun createVideoFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "VIDEO_" + timeStamp + "_"
        val storageDir = mActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".mp4", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    @Throws(IOException::class)
    fun setUpPhotoFile(): File {

        val f = createImageFile()
        mCurrentPhotoPath = f.absolutePath

        return f
    }

    fun setPic() {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        val targetW = imageView?.width
        val targetH = imageView?.height

        /* Get the size of the image */
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        /* Figure out which way needs to be reduced less */
        var scaleFactor = 1
        if (targetW!! > 0 || targetH!! > 0) {
            scaleFactor = Math.min(photoW / targetW!!, photoH / targetH!!)
        }

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true

        /* Decode the JPEG file into a Bitmap */
        val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)

        /* Associate the Bitmap to the ImageView */
        imageView?.setImageBitmap(bitmap)
    }

    fun galleryAddPic(activity: Activity = mActivity) {
        val mediaScanIntent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
        val f = File(mCurrentPhotoPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        activity.sendBroadcast(mediaScanIntent)
    }

    fun dispatchTakePictureIntent(activity: Activity) {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            // Create the File where the photo should go
            try {
                val photoFile: File = createImageFile()
                val photoURI: Uri =
                    FileProvider.getUriForFile(activity, APP_FILEPROVIDER, photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    activity!!,
                    takePictureIntent,
                    REQUEST_CAMERA,
                    null
                )
            } catch (e: java.lang.Exception) {
                CommonUtils.printLog("zxfsdG", e.printStackTrace().toString())
            }
        }

    }

    fun dispatchSelfiePictureIntent(facingLockInCamera: Boolean = false, cameraMode: String = "") {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mActivity.packageManager) != null) {
            // Create the File where the photo should go
            try {
                val photoFile: File = createImageFile()
                val photoURI: Uri =
                    FileProvider.getUriForFile(mActivity, APP_FILEPROVIDER, photoFile)
                if (facingLockInCamera) {
                    if (!TextUtils.isEmpty(cameraMode)) {
                        takePictureIntent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            CameraCharacteristics.LENS_FACING_BACK
                        )
                    }
                } else {
                    takePictureIntent.putExtra(
                        "android.intent.extras.CAMERA_FACING",
                        CameraCharacteristics.LENS_FACING_FRONT
                    )
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                ActivityCompat.startActivityForResult(
                    mActivity,
                    takePictureIntent,
                    REQUEST_CAMERA,
                    null
                )
            } catch (e: java.lang.Exception) {
                CommonUtils.printLog("zxfsdG", e.printStackTrace().toString())
            }
        }


    }

    fun openGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        if (galleryIntent.resolveActivity(activity.packageManager) != null) {
            galleryIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivityForResult(galleryIntent, REQUEST_GALLERY)
        } else {
            val gallery = Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )
            if (gallery.resolveActivity(activity.packageManager) != null) {
                gallery.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                activity.startActivityForResult(gallery, REQUEST_GALLERY)
            }
        }
    }

    fun choosePhotoFromGallary(activity: Activity) {
//        val gallery = Intent(
//            Intent.ACTION_PICK,
//            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
//        )
//        gallery.type = "image/*"

        val gallery = Intent(Intent.ACTION_OPEN_DOCUMENT)
        gallery.addCategory(Intent.CATEGORY_OPENABLE)
        gallery.type = "image/*"

        if (gallery.resolveActivity(activity.packageManager) != null) {
            gallery.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivityForResult(gallery, REQUEST_GALLERY)
        }
    }

    fun openGalleryDocument(isOnlyImg: Boolean = false) {
        var mimeTypes =
            arrayOf(
                "image/*",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/doc", // .doc & .docx
//            "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/xlsx",
                "application/xls", // .xls & .xlsx
//            "text/plain",
                "application/pdf"
//            "application/zip"
            )
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
        val mimetypes =
            if (isOnlyImg) "image/*" else mimeTypes//arrayOf("image/*","application/xlsx", "application/pdf","application/xls","application/doc")
        galleryIntent.type = "*/*"
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        if (galleryIntent.resolveActivity(activity.packageManager) != null) {
            galleryIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activity.startActivityForResult(galleryIntent, REQUEST_GALLERY)
        }
    }

    fun openEsGalleryDocument() {
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
        galleryIntent.type = "*/*"
        galleryIntent.putExtra(
            Intent.EXTRA_MIME_TYPES, arrayOf(
                "image/jpeg", "image/png", "image/jpg",
                "application/pdf"
            )
        )
        if (galleryIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(galleryIntent, REQUEST_GALLERY)
        }
    }

    fun openEsNachGalleryDocument() {
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
        galleryIntent.type = "*/*"
        galleryIntent.putExtra(
            Intent.EXTRA_MIME_TYPES, arrayOf(
                "image/jpeg", "image/png", "image/jpg"
            )
        )
        if (galleryIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(galleryIntent, REQUEST_GALLERY)
        }
    }

    fun handleBigCameraPhoto(): String {

//            setPic()
        galleryAddPic()
        val baos = ByteArrayOutputStream()
        val bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val imageBytes = baos.toByteArray()
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
    }

    fun showImage(mPhotoPath: String, name: String) {
        if (!TextUtils.isEmpty(mPhotoPath)) {
            mCurrentPhotoPath = mPhotoPath
        }
        textView?.text = name
        ImageGlideUtils.loadLocalImage(activity, mCurrentPhotoPath, imageView!!)
    }

    fun showImage(mPhotoPath: String, name: String, imageView: ImageView, textView: TextView) {
        textView?.text = name
        ImageGlideUtils.loadLocalImage(activity, mPhotoPath, imageView)
    }

    fun showImage(mPhotoPath: String, name: String, imgCaptureSelfie: ImageView) {
        ImageGlideUtils.loadLocalImage(activity, mPhotoPath, imgCaptureSelfie)
    }


    fun getImagePath(uri: Uri): String {
        var cursor = activity.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        var document_id = cursor?.getString(0)
        document_id = document_id?.substring(document_id.lastIndexOf(":") + 1)
        cursor?.close()

        cursor = activity.contentResolver.query(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
            """${MediaStore.Images.Media._ID} = $document_id""", null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            mCurrentPhotoPath =
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            cursor.close()
        }

        return mCurrentPhotoPath
    }


    fun saveImageFromURL(strURL: String): String {
        val bytearrayoutputstream = ByteArrayOutputStream()
        val fileoutputstream: FileOutputStream
        val file = createImageFile()
        try {
            val finalBitmap: Bitmap = getBitmapFromURL(strURL)!!
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream)
            file.createNewFile()
            fileoutputstream = FileOutputStream(file)
            fileoutputstream.write(bytearrayoutputstream.toByteArray())
            fileoutputstream.close()
        } catch (e: Exception) {
//            e.printStackTrace()
        }
        return file.absolutePath
    }

    fun getBitmapFromURL(strURL: String): Bitmap? {
        try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
//            e.printStackTrace()
            return null
        }

    }
}