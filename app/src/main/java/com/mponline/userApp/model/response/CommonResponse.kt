package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String = ""
)


