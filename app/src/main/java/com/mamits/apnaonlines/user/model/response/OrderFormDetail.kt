package com.mamits.apnaonlines.user.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class OrderFormDetail(@SerializedName("order_detail")
                           val orderDetail: List<OrderDetailItem>?)

@Parcelize
data class OrderDetailItem(@SerializedName("name")
                           val name: String? = "",
                           @SerializedName("filedata")
                           val filedata: FileData? = FileData(),
                           @SerializedName("value")
                           val value: String? = ""):Parcelable{
}


@Parcelize
data class FileData(
    @SerializedName("type")
    val type: String? = "",
    @SerializedName("url")
    val url: String? = ""
):Parcelable


