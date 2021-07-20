package com.mponline.userApp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomFieldObj(
    var fieldType:String? = "",
    var hintName:String? = "",
    var max:String? = "",
    var min:String? = "",
    var isRequired:String = "",
//    var value:String? = "",
    var name:String? = "",
    var ext:String? = "",
    var ansValue:String? = "",
    var id:String? = "",
    var isVisible:Boolean = true,
    var visibilityControlfield:ArrayList<String> = ArrayList(),
    var value:ArrayList<ValueObj> = ArrayList()
):Parcelable{
    @Parcelize
    data class ValueObj(
        var value:String? = "",
        var condition:String? = "",
        var fieldType:String? = "",
        var hidefield:ArrayList<String> = ArrayList()
    ):Parcelable
}