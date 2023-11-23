package com.mamits.apnaonlines.userv.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mamits.apnaonlines.userv.R


class ImageGlideUtils {

    @Suppress("SENSELESS_COMPARISON")
    companion object {

        fun loadUrlImage(context: Context, urlLink: String, imageView: ImageView, width: Int = 0, height: Int = 0) {
            if (urlLink != null) {
                CommonUtils.printLog("IMAGE URL", urlLink)
                if (width == 0 && height == 0) {
                    Glide.with(context)
                        .load(urlLink)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.color.dark_grey)
                        .placeholder(R.color.dark_grey)
                        .into(imageView)
                } else {
                    Glide.with(context)
                        .load(urlLink)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(width, height)
                        .error(R.color.dark_grey)
                        .placeholder(R.color.dark_grey)
                        .into(imageView)
                }
            }
        }

        fun loadUrlImageWithClearCache(context: Context, url: String, imageView: ImageView) {
            Glide.with(context)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.color.dark_grey)
                .placeholder(R.color.dark_grey)
                .into(imageView)
        }

        fun loadLocalImage(context: Context, path: String, imageView: ImageView) {
            Glide.with(context)
                .load(path)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.color.dark_grey)
                .placeholder(R.color.dark_grey)
                .into(imageView)
        }

        fun loadGIFImage(context: Context, drawable: Int, imageView: ImageView) {
            Glide.with(context)
                .asGif()
                .load(drawable)
                .into(imageView)
        }

        fun loadCircularImage(context: Context, imgPath: String, imageView: ImageView) {
            Glide.with(context).load(imgPath).apply(RequestOptions.circleCropTransform())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    }
}