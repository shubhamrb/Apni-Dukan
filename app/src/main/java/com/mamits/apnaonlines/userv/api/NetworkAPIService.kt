package com.mamits.apnaonlines.userv.api

import com.mamits.apnaonlines.userv.model.UserListResponse
import com.mamits.apnaonlines.userv.model.request.*
import com.mamits.apnaonlines.userv.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface NetworkAPIService {

    @GET(ApiName.USER_LIST)
    suspend fun fetchUsers(@Query("results") results: Int): Response<UserListResponse>

    //MpOnline
    @POST(ApiName.GET_HOME)
    suspend fun getHomeData(@Body commonRequestObj: CommonRequestObj): Response<GetHomeDataResponse>

    @POST(ApiName.STORE_AROUND)
    suspend fun getStoreAround(@Body commonRequestObj: CommonRequestObj): Response<GetStoreAroundResponse>

    @POST(ApiName.GET_CATEGORIES)
    suspend fun getCategories(@Body commonRequestObj: CommonRequestObj): Response<GetCategoriesResponse>

    @POST(ApiName.GET_SUBCATEGORIES)
    suspend fun getSubCategories(@Body commonRequestObj: CommonRequestObj): Response<GetCategoriesResponse>

    @POST(ApiName.GET_STORE_BY_CATEGORY)
    suspend fun getStoreByCategory(@Body commonRequestObj: CommonRequestObj): Response<GetStoreByCategoryResponse>

    @POST(ApiName.GET_DOCUMENTS)
    suspend fun getDocuments(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<GetDocumentResponse>

    @POST(ApiName.GET_CATEGORY_BY_STORE)
    suspend fun getCategoryByStore(@Body commonRequestObj: CommonRequestObj): Response<GetCategoryByStoreResponse>

    @POST(ApiName.GET_PRODUCT_BY_CATEGORY)
    suspend fun getProductByCategory(@Body commonRequestObj: CommonRequestObj): Response<GetProductByCategoryResponse>

    @POST(ApiName.GET_STORE_BY_PRODUCT)
    suspend fun getStoreByProduct(@Body commonRequestObj: CommonRequestObj): Response<GetStoreByProductResponse>

    @POST(ApiName.GET_STORE_DETAIL)
    suspend fun getStoreDetail(@Body commonRequestObj: CommonRequestObj): Response<GetStoreDetailResponse>

    @POST(ApiName.GET_PRODUCT_DETAIL)
    suspend fun getProductDetail(@Body commonRequestObj: CommonRequestObj): Response<GetProductDetailResponse>

    //Login
    @POST(ApiName.SIGNUP)
    suspend fun signup(@Body userAuthRequestObj: UserAuthRequestObj): Response<SignupResponse>

    @POST(ApiName.VERIFY_VENDOR_CODE)
    suspend fun verifyVendorCode(@Body userAuthRequestObj: UserAuthRequestObj): Response<LoginOtpResponse>

    @POST(ApiName.SIGNIN)
    suspend fun login(@Body userAuthRequestObj: UserAuthRequestObj): Response<LoginOtpResponse>

    @POST(ApiName.SEND_OTP)
    suspend fun sendOtp(@Body userAuthRequestObj: UserAuthRequestObj): Response<CommonResponse>

    @POST(ApiName.VERIFY_MOBILE_OTP)
    suspend fun verifyMobile(@Body userAuthRequestObj: UserAuthRequestObj): Response<LoginResponse>

    @POST(ApiName.VERIFY_LOGIN_OTP)
    suspend fun verifyLoginOtp(@Body userAuthRequestObj: UserAuthRequestObj): Response<LoginResponse>

    //Listing
    @POST(ApiName.GET_ORDER_HISTORY)
    suspend fun getOrderHistory(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<OrderHistoryResponse>

    @POST(ApiName.GET_LATEST_ORDER)
    suspend fun getNewOrders(
        @Header("Authorization") token: String
    ): Response<OrderHistoryResponse>

    @POST(ApiName.NOTIFICATION_LIST)
    suspend fun getNotificationList(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<NotificationListResponse>

    @POST(ApiName.GET_CHAT_LIST)
    suspend fun getChatList(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<GetChatListResponse>

    @POST(ApiName.GET_UPDATED_CHAT_LIST)
    suspend fun getUpdatedChatList(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<GetChatListResponse>

    @Multipart
    @POST(ApiName.SAVE_CHAT)
    suspend fun saveChat(
        @Header("Authorization") token: String, @Part file: MultipartBody.Part?,
        @Part(
            "orderid"
        ) orderid: RequestBody,
        @Part(
            "vendorid"
        ) vendorid: RequestBody,
        @Part(
            "message"
        ) message: RequestBody,
        @Part(
            "docName"
        ) docName: RequestBody,
        @Part(
            "doc_type"
        ) doc_type: RequestBody
    ): Response<GetChatListResponse>

    @POST(ApiName.APPLY_COUPON)
    suspend fun applyCoupon(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<ApplyCouponResponse>

    @POST(ApiName.GET_COUPON_LIST)
    suspend fun getCouponList(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<GetCouponListResponse>

    @POST(ApiName.GET_COUPON_LIST)
    suspend fun getOfferCouponList(
        @Header("Authorization") token: String
    ): Response<GetCouponListResponse>

    @POST(ApiName.PLACE_ORDER)
    suspend fun placeOrder(
        @Header("Authorization") token: String,
        @Body placeOrderRequest: PlaceOrderRequest
    ): Response<CommonResponse>

    @Multipart
    @POST(ApiName.UPLOAD_FILE)
    suspend fun callUploadDocuments(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part?,
        @Part("name") name: RequestBody,
        @Part("docName") docName: RequestBody,
        @Part("doc_type") doc_type: RequestBody
    ): Response<UploadFileResponse>

    @POST(ApiName.SAVE_RATINGS)
    suspend fun saveRating(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<CommonResponse>

    @POST(ApiName.CHANGE_PWD)
    suspend fun changePwd(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<CommonResponse>

    @POST(ApiName.UPDATE_PROFILE)
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<CommonResponse>

    @POST(ApiName.DELETE_DOCUMENT)
    suspend fun deleteDocument(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<CommonResponse>

    @Multipart
    @POST(ApiName.ADD_DOCUMENT)
    suspend fun addDocument(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody
    ): Response<CommonResponse>

    @POST(ApiName.GET_PRODUCT_BY_STORE)
    suspend fun getProdByStore(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<ProductByStoreResponse>

    @POST(ApiName.HOME_SEARCH)
    suspend fun homeSearch(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<HomeSearchResponse>

    @POST(ApiName.CASHFREE_TOKEN)
    suspend fun cashfreeToken(
        @Header("Authorization") token: String,
        @Body mCashfreeObj: CashfreeObj
    ): Response<CashfreeTokenResponse>

    @POST(ApiName.SAVE_PAYMENT)
    suspend fun savePayment(
        @Header("Authorization") token: String,
        @Body savePaymentRequest: SavePaymentRequest
    ): Response<CommonResponse>

    @POST(ApiName.GET_INVOICE)
    suspend fun getInvoice(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<GetInvoiceResponse>

    @POST(ApiName.FORGOT_PWD)
    suspend fun forgotPwd(
        @Body commonRequestObj: UserAuthRequestObj
    ): Response<CommonResponse>

    @POST(ApiName.REMOVE_COUPON)
    suspend fun removeCoupon(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<CommonResponse>

    @GET(ApiName.PAYTM_CHECKSUM)
    suspend fun getPaytmChecksum(
        @Query("mobile") mobile: String,
        @Query("email") email: String,
        @Query("userid") userid: String,
        @Query("orderid") orderid: String,
        @Query("amount") amount: String
    ): Response<PaytmChecksumResponse>

    @POST(ApiName.UPDATE_PWD)
    suspend fun updatePwd(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<CommonResponse>

    @POST(ApiName.GET_SETTING)
    suspend fun getSetting(
        @Header("Authorization") token: String,
        @Body commonRequestObj: CommonRequestObj
    ): Response<GetSettingResponse>

    @POST(ApiName.HELP_SUPPORT_WEBVIEW)
    suspend fun getHelpSupportWebview(
        @Header("Authorization") token: String
    ): Response<String>

    @POST(ApiName.ENQUIRY_WEBVIEW)
    suspend fun getEnquiryWebview(
        @Header("Authorization") token: String
    ): String


}