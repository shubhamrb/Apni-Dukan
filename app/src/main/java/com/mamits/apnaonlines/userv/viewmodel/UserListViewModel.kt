package com.mamits.apnaonlines.userv.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.mamits.apnaonlines.userv.R
import com.mamits.apnaonlines.userv.listener.UserRepository
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

public class UserListViewModel @ViewModelInject constructor(
//    @ApplicationContext var application: Context,
    val userRepositoryImpl: UserRepository
) : ViewModel() {

    //Validation
    fun validateLogin(userAuthRequestObj: UserAuthRequestObj, context: Context): String {
        if (userAuthRequestObj?.mobile?.isNullOrEmpty() || userAuthRequestObj?.mobile?.length!! < 10) {
            return context?.resources?.getString(R.string.empty_mobile)!!
        }
        /* else if (userAuthRequestObj?.pin?.isNullOrEmpty() || userAuthRequestObj?.pin?.length!! < 6) {
             return context?.resources?.getString(R.string.empty_pin)!!
         } */
        else if (userAuthRequestObj?.device_token?.isNullOrEmpty()) {
            return context?.resources?.getString(R.string.empty_device_token)!!
        } else if (userAuthRequestObj?.device_type?.isNullOrEmpty()) {
            return context?.resources?.getString(R.string.empty_device_type)!!
        }
        return context?.resources?.getString(R.string.valid)!!
    }

    fun validateRegister(userAuthRequestObj: UserAuthRequestObj, context: Context): String {
        if (userAuthRequestObj?.name?.isNullOrEmpty() || userAuthRequestObj?.name?.length!! < 3) {
            return context?.resources?.getString(R.string.empty_name)!!
        } else if (userAuthRequestObj?.mobile?.isNullOrEmpty() || userAuthRequestObj?.mobile?.length!! < 10) {
            return context?.resources?.getString(R.string.empty_mobile)!!
        } else if (userAuthRequestObj?.vendor_code?.isNullOrEmpty()) {
            return context?.resources?.getString(R.string.empty_vendor_code)!!
        }
        /*else if (userAuthRequestObj?.pin?.isNullOrEmpty() || userAuthRequestObj?.pin?.length!! < 6) {
            return context?.resources?.getString(R.string.empty_pin)!!
        } */
        else if (userAuthRequestObj?.device_token?.isNullOrEmpty()) {
            return context?.resources?.getString(R.string.empty_device_token)!!
        } else if (userAuthRequestObj?.device_type?.isNullOrEmpty()) {
            return context?.resources?.getString(R.string.empty_device_type)!!
        }
        return context?.resources?.getString(R.string.valid)!!
    }

    //API calls
    fun fetchUsersFromDb(): LiveData<List<ResultUserItem>> {
        return userRepositoryImpl.getAllUsersFromDb();
    }


    fun fetchUserListInfo(results: Int): MutableLiveData<UserListResponse> {
        return userRepositoryImpl?.fetchAllUsers(results)
    }

    fun updateUserInfo(usr: ResultUserItem) {
        userRepositoryImpl.updateUser(usr);
    }

    //MpOnline
    fun getHomeData(commonRequestObj: CommonRequestObj): LiveData<GetHomeDataResponse> {
        return userRepositoryImpl.getHomeData(commonRequestObj);
    }

    fun getCategories(commonRequestObj: CommonRequestObj): LiveData<GetCategoriesResponse> {
        return userRepositoryImpl.getCategories(commonRequestObj);
    }

    fun getSubCategories(commonRequestObj: CommonRequestObj): LiveData<GetCategoriesResponse> {
        return userRepositoryImpl.getSubCategories(commonRequestObj);
    }

    fun getStoreAround(commonRequestObj: CommonRequestObj): LiveData<GetStoreAroundResponse> {
        return userRepositoryImpl.getStoreAround(commonRequestObj);
    }

    fun getStoreByCategory(commonRequestObj: CommonRequestObj): LiveData<GetStoreByCategoryResponse> {
        return userRepositoryImpl.getStoreByCategory(commonRequestObj);
    }

    fun getDocuments(commonRequestObj: CommonRequestObj): LiveData<GetDocumentResponse> {
        return userRepositoryImpl.getDocuments(commonRequestObj);
    }

    fun getProductByCategory(commonRequestObj: CommonRequestObj): LiveData<GetProductByCategoryResponse> {
        return userRepositoryImpl.getProductByCategory(commonRequestObj);
    }

    fun getCategoryByStore(commonRequestObj: CommonRequestObj): LiveData<GetCategoryByStoreResponse> {
        return userRepositoryImpl.getCategoryByStore(commonRequestObj);
    }

    fun getStoreByProduct(commonRequestObj: CommonRequestObj): LiveData<GetStoreByProductResponse> {
        return userRepositoryImpl.getStoreByProduct(commonRequestObj);
    }

    fun getStoreDetail(commonRequestObj: CommonRequestObj): LiveData<GetStoreDetailResponse> {
        return userRepositoryImpl.getStoreDetail(commonRequestObj);
    }

    fun getProductDetail(commonRequestObj: CommonRequestObj): LiveData<GetProductDetailResponse> {
        return userRepositoryImpl.getProductDetail(commonRequestObj);
    }

    fun verifyVendorCode(userAuthRequestObj: UserAuthRequestObj): LiveData<LoginOtpResponse> {
        return userRepositoryImpl.verifyVendorCode(userAuthRequestObj);
    }

    //Login
    fun login(userAuthRequestObj: UserAuthRequestObj): LiveData<LoginOtpResponse> {
        return userRepositoryImpl.login(userAuthRequestObj);
    }

    fun register(userAuthRequestObj: UserAuthRequestObj): LiveData<SignupResponse> {
        return userRepositoryImpl.register(userAuthRequestObj);
    }

    fun sendOtp(userAuthRequestObj: UserAuthRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.sendOtp(userAuthRequestObj);
    }

    fun verifyOtp(userAuthRequestObj: UserAuthRequestObj): LiveData<LoginResponse> {
        return userRepositoryImpl.verifyOtp(userAuthRequestObj);
    }

    fun verifyLoginOtp(userAuthRequestObj: UserAuthRequestObj): LiveData<LoginResponse> {
        return userRepositoryImpl.verifyLoginOtp(userAuthRequestObj);
    }

    fun getOrderHistory(commonRequestObj: CommonRequestObj): LiveData<OrderHistoryResponse> {
        return userRepositoryImpl.getOrderHistory(commonRequestObj);
    }

    fun getNewOrders(commonRequestObj: CommonRequestObj): LiveData<OrderHistoryResponse> {
        return userRepositoryImpl.getNewOrders(commonRequestObj);
    }

    fun getNotificationList(commonRequestObj: CommonRequestObj): LiveData<NotificationListResponse> {
        return userRepositoryImpl.getNotificationList(commonRequestObj);
    }

    fun getChatList(commonRequestObj: CommonRequestObj): LiveData<GetChatListResponse> {
        return userRepositoryImpl.getChatList(commonRequestObj);
    }

    fun getUpdatedChatList(commonRequestObj: CommonRequestObj): LiveData<GetChatListResponse> {
        return userRepositoryImpl.getUpdatedChatList(commonRequestObj);
    }

    fun saveChat(
        token: String,
        file: MultipartBody.Part?,
        orderId: RequestBody,
        vendorId: RequestBody,
        msg: RequestBody,
        docName: RequestBody,
        docType: RequestBody
    ): LiveData<GetChatListResponse> {
        return userRepositoryImpl.saveChat(token, file, orderId, vendorId, msg, docName, docType);
    }

    fun getCouponList(commonRequestObj: CommonRequestObj): LiveData<GetCouponListResponse> {
        return userRepositoryImpl.getCouponList(commonRequestObj);
    }

    fun getOfferCouponList(commonRequestObj: CommonRequestObj): LiveData<GetCouponListResponse> {
        return userRepositoryImpl.getOfferCouponList(commonRequestObj);
    }

    fun applyCoupon(commonRequestObj: CommonRequestObj): LiveData<ApplyCouponResponse> {
        return userRepositoryImpl.applyCoupon(commonRequestObj);
    }

    fun checkPhonepeStatus(
        PHONEPE_MERCHANT_ID: String,
        PHONEPE_MERCHANT_TR_ID: String,
        headers: Map<String, String>
    ): LiveData<JsonObject> {
        return userRepositoryImpl.checkPhonepeStatus(
            PHONEPE_MERCHANT_ID,
            PHONEPE_MERCHANT_TR_ID,
            headers
        );
    }

    fun placeOrder(token: String, postOrderRequest: PlaceOrderRequest): LiveData<CommonResponse> {
        return userRepositoryImpl.placeOrder(token, postOrderRequest);
    }

    fun uploadFile(
        token: String,
        file: MultipartBody.Part?,
        requestDocs: RequestBody,
        requestDocName: RequestBody,
        requestDocType: RequestBody
    ): LiveData<UploadFileResponse> {
        return userRepositoryImpl.uploadFile(
            token,
            file,
            requestDocs,
            requestDocName,
            requestDocType
        );
    }

    fun saveRating(commonRequestObj: CommonRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.saveRating(commonRequestObj);
    }

    fun changePwd(commonRequestObj: CommonRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.changePwd(commonRequestObj);
    }

    fun updateProfile(commonRequestObj: CommonRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.updateProfile(commonRequestObj);
    }

    fun deleteDocument(commonRequestObj: CommonRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.deleteDocument(commonRequestObj);
    }

    fun addDocument(
        token: String,
        file: MultipartBody.Part?,
        docNameBody: RequestBody
    ): LiveData<CommonResponse> {
        return userRepositoryImpl.addDocument(token, file, docNameBody);
    }

    fun getProdByStore(commonRequestObj: CommonRequestObj): LiveData<ProductByStoreResponse> {
        return userRepositoryImpl.getProdByStore(commonRequestObj);
    }

    fun homeSearch(commonRequestObj: CommonRequestObj): LiveData<HomeSearchResponse> {
        return userRepositoryImpl.homeSearch(commonRequestObj);
    }

    fun cashfreeToken(token: String, cashfreeObj: CashfreeObj): LiveData<CashfreeTokenResponse> {
        return userRepositoryImpl.cashfreeToken(token, cashfreeObj);
    }

    fun savePayment(
        token: String,
        savePaymentRequest: SavePaymentRequest
    ): LiveData<CommonResponse> {
        return userRepositoryImpl.savePayment(token, savePaymentRequest);
    }

    fun getInvoice(commonRequestObj: CommonRequestObj): LiveData<GetInvoiceResponse> {
        return userRepositoryImpl.getInvoice(commonRequestObj);
    }

    fun forgotPwd(commonRequestObj: UserAuthRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.forgotPwd(commonRequestObj);
    }

    fun removeCoupon(commonRequestObj: CommonRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.removeCoupon(commonRequestObj);
    }

    fun getPaytmChecksum(
        mobile: String,
        email: String,
        userid: String,
        orderid: String,
        amount: String
    ): LiveData<PaytmChecksumResponse> {
        return userRepositoryImpl.getPaytmChecksum(mobile, email, userid, orderid, amount);
    }

    fun updatePwd(commonRequestObj: CommonRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.updatePwd(commonRequestObj);
    }

    fun getSetting(commonRequestObj: CommonRequestObj): LiveData<GetSettingResponse> {
        return userRepositoryImpl.getSetting(commonRequestObj);
    }

    fun getHelpSupportWebview(token: String): LiveData<String> {
        return userRepositoryImpl.getHelpSupportWebview(token);
    }

    fun getEnquiryWebview(token: String): LiveData<String> {
        return userRepositoryImpl.getEnquiryWebview(token);
    }


}