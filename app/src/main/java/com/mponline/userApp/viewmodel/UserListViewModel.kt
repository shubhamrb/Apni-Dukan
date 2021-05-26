package com.mponline.userApp.viewmodel

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mponline.userApp.R
import com.mponline.userApp.listener.UserRepository
import com.mponline.userApp.model.ResultUserItem
import com.mponline.userApp.model.UserListResponse
import com.mponline.userApp.model.request.CommonRequestObj
import com.mponline.userApp.model.request.PlaceOrderRequest
import com.mponline.userApp.model.request.UserAuthRequestObj
import com.mponline.userApp.model.response.*
import dagger.hilt.android.qualifiers.ApplicationContext
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
        } else if (userAuthRequestObj?.pin?.isNullOrEmpty() || userAuthRequestObj?.pin?.length!! < 6) {
            return context?.resources?.getString(R.string.empty_pin)!!
        } else if (userAuthRequestObj?.device_token?.isNullOrEmpty()) {
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
        } else if (userAuthRequestObj?.pin?.isNullOrEmpty() || userAuthRequestObj?.pin?.length!! < 6) {
            return context?.resources?.getString(R.string.empty_pin)!!
        } else if (userAuthRequestObj?.device_token?.isNullOrEmpty()) {
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

    //Login
    fun login(userAuthRequestObj: UserAuthRequestObj): LiveData<LoginResponse> {
        return userRepositoryImpl.login(userAuthRequestObj);
    }

    fun register(userAuthRequestObj: UserAuthRequestObj): LiveData<SignupResponse> {
        return userRepositoryImpl.register(userAuthRequestObj);
    }

    fun sendOtp(userAuthRequestObj: UserAuthRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.sendOtp(userAuthRequestObj);
    }

    fun verifyOtp(userAuthRequestObj: UserAuthRequestObj): LiveData<CommonResponse> {
        return userRepositoryImpl.verifyOtp(userAuthRequestObj);
    }

    fun getOrderHistory(commonRequestObj: CommonRequestObj): LiveData<OrderHistoryResponse> {
        return userRepositoryImpl.getOrderHistory(commonRequestObj);
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

    fun saveChat(token:String, file: MultipartBody.Part?, orderId: RequestBody, vendorId: RequestBody, msg: RequestBody): LiveData<GetChatListResponse> {
        return userRepositoryImpl.saveChat(token, file, orderId, vendorId, msg);
    }

    fun getCouponList(commonRequestObj: CommonRequestObj): LiveData<GetCouponListResponse> {
        return userRepositoryImpl.getCouponList(commonRequestObj);
    }

    fun applyCoupon(commonRequestObj: CommonRequestObj): LiveData<ApplyCouponResponse> {
        return userRepositoryImpl.applyCoupon(commonRequestObj);
    }
    fun placeOrder(token:String, postOrderRequest: PlaceOrderRequest): LiveData<CommonResponse> {
        return userRepositoryImpl.placeOrder(token, postOrderRequest);
    }

    fun uploadFile(token:String, file: MultipartBody.Part?, requestDocs: RequestBody): LiveData<UploadFileResponse> {
        return userRepositoryImpl.uploadFile(token, file, requestDocs);
    }


}