package com.mamits.apnaonlines.user.model

data class PaytmModel(
     val mId: String = "",
             val orderId: String = "",
             val custId: String = "",
             val channelId: String = "",
             val txtAmount: String = "",
             val website: String = "",
             val callbackURL: String = "",
             val industryTypeId: String = "",
             val checkSumHash: String = ""
) {

    
}