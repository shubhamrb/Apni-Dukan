package com.mamits.apnaonlines.userv.listener

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamits.apnaonlines.userv.model.ResultUserItem
import com.mamits.apnaonlines.userv.model.UserListResponse
import com.mamits.apnaonlines.userv.model.request.CashfreeObj
import com.mamits.apnaonlines.userv.model.request.CommonRequestObj
import com.mamits.apnaonlines.userv.model.request.PlaceOrderRequest
import com.mamits.apnaonlines.userv.model.request.SavePaymentRequest
import com.mamits.apnaonlines.userv.model.request.UserAuthRequestObj
import com.mamits.apnaonlines.userv.model.response.ApplyCouponResponse
import com.mamits.apnaonlines.userv.model.response.CashfreeTokenResponse
import com.mamits.apnaonlines.userv.model.response.CommonResponse
import com.mamits.apnaonlines.userv.model.response.GetCategoriesResponse
import com.mamits.apnaonlines.userv.model.response.GetCategoryByStoreResponse
import com.mamits.apnaonlines.userv.model.response.GetChatListResponse
import com.mamits.apnaonlines.userv.model.response.GetCouponListResponse
import com.mamits.apnaonlines.userv.model.response.GetDocumentResponse
import com.mamits.apnaonlines.userv.model.response.GetHomeDataResponse
import com.mamits.apnaonlines.userv.model.response.GetInvoiceResponse
import com.mamits.apnaonlines.userv.model.response.GetProductByCategoryResponse
import com.mamits.apnaonlines.userv.model.response.GetProductDetailResponse
import com.mamits.apnaonlines.userv.model.response.GetSettingResponse
import com.mamits.apnaonlines.userv.model.response.GetStoreAroundResponse
import com.mamits.apnaonlines.userv.model.response.GetStoreByCategoryResponse
import com.mamits.apnaonlines.userv.model.response.GetStoreByProductResponse
import com.mamits.apnaonlines.userv.model.response.GetStoreDetailResponse
import com.mamits.apnaonlines.userv.model.response.HomeSearchResponse
import com.mamits.apnaonlines.userv.model.response.LoginOtpResponse
import com.mamits.apnaonlines.userv.model.response.LoginResponse
import com.mamits.apnaonlines.userv.model.response.NotificationListResponse
import com.mamits.apnaonlines.userv.model.response.OrderHistoryResponse
import com.mamits.apnaonlines.userv.model.response.PaytmChecksumResponse
import com.mamits.apnaonlines.userv.model.response.ProductByStoreResponse
import com.mamits.apnaonlines.userv.model.response.SignupResponse
import com.mamits.apnaonlines.userv.model.response.UploadFileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface UserRepository {

    fun fetchAllUsers(result: Int): MutableLiveData<UserListResponse>

    fun insertUserList(userRes: MutableList<ResultUserItem>)

    fun updateUser(userRes: ResultUserItem)

    fun getAllUsersFromDb(): LiveData<List<ResultUserItem>>

    fun getHomeData(commonRequestObj: CommonRequestObj): MutableLiveData<GetHomeDataResponse>
    fun getStoreAround(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreAroundResponse>
    fun getCategories(commonRequestObj: CommonRequestObj): MutableLiveData<GetCategoriesResponse>
    fun getSubCategories(commonRequestObj: CommonRequestObj): MutableLiveData<GetCategoriesResponse>

    fun getStoreByCategory(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreByCategoryResponse>
    fun getDocuments(commonRequestObj: CommonRequestObj): MutableLiveData<GetDocumentResponse>
    fun getProductByCategory(commonRequestObj: CommonRequestObj): MutableLiveData<GetProductByCategoryResponse>

    fun getCategoryByStore(commonRequestObj: CommonRequestObj): MutableLiveData<GetCategoryByStoreResponse>
    fun getStoreByProduct(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreByProductResponse>


    fun getStoreDetail(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreDetailResponse>
    fun getProductDetail(commonRequestObj: CommonRequestObj): MutableLiveData<GetProductDetailResponse>

    //Login
    fun login(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginOtpResponse>
    fun verifyVendorCode(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginOtpResponse>
    fun register(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<SignupResponse>
    fun sendOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<CommonResponse>
    fun verifyOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginResponse>
    fun verifyLoginOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginResponse>

    //Listing
    fun getOrderHistory(commonRequestObj: CommonRequestObj): MutableLiveData<OrderHistoryResponse>
    fun getNewOrders(commonRequestObj: CommonRequestObj): MutableLiveData<OrderHistoryResponse>
    fun getNotificationList(commonRequestObj: CommonRequestObj): MutableLiveData<NotificationListResponse>
    fun getChatList(commonRequestObj: CommonRequestObj): MutableLiveData<GetChatListResponse>
    fun getUpdatedChatList(commonRequestObj: CommonRequestObj): MutableLiveData<GetChatListResponse>
    fun saveChat(
        token: String,
        file: MultipartBody.Part?,
        orderId: RequestBody,
        vendorId: RequestBody,
        msg: RequestBody,
        docName: RequestBody,
        docType: RequestBody
    ): MutableLiveData<GetChatListResponse>

    fun applyCoupon(commonRequestObj: CommonRequestObj): MutableLiveData<ApplyCouponResponse>
    fun getCouponList(commonRequestObj: CommonRequestObj): MutableLiveData<GetCouponListResponse>
    fun getOfferCouponList(commonRequestObj: CommonRequestObj): MutableLiveData<GetCouponListResponse>
    fun placeOrder(
        token: String,
        postOrderRequest: PlaceOrderRequest
    ): MutableLiveData<CommonResponse>

    fun uploadFile(
        token: String,
        file: MultipartBody.Part?,
        requestDocs: RequestBody,
        requestDocName: RequestBody,
        requestDocType: RequestBody
    ): MutableLiveData<UploadFileResponse>

    fun saveRating(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>

    fun changePwd(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>
    fun updateProfile(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>
    fun addDocument(
        token: String,
        file: MultipartBody.Part?,
        docNameBody: RequestBody
    ): MutableLiveData<CommonResponse>

    fun deleteDocument(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>


    fun getProdByStore(commonRequestObj: CommonRequestObj): MutableLiveData<ProductByStoreResponse>
    fun homeSearch(commonRequestObj: CommonRequestObj): MutableLiveData<HomeSearchResponse>
    fun cashfreeToken(
        token: String,
        cashfreeObj: CashfreeObj
    ): MutableLiveData<CashfreeTokenResponse>

    fun savePayment(
        token: String,
        savePaymentRequest: SavePaymentRequest
    ): MutableLiveData<CommonResponse>

    fun getInvoice(commonRequestObj: CommonRequestObj): MutableLiveData<GetInvoiceResponse>
    fun forgotPwd(commonRequestObj: UserAuthRequestObj): MutableLiveData<CommonResponse>
    fun removeCoupon(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>

    fun getPaytmChecksum(
        mobile: String,
        email: String,
        userid: String,
        orderid: String,
        amount: String
    ): MutableLiveData<PaytmChecksumResponse>

    fun updatePwd(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>
    fun getSetting(commonRequestObj: CommonRequestObj): MutableLiveData<GetSettingResponse>
    fun getHelpSupportWebview(token: String): MutableLiveData<String>
    fun getEnquiryWebview(token: String): MutableLiveData<String>

}