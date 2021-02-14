package com.mponline.userApp.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductListItem(@SerializedName("image")
                           val image: String = "",
                           @SerializedName("short_description")
                           val shortDescription: String = "",
                           @SerializedName("category_id")
                           val categoryId: String = "",
                           @SerializedName("sub_category_id")
                           val subCategoryId: Int = 0,
                           @SerializedName("name")
                           val name: String = "",
                           @SerializedName("id")
                           val id: String = ""):Parcelable


data class GetProductByCategoryResponse(@SerializedName("data")
                                     val data: ProductByCategoryData,
                                     @SerializedName("success")
                                     val success: Boolean = false,
                                     @SerializedName("messageId")
                                     val messageId: Int = 0,
                                     @SerializedName("message")
                                     val message: String = "")


data class ProductByCategoryData(@SerializedName("product_list")
                val productList: ArrayList<ProductListItem>?)


