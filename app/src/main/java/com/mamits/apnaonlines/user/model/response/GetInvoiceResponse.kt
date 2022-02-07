package com.mamits.apnaonlines.user.model.response


import com.google.gson.annotations.SerializedName

data class InvoiceProducts(@SerializedName("short_description")
                    val shortDescription: String = "",
                           @SerializedName("IsVerify")
                    val isVerify: Int = 0,
                           @SerializedName("IsActive")
                    val isActive: Int = 0,
                           @SerializedName("discount")
                    val discount: Int = 0,
                           @SerializedName("description")
                    val description: String = "",
                           @SerializedName("created_at")
                    val createdAt: String = "",
                           @SerializedName("internal_price")
                    val internalPrice:String = "",
                           @SerializedName("variation")
                    val variation: String = "",
                           @SerializedName("category_list")
                    val categoryList: CategoryList,
                           @SerializedName("category_id")
                    val categoryId: String = "",
                           @SerializedName("updated_at")
                    val updatedAt: String = "",
                           @SerializedName("sub_category_id")
                    val subCategoryId: Int = 0,
                           @SerializedName("price")
                    val price: String = "",
                           @SerializedName("meta_data")
                    val metaData: String = "",
                           @SerializedName("id")
                    val id: Int = 0,
                           @SerializedName("image")
                    val image: String = "",
                           @SerializedName("form_id")
                    val formId: Int = 0,
                           @SerializedName("discount_type")
                    val discountType: Int = 0,
                           @SerializedName("created_by")
                    val createdBy: Int = 0,
                           @SerializedName("deleted_at")
                    val deletedAt: String = "",
                           @SerializedName("meta_description")
                    val metaDescription: String = "",
                           @SerializedName("product_type")
                    val productType: Int = 0,
                           @SerializedName("IsHomePage")
                    val isHomePage: Int = 0,
                           @SerializedName("sub_category_list")
                    val subCategoryList: SubCategoryList,
                           @SerializedName("name")
                    val name: String = "",
                           @SerializedName("updated_by")
                    val updatedBy:String = "")


data class UserName(@SerializedName("CreatedBy")
                    val createdBy: Int = 0,
                    @SerializedName("wallet")
                    val wallet: String = "",
                    @SerializedName("IsVerify")
                    val isVerify: Int = 0,
                    @SerializedName("IsActive")
                    val isActive: Int = 0,
                    @SerializedName("created_at")
                    val createdAt: String = "",
                    @SerializedName("otp")
                    val otp: String = "",
                    @SerializedName("deleted_at")
                    val deletedAt: String = "",
                    @SerializedName("UpdatedBy")
                    val updatedBy: String = "",
                    @SerializedName("user_type")
                    val userType: Int = 0,
                    @SerializedName("user_code")
                    val userCode: String = "",
                    @SerializedName("updated_at")
                    val updatedAt: String = "",
                    @SerializedName("Phone")
                    val phone: String = "",
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("state_id")
                    val stateId: String = "",
                    @SerializedName("email")
                    val email: String = "",
                    @SerializedName("city_id")
                    val cityId: String = "")


data class CategoryList(@SerializedName("image")
                        val image: String = "",
                        @SerializedName("sell_count")
                        val sellCount: Int = 0,
                        @SerializedName("IsActive")
                        val isActive: Int = 0,
                        @SerializedName("created_at")
                        val createdAt: String = "",
                        @SerializedName("created_by")
                        val createdBy: Int = 0,
                        @SerializedName("deleted_at")
                        val deletedAt: String = "",
                        @SerializedName("category_order")
                        val categoryOrder: Int = 0,
                        @SerializedName("IsHomePage")
                        val isHomePage: Int = 0,
                        @SerializedName("updated_at")
                        val updatedAt: String = "",
                        @SerializedName("parent_id")
                        val parentId: Int = 0,
                        @SerializedName("name")
                        val name: String = "",
                        @SerializedName("updated_by")
                        val updatedBy: String = "",
                        @SerializedName("id")
                        val id: Int = 0,
                        @SerializedName("banner_image")
                        val bannerImage: String = "")


data class SubCategoryList(@SerializedName("image")
                           val image: String = "",
                           @SerializedName("sell_count")
                           val sellCount: Int = 0,
                           @SerializedName("IsActive")
                           val isActive: Int = 0,
                           @SerializedName("created_at")
                           val createdAt: String = "",
                           @SerializedName("created_by")
                           val createdBy: Int = 0,
                           @SerializedName("deleted_at")
                           val deletedAt: String = "",
                           @SerializedName("category_order")
                           val categoryOrder: Int = 0,
                           @SerializedName("IsHomePage")
                           val isHomePage: Int = 0,
                           @SerializedName("updated_at")
                           val updatedAt: String = "",
                           @SerializedName("parent_id")
                           val parentId: Int = 0,
                           @SerializedName("name")
                           val name: String = "",
                           @SerializedName("updated_by")
                           val updatedBy: String = "",
                           @SerializedName("id")
                           val id: Int = 0,
                           @SerializedName("banner_image")
                           val bannerImage: String = "")


data class GetInvoiceResponse(@SerializedName("message")
                              val message: String = "",
                              @SerializedName("html")
                              var html: String = "",
                              @SerializedName("status")
                              val status: Boolean = false)


data class InvoiceData(@SerializedName("offer_amount")
                val offerAmount: Int = 0,
                       @SerializedName("time_type")
                val timeType: String = "",
                       @SerializedName("order_completion_time")
                val orderCompletionTime: Int = 0,
                       @SerializedName("user_name")
                val userName: UserName,
                       @SerializedName("storedetail")
                val storedetail: StoredetailInvoice,
                       @SerializedName("description")
                val description: String = "",
                       @SerializedName("created_at")
                val createdAt: String = "",
                       @SerializedName("type")
                val type: String = "",
                       @SerializedName("order_detail")
                val orderDetail: String = "",
                       @SerializedName("accepted_at")
                val acceptedAt: String = "",
                       @SerializedName("payment_file")
                val paymentFile: String = "",
                       @SerializedName("products")
                val products: InvoiceProducts,
                       @SerializedName("admin_commission")
                val adminCommission: String = "",
                       @SerializedName("updated_at")
                val updatedAt: String = "",
//                @SerializedName("product_id")
//                val productId: Int = 0,
                       @SerializedName("order_amount")
                val orderAmount: String = "",
                       @SerializedName("orderdatetime")
                val orderdatetime: String = "",
                       @SerializedName("id")
                val id: Int = 0,
                       @SerializedName("commission_type")
                val commissionType: String = "",
                       @SerializedName("store_id")
                val storeId: Int = 0,
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
                val status: Int = 0)


data class StoredetailInvoice(@SerializedName("IsVerify")
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
                       val userId: Int = 0,
                       @SerializedName("ratting")
                       val ratting: Int = 0,
                       @SerializedName("IsAvailable")
                       val isAvailable: Int = 0,
                       @SerializedName("name")
                       val name: String = "",
                       @SerializedName("mobile_number")
                       val mobileNumber: String = "")


