package com.mponline.userApp.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class GetHomeDataResponse(@SerializedName("data")
                               val data: HomeData,
                               @SerializedName("success")
                               val success: Boolean = false,
                               @SerializedName("messageId")
                               val messageId: Int = 0,
                               @SerializedName("message")
                               val message: String = "")


data class StorelistItem(@SerializedName("distance")
                         val distance: Int = 0,
                         @SerializedName("ratting")
                         val ratting: Float = 0f,
                         @SerializedName("latitude")
                         val latitude: String = "",
                         @SerializedName("IsAvailable")
                         val isAvailable: Int = 0,
                         @SerializedName("name")
                         val name: String = "",
                         @SerializedName("storelogo")
                         val storelogo: String = "",
                         @SerializedName("image")
                         val image: String = "",
                         @SerializedName("id")
                         val id: Int = 0,
                         @SerializedName("longitude")
                         val longitude: String = "")


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
                          @SerializedName("image")
                          val image: String = "",
                          @SerializedName("id")
                          val id: Int = 0):Parcelable


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
                            @SerializedName("IsHomePage")
                            val isHomePage: Int = 0,
                            @SerializedName("name")
                            val name: String = "",
                            @SerializedName("id")
                            val id: String = "",
                            @SerializedName("banner_image")
                            val bannerImage: String = "",
                            @SerializedName("category_order")
                            val categoryOrder: Int = 0):Parcelable


