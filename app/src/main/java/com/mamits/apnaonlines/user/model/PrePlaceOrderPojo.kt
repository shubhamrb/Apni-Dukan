package com.mamits.apnaonlines.user.model

import android.os.Parcelable
import com.mamits.apnaonlines.user.model.response.GetProductDetailResponse
import com.mamits.apnaonlines.user.model.response.StoreDetailDataItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrePlaceOrderPojo(
    var storeDetailDataItem: StoreDetailDataItem,
    var mGetProductDetailResponse: GetProductDetailResponse
):Parcelable {
}