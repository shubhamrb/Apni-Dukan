package com.mamits.apnaonlines.user.model.response


import com.google.gson.annotations.SerializedName

data class GetSettingResponse(@SerializedName("data")
                              val data: ArrayList<GetSettingDataItem>? = ArrayList(),
                              @SerializedName("messageId")
                              val messageId: Int = 0,
                              @SerializedName("message")
                              val message: String = "",
                              @SerializedName("status")
                              val status: Boolean = false)


data class GetSettingDataItem(@SerializedName("paytm_merchant_key")
                    val paytmMerchantKey: String = "",
                    @SerializedName("cashfree_mode")
                    val cashfreeMode: String = "",
                    @SerializedName("appid")
                    val appid: String = "",
                    @SerializedName("cashfree_secretkey")
                    val cashfreeSecretkey: String = "",
                    @SerializedName("paytm_merchant_website")
                    val paytmMerchantWebsite: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("paytm_mode")
                    val paytmMode: String = "",
                    @SerializedName("paytm_merchant_mid")
                    val paytmMerchantMid: String = "")


