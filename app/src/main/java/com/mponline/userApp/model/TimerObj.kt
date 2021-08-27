package com.mponline.userApp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimerObj(
    var hour:String,
    var min:String,
    var sec:String,
    var totalMillis:Long
):Parcelable {
}