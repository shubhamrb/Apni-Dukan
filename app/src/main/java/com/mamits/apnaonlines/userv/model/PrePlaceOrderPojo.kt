package com.mamits.apnaonlines.userv.model

import android.os.Parcelable
import com.mamits.apnaonlines.userv.model.response.GetProductDetailResponse
import com.mamits.apnaonlines.userv.model.response.StoreDetailDataItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrePlaceOrderPojo(
    var storeDetailDataItem: StoreDetailDataItem,
    var mGetProductDetailResponse: GetProductDetailResponse
):Parcelable {
}