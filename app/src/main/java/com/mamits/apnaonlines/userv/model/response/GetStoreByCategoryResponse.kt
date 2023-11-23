package com.mamits.apnaonlines.userv.model.response


import com.google.gson.annotations.SerializedName

data class StoreByCategoryData(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("banner_image")
    val bannerImage: String = "",
    @SerializedName("stores")
    val stores: ArrayList<StorelistItem>?,
    @SerializedName("next")
    val next: Boolean = false
)


data class GetStoreByCategoryResponse(
    @SerializedName("data")
    val data: StoreByCategoryData,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int = 0,
    @SerializedName("message")
    val message: String = ""
)


