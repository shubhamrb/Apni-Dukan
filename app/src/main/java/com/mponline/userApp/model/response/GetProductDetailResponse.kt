package com.mponline.userApp.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mponline.userApp.model.CustomFieldObj
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetProdDetailDataItem(@SerializedName("store_id")
                    val storeId: String = "",
                    @SerializedName("short_description")
                    val shortDescription: String = "",
                    @SerializedName("image")
                    val image: String = "",
                    @SerializedName("form")
                    val form: List<FormItem>?,
                    @SerializedName("price")
                    val price: String = "",
                    @SerializedName("product_id")
                    val productId: String = "",
                    @SerializedName("discount")
                    val discount: String = "",
                    @SerializedName("description")
                    val description: String = "",
                    @SerializedName("id")
                    val id: String = "",
                    @SerializedName("discount_type")
                    val discountType: String = ""):Parcelable

@Parcelize
data class FormItem(@SerializedName("isRequired")
                    val isRequired: String = "",
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("label")
                    val label: String = "",
                     @SerializedName("id")
                    val id: String = "",
                    @SerializedName("field_type")
                    val fieldType: String = "",
                    @SerializedName("value")
                    val value: ArrayList<CustomFieldObj.ValueObj> = ArrayList()
):Parcelable

@Parcelize
data class GetProductDetailResponse(@SerializedName("data")
                                    val data: ArrayList<GetProdDetailDataItem>? = ArrayList(),
                                    @SerializedName("messageId")
                                    val messageId: String = "",
                                    @SerializedName("message")
                                    val message: String = "",
                                    @SerializedName("status")
                                    val status: Boolean = false):Parcelable


