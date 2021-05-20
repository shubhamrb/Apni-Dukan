package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class ApplyCouponResponse(@SerializedName("data")
                               val data: ApplyCouponData,
                               @SerializedName("success")
                               val success: Boolean = false,
                               @SerializedName("messageId")
                               val messageId: Int = 0,
                               @SerializedName("message")
                               val message: String = "")


data class ApplyCouponData(@SerializedName("discountamount")
                           val discountamount: String = "",
                           @SerializedName("finalamountpay")
                           val finalamountpay: Int = 0,
                           @SerializedName("type")
                           val type: String = "")


