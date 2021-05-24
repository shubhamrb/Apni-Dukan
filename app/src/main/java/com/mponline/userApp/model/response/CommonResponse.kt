package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = ""
)


