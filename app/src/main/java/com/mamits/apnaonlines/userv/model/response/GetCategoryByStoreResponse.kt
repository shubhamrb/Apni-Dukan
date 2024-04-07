package com.mamits.apnaonlines.userv.model.response


import com.google.gson.annotations.SerializedName

//@Parcelize
//data class CategoriesItem(@SerializedName("image")
//                          val image: String = "",
//                          @SerializedName("name")
//                          val name: String = "",
//                          @SerializedName("id")
//                          val id: String = ""):Parcelable


data class CategoryByStoreData(
    @SerializedName("image")
    val image: String = "",
    @SerializedName("distance")
    val distance: Int = 0,
    @SerializedName("city")
    val city: String = "",
    @SerializedName("ratting")
    val ratting: Int = 0,
    @SerializedName("IsAvailable")
    val isAvailable: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("categories")
    val categories: List<StoreDetailDataItem>?
)


data class GetCategoryByStoreResponse(
    @SerializedName("data")
    val data: CategoryByStoreData,
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int = 0,
    @SerializedName("message")
    val message: String = ""
)


