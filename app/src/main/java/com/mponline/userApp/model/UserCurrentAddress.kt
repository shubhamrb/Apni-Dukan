package com.mponline.userApp.model

data class UserCurrentAddress(
    var address:String? = "",
    var city:String? = "",
    var state:String? = "",
    var country:String?= "",
    var postalCode:String?= "",
    var knownName:String? = ""
) {
}