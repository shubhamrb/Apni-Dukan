package com.mamits.apnaonlines.user.model.request


import com.google.gson.annotations.SerializedName

data class CashfreeObj(
    @SerializedName("orderId")
    val orderId: String = "",
     @SerializedName("orderAmount")
    val orderAmount: String = ""
)
