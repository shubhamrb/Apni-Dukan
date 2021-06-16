package com.mponline.userApp.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FilterDataSelectedObj(
    var mlocation:String? = "",
    var mprice:String? = "",
    var mrating:String? = ""
):Parcelable {
}