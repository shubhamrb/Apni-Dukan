package com.mamits.apnaonlines.user.model.response


import com.google.gson.annotations.SerializedName

data class LoginOtpResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int? = 0,
    @SerializedName("message")
    val message: String? = ""
)


