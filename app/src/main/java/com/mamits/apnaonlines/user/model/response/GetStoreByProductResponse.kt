package com.mamits.apnaonlines.user.model.response


import com.google.gson.annotations.SerializedName

data class GetStoreByProductResponse(
    @SerializedName("data")
    val data: StoreByProductData,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int = 0,
    @SerializedName("message")
    val message: String = ""
)


data class StoreByProductData(
    @SerializedName("stores")
    val stores: ArrayList<StorelistItem>?,
    @SerializedName("next")
    val next: Boolean = false
)


//data class StoresItem(@SerializedName("image")
//                      val image: String = "",
//                      @SerializedName("distance")
//                      val distance: Int = 0,
//                      @SerializedName("city")
//                      val city: String = "",
//                      @SerializedName("ratting")
//                      val ratting: Int = 0,
//                      @SerializedName("IsAvailable")
//                      val isAvailable: Int = 0,
//                      @SerializedName("name")
//                      val name: String = "",
//                      @SerializedName("id")
//                      val id: Int = 0)


