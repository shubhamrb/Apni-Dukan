package com.mponline.userApp.model

import android.os.Parcelable
import com.mponline.userApp.model.response.GetProductDetailResponse
import com.mponline.userApp.model.response.ProductListItem
import com.mponline.userApp.model.response.StoreDetailDataItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PrePlaceOrderPojo(
    var storeDetailDataItem: StoreDetailDataItem,
    var mGetProductDetailResponse: GetProductDetailResponse
):Parcelable {
}