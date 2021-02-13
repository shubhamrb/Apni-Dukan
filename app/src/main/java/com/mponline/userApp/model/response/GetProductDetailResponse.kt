package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class ProductDetailDataItem(@SerializedName("store_id")
                    val storeId: Int = 0,
                    @SerializedName("short_description")
                    val shortDescription: String = "",
                    @SerializedName("image")
                    val image: String = "",
                    @SerializedName("form")
                    val form: String = "",
                    @SerializedName("price")
                    val price: String = "",
                    @SerializedName("product_id")
                    val productId: Int = 0,
                    @SerializedName("discount")
                    val discount: Int = 0,
                    @SerializedName("description")
                    val description: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("discount_type")
                    val discountType: Int = 0)


data class GetProductDetailResponse(@SerializedName("data")
                                    val data: List<ProductDetailDataItem>?,
                                    @SerializedName("success")
                                    val success: Boolean = false,
                                    @SerializedName("messageId")
                                    val messageId: Int = 0,
                                    @SerializedName("message")
                                    val message: String = "")


