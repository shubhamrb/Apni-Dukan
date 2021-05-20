package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class GetCouponListResponse(@SerializedName("data")
                                 val data: List<DataItem>?,
                                 @SerializedName("success")
                                 val success: Boolean = false,
                                 @SerializedName("messageId")
                                 val messageId: Int = 0,
                                 @SerializedName("message")
                                 val message: String = "")


data class DataItem(@SerializedName("coupon")
                    val coupon: String = "",
                    @SerializedName("id")
                    val id: Int = 0)


