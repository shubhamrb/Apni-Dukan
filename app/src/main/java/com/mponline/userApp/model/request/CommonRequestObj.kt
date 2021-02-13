package com.mponline.userApp.model.request


import com.google.gson.annotations.SerializedName

data class CommonRequestObj(
    @SerializedName("api_key")
    val apiKey: String = "",
    @SerializedName("latitude")
    val latitude: String = "",
    @SerializedName("longitude")
    val longitude: String = "",
    @SerializedName("category_id")
    val category_id: String = "",
    @SerializedName("subcategory_id")
    val subcategory_id: String = "",
    @SerializedName("product_id")
    val product_id: String = ""
)


