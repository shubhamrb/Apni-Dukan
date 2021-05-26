package com.mponline.userApp.model.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderHistoryResponse(
    @SerializedName("data")
    val data: List<OrderHistoryDataItem>?,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int = 0,
    @SerializedName("message")
    val message: String = ""
) : Parcelable

@Parcelize
data class Products(
    @SerializedName("image")
    val image: String = "",
    @SerializedName("short_description")
    val shortDescription: String = "",
    @SerializedName("IsVerify")
    val isVerify: Int = 0,
    @SerializedName("IsActive")
    val isActive: Int = 0,
    @SerializedName("form_id")
    val formId: String = "",
    @SerializedName("discount")
    val discount: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("discount_type")
    val discountType: String = "",
    @SerializedName("created_by")
    val createdBy: String = "",
    @SerializedName("deleted_at")
    val deletedAt: String = "",
    @SerializedName("internal_price")
    val internalPrice: String = "",
    @SerializedName("variation")
    val variation: String = "",
    @SerializedName("meta_description")
    val metaDescription: String = "",
    @SerializedName("product_type")
    val productType: Int = 0,
    @SerializedName("category_id")
    val categoryId: String = "",
    @SerializedName("IsHomePage")
    val isHomePage: Int = 0,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("sub_category_id")
    val subCategoryId: Int = 0,
    @SerializedName("price")
    val price: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("meta_data")
    val metaData: String = "",
    @SerializedName("updated_by")
    val updatedBy: String = "",
    @SerializedName("id")
    val id: Int = 0
) : Parcelable

@Parcelize
data class OrderHistoryDataItem(
    @SerializedName("offer_amount")
    val offerAmount: Int = 0,
    @SerializedName("time_type")
    val timeType: String = "",
    @SerializedName("order_completion_time")
    val orderCompletionTime: Int = 0,
    @SerializedName("storedetail")
    val storedetail: Storedetail,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("order_detail")
    val orderDetail: ArrayList<OrderDetailItem>?,
    @SerializedName("accepted_at")
    val acceptedAt: String = "",
    @SerializedName("payment_file")
    val paymentFile: String = "",
    @SerializedName("products")
    val products: Products,
    @SerializedName("admin_commission")
    val adminCommission: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("product_id")
    val productId: Int = 0,
    @SerializedName("order_amount")
    val orderAmount: String = "",
    @SerializedName("orderdatetime")
    val orderdatetime: String = "",
    @SerializedName("id")
    val id: String = "",
    @SerializedName("commission_type")
    val commissionType: String = "",
    @SerializedName("store_id")
    val storeId: String = "",
    @SerializedName("updatedBy")
    val updatedBy: String = "",
    @SerializedName("payment_status")
    val paymentStatus: Int = 0,
    @SerializedName("deleted_at")
    val deletedAt: String = "",
    @SerializedName("offer_id")
    val offerId: Int = 0,
    @SerializedName("completed_at")
    val completedAt: String = "",
    @SerializedName("payment_type")
    val paymentType: String = "",
    @SerializedName("rating_status")
    val ratingStatus: Int = 0,
    @SerializedName("myrating")
    var myrating: String = "",
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("createdBy")
    val createdBy: Int = 0,
    @SerializedName("order_completed_by")
    val orderCompletedBy: Int = 0,
    @SerializedName("payable_amount")
    val payableAmount: String = "",
    @SerializedName("order_id")
    val orderId: String = "",
    @SerializedName("status")
    val status: Int = 0
) : Parcelable

@Parcelize
data class Storedetail(
    @SerializedName("IsVerify")
    val isVerify: Int = 0,
    @SerializedName("city")
    val city: String = "",
    @SerializedName("qrcode")
    val qrcode: String = "",
    @SerializedName("latitude")
    val latitude: String = "",
    @SerializedName("closingtime")
    val closingtime: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("UpdatedBy")
    val updatedBy: String = "",
    @SerializedName("payment_accept_mode")
    val paymentAcceptMode: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("IsBlock")
    val isBlock: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("state_id")
    val stateId: Int = 0,
    @SerializedName("longitude")
    val longitude: String = "",
    @SerializedName("upi_number")
    val upiNumber: String = "",
    @SerializedName("store_id")
    val storeId: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("images")
    val images: String = "",
    @SerializedName("whatsapp_no")
    val whatsappNo: String = "",
    @SerializedName("address")
    val address: String = "",
    @SerializedName("CreatedBy")
    val createdBy: Int = 0,
    @SerializedName("storelogo")
    val storelogo: String = "",
    @SerializedName("deleted_at")
    val deletedAt: String = "",
    @SerializedName("zipcode")
    val zipcode: String = "",
    @SerializedName("openingtime")
    val openingtime: String = "",
    @SerializedName("IsHomePage")
    val isHomePage: Int = 0,
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("ratting")
    val ratting: Int = 0,
    @SerializedName("IsAvailable")
    val isAvailable: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("mobile_number")
    val mobileNumber: String = ""
) : Parcelable


