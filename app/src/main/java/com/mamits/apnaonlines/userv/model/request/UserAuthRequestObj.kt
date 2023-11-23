package com.mamits.apnaonlines.userv.model.request


import com.google.gson.annotations.SerializedName

data class UserAuthRequestObj(
    @SerializedName("api_key")
    val apiKey: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("mobile")
    val mobile: String = "",
    /*@SerializedName("pin")
    val pin: String = "",*/
    @SerializedName("device_type")
    val device_type: String = "",
    @SerializedName("device_token")
    val device_token: String = "",
    @SerializedName("otp")
    val otp: String = "",
    @SerializedName("vendor_code")
    val vendor_code: String = ""
)


