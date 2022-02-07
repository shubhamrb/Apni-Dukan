package com.mamits.apnaonlines.user.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoreDetailDataItem(
    @SerializedName("category")
    val category: ArrayList<CategorylistItem>,
    @SerializedName("banner_image")
    val banner_image: ArrayList<String>,
    @SerializedName("openingtime")
    val openingtime: String = "",
    @SerializedName("distance")
    val distance: String = "",
    @SerializedName("openstatus")
    val openstatus: String = "",
    @SerializedName("is_available")
    val is_available: String = "0",
    @SerializedName("ratting")
    val ratting: String = "",
    @SerializedName("latitude")
    val latitude: String = "",
    @SerializedName("closingtime")
    val closingtime: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("storelogo")
    val storelogo: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("totalrating")
    val totalrating: String? = "",
    @SerializedName("rating")
    val rating: RatingDetail?,
    @SerializedName("longitude")
    val longitude: String = ""
) : Parcelable {

    @Parcelize
    data class RatingDetail(
        @SerializedName("1")
        var one: String?,
        @SerializedName("2")
        var two: String?,
        @SerializedName("3")
        var three: String?,
        @SerializedName("4")
        var four: String?,
        @SerializedName("5")
        var five: String?
    ):Parcelable
}

@Parcelize
data class GetStoreDetailResponse(
    @SerializedName("data")
    val data: List<StoreDetailDataItem>?,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int = 0,
    @SerializedName("message")
    val message: String = ""
) : Parcelable


