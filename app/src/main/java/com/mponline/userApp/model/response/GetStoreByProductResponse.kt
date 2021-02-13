package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class GetStoreByProductResponse(@SerializedName("data")
                                     val data: StoreByProductData,
                                     @SerializedName("success")
                                     val success: Boolean = false,
                                     @SerializedName("messageId")
                                     val messageId: Int = 0,
                                     @SerializedName("message")
                                     val message: String = "")


data class StoreByProductData(@SerializedName("stores")
                val stores: List<StoresItem>?)


data class StoresItem(@SerializedName("image")
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
                      val id: Int = 0)


