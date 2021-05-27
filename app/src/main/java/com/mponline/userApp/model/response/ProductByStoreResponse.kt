package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class ProdByStoreData(@SerializedName("image")
                    val image: String = "",
                    @SerializedName("short_description")
                    val shortDescription: String = "",
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("id")
                    val id: Int = 0)


data class ProductByStoreResponse(@SerializedName("data")
                                  val data: ArrayList<ProductListItem>?,
                                  @SerializedName("messageId")
                                  val messageId: Int = 0,
                                  @SerializedName("message")
                                  val message: String = "",
                                  @SerializedName("status")
                                  val status: Boolean = false)


