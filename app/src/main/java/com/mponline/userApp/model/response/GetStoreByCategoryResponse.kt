package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class StoreByCategoryData(@SerializedName("name")
                val name: String = "",
                @SerializedName("id")
                val id: Int = 0,
                @SerializedName("banner_image")
                val bannerImage: String = "",
                @SerializedName("stores")
                val stores: List<StorelistItem>?)


data class GetStoreByCategoryResponse(@SerializedName("data")
                                      val data: StoreByCategoryData,
                                      @SerializedName("success")
                                      val success: Boolean = false,
                                      @SerializedName("messageId")
                                      val messageId: Int = 0,
                                      @SerializedName("message")
                                      val message: String = "")


