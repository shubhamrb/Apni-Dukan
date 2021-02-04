package com.mponline.vendorApp.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageOrientationChecker {

    fun changeOrientation(mCurrentPhotoPath: String, bitmapGallery: Bitmap): Bitmap? {
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(mCurrentPhotoPath)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val orientation = exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)
        CommonUtils.printLog("ORIENTATION_CAMERA", ""+orientation)
        return rotateBitmap(bitmapGallery, orientation)
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        try {
            val bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            return bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            return null
        }

    }

    fun imagePreviewCamera(file: File, needCompress:Boolean = false, customWidth:Int = 0, customHeight:Int = 0) {
        try {
            val originalBitmap = BitmapFactory.decodeFile(file.path)
            val options = BitmapFactory.Options()
            options.inPurgeable = true
            val maxSize = 1080
            val outWidth: Int
            val outHeight: Int
            val inWidth = originalBitmap.width
            val inHeight = originalBitmap.height
            if (inWidth > inHeight) {
                outWidth = maxSize
                outHeight = inHeight * maxSize / inWidth
            } else {
                outHeight = maxSize
                outWidth = inWidth * maxSize / inHeight
            }
            var processedImage = Bitmap.createScaledBitmap(originalBitmap, outWidth, outHeight, true)
            val orientedImage = changeOrientation(file.path, processedImage)
            if(needCompress){
                var newWidth = outWidth
                var newHeight = outHeight
                var aspectRatio = (outWidth/outHeight)
                if((newWidth > 800 || outHeight > 800)) {
                    try {
                        if(aspectRatio>0) {
                            newWidth = 800
                            newHeight = newWidth / aspectRatio
                        }
                    }catch (e:Exception){

                    }
                }
                if(customWidth!=0 && customHeight!=0){
                    newWidth = customWidth
                    newHeight = customHeight
                }
                val resizedImg =
                    Bitmap.createScaledBitmap(orientedImage!!, newWidth, newHeight, true)
                CommonUtils.printLog(
                    "RESIZED_BITMAP",
                    "W=${resizedImg?.width} & H=${resizedImg.height} , Original Size =${orientedImage?.width} & H=${orientedImage?.height}"
                )
                processedImage = resizedImg
            }else{
                processedImage = orientedImage
            }

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            processedImage.compress(Bitmap.CompressFormat.JPEG, 80, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

        } catch (e: Exception) {
            CommonUtils.printLog("Exception camera", e.toString())
        }
    }

    fun getCopyOfImage(oldfile: File, newfile:File) {
        try {
            val originalBitmap = BitmapFactory.decodeFile(oldfile.path)
            val options = BitmapFactory.Options()
            options.inPurgeable = true
            val maxSize = 1080
            val outWidth: Int
            val outHeight: Int
            val inWidth = originalBitmap.width
            val inHeight = originalBitmap.height
            if (inWidth > inHeight) {
                outWidth = maxSize
                outHeight = inHeight * maxSize / inWidth
            } else {
                outHeight = maxSize
                outWidth = inWidth * maxSize / inHeight
            }
            var processedImage = Bitmap.createScaledBitmap(originalBitmap, outWidth, outHeight, true)
            val orientedImage = changeOrientation(oldfile.path, processedImage)

            processedImage = orientedImage

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            processedImage.compress(Bitmap.CompressFormat.JPEG, 80, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(newfile)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()

        } catch (e: Exception) {
            CommonUtils.printLog("Exception camera", e.toString())
        }

    }

}
