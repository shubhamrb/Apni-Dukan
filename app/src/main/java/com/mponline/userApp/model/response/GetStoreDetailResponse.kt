package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class StoreDetailDataItem(@SerializedName("category")
                    val category: ArrayList<CategorylistItem>,
                    @SerializedName("openingtime")
                    val openingtime: String = "",
                    @SerializedName("distance")
                    val distance: String = "",
                     @SerializedName("is_available")
                    val is_available:String = "0",
                    @SerializedName("ratting")
                    val ratting:String = "",
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
                                  val data: List<StoreDetailDataItem>?,
                                  @SerializedName("status")
                                  val status: Boolean = false,
                                  @SerializedName("messageId")
                                  val messageId: Int = 0,
                                  @SerializedName("message")
                                  val message: String = "")


