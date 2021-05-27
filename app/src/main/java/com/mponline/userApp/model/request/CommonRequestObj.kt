package com.mponline.userApp.model.request


import com.google.gson.annotations.SerializedName

data class CommonRequestObj(
    @SerializedName("api_key")
    val apiKey: String = "",
     @SerializedName("name")
    val name: String = "",
     @SerializedName("email")
    val email: String = "",
     @SerializedName("mobile")
    val mobile: String = "",
    @SerializedName("search")
    val search: String = "",
    @SerializedName("oldpassword")
    val oldpassword: String = "",
    @SerializedName("newpassword")
    val newpassword: String = "",
    @SerializedName("orderid")
    val orderid: String = "",
    @SerializedName("vendorid")
    val vendorid: String = "",
    @SerializedName("latitude")
    val latitude: String = "",
    @SerializedName("longitude")
    val longitude: String = "",
    @SerializedName("category_id")
    val category_id: String = "",
    @SerializedName("category")
    val category: String = "",
    @SerializedName("store_id")
    val store_id: String = "",
    @SerializedName("subcategory_id")
    val subcategory_id: String = "",
    @SerializedName("storeid")
    val storeid: String = "",
    @SerializedName("rating")
    val rating: String = "",
    @SerializedName("product_id")
    val product_id: String = "",
    var headerInfo: HeaderInfo? = null
)

data class HeaderInfo(
    var Authorization: String = ""
)


