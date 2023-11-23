package com.mamits.apnaonlines.userv.model.response


import com.google.gson.annotations.SerializedName

data class PaytmChecksumResponse(@SerializedName("data")
                                 val data: String = "",
                                 @SerializedName("messageId")
                                 val messageId: Int = 0,
                                 @SerializedName("message")
                                 val message: String = "",
                                 @SerializedName("status")
                                 val status: Boolean = false)


