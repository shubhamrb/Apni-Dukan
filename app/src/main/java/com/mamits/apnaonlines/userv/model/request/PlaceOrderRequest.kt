package com.mamits.apnaonlines.userv.model.request


import com.google.gson.annotations.SerializedName

data class PlaceOrderRequest(
    @SerializedName("store_id")
    val storeId: String = "",
    @SerializedName("form_data")
    val formData: List<FormDataItem>?,
    @SerializedName("price")
    val price: String = "",
    @SerializedName("product_id")
    val productId: String = "",
    @SerializedName("type")
    val type: String = ""
)


data class FormDataItem(
    @SerializedName("isRequired")
    val isRequired: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("ext")
    var ext: String = "",
    @SerializedName("field_type")
    val fieldType: String = "",
    @SerializedName("ansValue")
    val ansValue: String = "",
    @SerializedName("doc_type")
    val doc_type: String = ""
)


