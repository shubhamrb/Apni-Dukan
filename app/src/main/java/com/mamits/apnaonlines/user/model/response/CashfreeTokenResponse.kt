package com.mamits.apnaonlines.user.model.response


import com.google.gson.annotations.SerializedName

data class CashfreeTokenResponse(@SerializedName("data")
                                 val data: String = "",
                                 @SerializedName("messageId")
                                 val messageId: Int = 0,
                                 @SerializedName("message")
                                 val message: String = "",
                                 @SerializedName("status")
                                 val status: Boolean = false)


