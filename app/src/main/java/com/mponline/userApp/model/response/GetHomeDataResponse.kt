package com.mponline.userApp.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class GetHomeDataResponse(@SerializedName("data")
                               val data: HomeData? = null,
                               @SerializedName("status")
                               val status: Boolean = false,
                               @SerializedName("messageId")
                               val messageId: String = "",
                               @SerializedName("message")
                               val message: String = "")

@Parcelize
data class StorelistItem(@SerializedName("price")
                         val price: String = "",
                         @SerializedName("distance")
                         val distance: String = "",
                         @SerializedName("ratting")
                         val ratting: String = "",
                         @SerializedName("latitude")
                         val latitude: String = "",
                         @SerializedName("IsAvailable")
                         val isAvailable: String = "",
                         @SerializedName("name")
                         val name: String = "",
                         @SerializedName("storelogo")
                         val storelogo: String = "",
                         @SerializedName("image")
                         val image: String = "",
                         @SerializedName("id")
                         val id: String = "",
                         @SerializedName("longitude")
                         val longitude: String = ""):Parcelable


data class HomeData(@SerializedName("bottom_bannerlist")
                var bottom_bannerlist: ArrayList<BannerlistItem>?,
                @SerializedName("bannerlist")
                var bannerlist: ArrayList<BannerlistItem>?,
                @SerializedName("productlist")
                var productlist: ArrayList<ProductItem>?,
                @SerializedName("storelist")
                val storelist: ArrayList<StorelistItem>?,
                 @SerializedName("top_storelist")
                val top_storelist: ArrayList<StorelistItem>?,
                @SerializedName("categorylist")
                var categorylist: ArrayList<CategorylistItem>?)

@Parcelize
data class BannerlistItem(@SerializedName("url")
                          val url: String = "",
                          @SerializedName("product_id")
                          val product_id: String = "",
                          @SerializedName("image")
                          val image: String = "",
                          @SerializedName("id")
                          val id: String = ""):Parcelable


@Parcelize
data class ProductItem(@SerializedName("id")
                          val id: String = "",
                          @SerializedName("image")
                          val image: String = "",
                          @SerializedName("name")
                          val name: String = ""):Parcelable

@Parcelize
data class CategorylistItem(@SerializedName("image")
                            val image: String = "",
                           @SerializedName("storeId")
                            var storeId: String = "",
                            @SerializedName("name")
                            val name: String = "",
                            @SerializedName("id")
                            val id: String = "",
                            @SerializedName("banner_image")
                            val bannerImage: String = ""):Parcelable


