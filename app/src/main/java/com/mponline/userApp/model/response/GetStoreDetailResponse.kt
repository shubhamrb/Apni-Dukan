package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class DataItem(@SerializedName("openingtime")
                    val openingtime: String = "",
                    @SerializedName("distance")
                    val distance: Int = 0,
                    @SerializedName("ratting")
                    val ratting: Int = 0,
                    @SerializedName("latitude")
                    val latitude: String = "",
                    @SerializedName("closingtime")
                    val closingtime: String = "",
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("storelogo")
                    val storelogo: String = "",
                    @SerializedName("description")
                    val description: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("longitude")
                    val longitude: String = "")


data class GetStoreDetailResponse(@SerializedName("data")
                                  val data: List<DataItem>?,
                                  @SerializedName("success")
                                  val success: Boolean = false,
                                  @SerializedName("messageId")
                                  val messageId: Int = 0,
                                  @SerializedName("message")
                                  val message: String = "")


