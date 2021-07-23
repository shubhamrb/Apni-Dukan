package com.mponline.userApp.model.request


import com.google.gson.annotations.SerializedName

data class SavePaymentRequest(@SerializedName("txStatus")
                              var txStatus: String? = "",
                              @SerializedName("paymentMethod")
                              var paymentMethod: String? = "",
                              @SerializedName("orderAmount")
                              var orderAmount: String? = "",
                              @SerializedName("orderId")
                              var orderId: String? = "",
                              @SerializedName("paymentMode")
                              var paymentMode: String? = "",
                              @SerializedName("txTime")
                              var txTime: String? = "",
                              @SerializedName("txMsg")
                              var txMsg: String? = "",
                              @SerializedName("referenceId")
                              var referenceId: String? = "")


