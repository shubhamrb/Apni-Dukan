package com.mponline.userApp.listener

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mponline.userApp.model.ResultUserItem
import com.mponline.userApp.model.UserListResponse
import com.mponline.userApp.model.request.*
import com.mponline.userApp.model.response.*
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
    fun getProductByCategory(commonRequestObj: CommonRequestObj): MutableLiveData<GetProductByCategoryResponse>

    fun getCategoryByStore(commonRequestObj: CommonRequestObj): MutableLiveData<GetCategoryByStoreResponse>
    fun getStoreByProduct(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreByProductResponse>


    fun getStoreDetail(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreDetailResponse>
    fun getProductDetail(commonRequestObj: CommonRequestObj): MutableLiveData<GetProductDetailResponse>

    //Login
    fun login(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginResponse>
    fun register(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<SignupResponse>
    fun sendOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<CommonResponse>
    fun verifyOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginResponse>

    //Listing
    fun getOrderHistory(commonRequestObj: CommonRequestObj): MutableLiveData<OrderHistoryResponse>
    fun getNotificationList(commonRequestObj: CommonRequestObj): MutableLiveData<NotificationListResponse>
    fun getChatList(commonRequestObj: CommonRequestObj): MutableLiveData<GetChatListResponse>
    fun getUpdatedChatList(commonRequestObj: CommonRequestObj): MutableLiveData<GetChatListResponse>
    fun saveChat(token:String, file: MultipartBody.Part?, orderId: RequestBody, vendorId: RequestBody, msg: RequestBody): MutableLiveData<GetChatListResponse>
    fun applyCoupon(commonRequestObj: CommonRequestObj): MutableLiveData<ApplyCouponResponse>
    fun getCouponList(commonRequestObj: CommonRequestObj): MutableLiveData<GetCouponListResponse>
    fun getOfferCouponList(commonRequestObj: CommonRequestObj): MutableLiveData<GetCouponListResponse>
    fun placeOrder(token:String, postOrderRequest: PlaceOrderRequest): MutableLiveData<CommonResponse>
    fun uploadFile(token:String, file: MultipartBody.Part?, requestDocs: RequestBody): MutableLiveData<UploadFileResponse>
    fun saveRating(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>

    fun changePwd(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>
    fun updateProfile(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>
    fun getProdByStore(commonRequestObj: CommonRequestObj): MutableLiveData<ProductByStoreResponse>
    fun homeSearch(commonRequestObj: CommonRequestObj): MutableLiveData<HomeSearchResponse>
    fun cashfreeToken(token:String, cashfreeObj: CashfreeObj): MutableLiveData<CashfreeTokenResponse>

    fun savePayment(token:String, savePaymentRequest: SavePaymentRequest): MutableLiveData<CommonResponse>
    fun getInvoice(commonRequestObj: CommonRequestObj): MutableLiveData<GetInvoiceResponse>
    fun forgotPwd(commonRequestObj: UserAuthRequestObj): MutableLiveData<CommonResponse>
    fun removeCoupon(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>

    fun getPaytmChecksum(userid:String, orderid:String, amount:String): MutableLiveData<PaytmChecksumResponse>

    fun updatePwd(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse>
    fun getHelpSupportWebview(token: String): MutableLiveData<String>
    fun getEnquiryWebview(token: String): MutableLiveData<String>

}