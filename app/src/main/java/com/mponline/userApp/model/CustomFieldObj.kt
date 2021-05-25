package com.mponline.userApp.model

data class CustomFieldObj(
    var fieldType:String? = "",
    var hintName:String? = "",
    var max:String? = "",
    var min:String? = "",
    var isRequired:String = "",
    var value:String? = "",
    var name:String? = "",
    var ext:String? = "",
    var ansValue:String? = ""
)