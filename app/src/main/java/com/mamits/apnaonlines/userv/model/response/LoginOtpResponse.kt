package com.mamits.apnaonlines.userv.model.response


import com.google.gson.annotations.SerializedName

data class LoginOtpResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("referred_by")
    val referred_by: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int? = 0,
    @SerializedName("message")
    val message: String? = ""
)


