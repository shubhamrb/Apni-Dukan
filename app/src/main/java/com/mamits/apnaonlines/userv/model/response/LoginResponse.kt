package com.mamits.apnaonlines.userv.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class LoginResponse(
    @SerializedName("data")
    val data: LoginData?,
    @SerializedName("messageId")
    val messageId: Int? = 0,
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("status")
    val status: Boolean = false
)

@Parcelize
data class UserRes(
    @SerializedName("IsVerify")
    val isVerify: Int = 0,
    @SerializedName("Phone")
    val phone: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("IsActive")
    val isActive: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("id")
    val id: String = ""
) : Parcelable

@Parcelize
data class LoginData(
    @SerializedName("user")
    val user: UserRes,
    @SerializedName("store")
    val store: StorelistItem,
    @SerializedName("token")
    val token: String = ""
) : Parcelable


