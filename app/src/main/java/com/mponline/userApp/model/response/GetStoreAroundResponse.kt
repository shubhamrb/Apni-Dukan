package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class GetStoreAroundResponse(@SerializedName("data")
                                  val data: ArrayList<StorelistItem>?,
                                  @SerializedName("status")
                                  val status: Boolean = false,
                                  @SerializedName("messageId")
                                  val messageId: Int = 0,
                                  @SerializedName("message")
                                  val message: String = "")


//data class StoreDataItem(@SerializedName("image")
//                    val image: String = "",
//                    @SerializedName("distance")
//                    val distance: Int = 0,
//                    @SerializedName("latitude")
//                    val latitude: String = "",
//                    @SerializedName("IsAvailable")
//                    val isAvailable: Int = 0,
//                    @SerializedName("name")
//                    val name: String = "",
//                    @SerializedName("id")
//                    val id: Int = 0,
//                    @SerializedName("longitude")
//                    val longitude: String = "")


