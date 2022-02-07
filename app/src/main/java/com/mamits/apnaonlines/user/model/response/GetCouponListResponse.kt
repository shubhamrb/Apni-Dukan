package com.mamits.apnaonlines.user.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetCouponListResponse(
    @SerializedName("data")
    val data: ArrayList<DataItem>?,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("next")
    val next: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int = 0,
    @SerializedName("message")
    val message: String = ""
) : Parcelable

@Parcelize
data class DataItem(
    @SerializedName("discount_amount")
    val discount_amount: String = "",
    @SerializedName("store_id")
    val store_id: String? = "",
    @SerializedName("discount_type")
    val discount_type: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("from_date")
    val from_date: String = "",
    @SerializedName("to_date")
    val to_date: String = "",
    @SerializedName("coupon")
    val coupon: String = "",
    @SerializedName("id")
    val id: String
) : Parcelable {
    

}


