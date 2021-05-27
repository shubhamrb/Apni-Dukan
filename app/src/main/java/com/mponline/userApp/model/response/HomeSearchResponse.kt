package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class HomeSearchData(@SerializedName("name")
                    val name: String = "",
                    @SerializedName("type")
                    val type: String = "",
                    @SerializedName("value")
                    val value: String = "")


data class HomeSearchResponse(@SerializedName("data")
                              val data: ArrayList<HomeSearchData>?,
                              @SerializedName("messageId")
                              val messageId: Int = 0,
                              @SerializedName("message")
                              val message: String = "",
                              @SerializedName("status")
                              val status: Boolean = false)


