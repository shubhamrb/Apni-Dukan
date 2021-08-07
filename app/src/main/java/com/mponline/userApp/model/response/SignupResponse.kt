package com.mponline.userApp.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(@SerializedName("user_id")
                val userId: Int = 0,
                @SerializedName("otp")
                val otp: Int = 0,
                @SerializedName("phone_number")
                val phoneNumber: String = ""):Parcelable


data class SignupResponse(@SerializedName("data")
                          val data: Data,
                          @SerializedName("messageId")
                          val messageId: Int = 0,
                          @SerializedName("message")
                          val message: String = "",
                          @SerializedName("status")
                          val status: Boolean = false)


