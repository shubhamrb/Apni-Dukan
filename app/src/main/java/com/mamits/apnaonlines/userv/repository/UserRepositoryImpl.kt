package com.mamits.apnaonlines.userv.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mamits.apnaonlines.userv.api.NetworkAPIService
import com.mamits.apnaonlines.userv.db.dao.UserDao
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
import com.mamits.apnaonlines.userv.util.CommonUtils
import com.mamits.apnaonlines.userv.util.Constants.Companion.EXCEPTION_500
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    val apiService: NetworkAPIService,
    val userDao: UserDao
) :
    UserRepository {
    override fun fetchAllUsers(results: Int): MutableLiveData<UserListResponse> {
        val data = MutableLiveData<UserListResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.fetchUsers(results)
                if (response?.isSuccessful!!) {
                    response?.body()?.results?.forEachIndexed { index, resultUserItem ->
                        resultUserItem?.userId = index?.toString()
                        CommonUtils.printLog("INTERATE", "${resultUserItem?.userId}")
                    }
                    /**
                     * Insert data in Room db
                     */
                    insertUserList((response?.body()?.results as MutableList<ResultUserItem>?)!!)
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("Something went wrong::${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun insertUserList(userRes: MutableList<ResultUserItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertUserList(userRes)
        }
    }

    override fun updateUser(userRes: ResultUserItem) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.updateUser(userRes)
        }
    }

    override fun getAllUsersFromDb(): LiveData<List<ResultUserItem>> {
        return userDao.loadUserList()
    }

    override fun getHomeData(commonRequestObj: CommonRequestObj): MutableLiveData<GetHomeDataResponse> {
        val data = MutableLiveData<GetHomeDataResponse>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var response = apiService?.getHomeData(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    CommonUtils.printLog("EXCEPTION_API", "${response?.message()}")
                    data.postValue(GetHomeDataResponse(status = false, message = EXCEPTION_500))
                }
            } catch (e: Exception) {
                CommonUtils.printLog("EXCEPTION_API", "${e?.message}")
                data.postValue(GetHomeDataResponse(status = false, message = EXCEPTION_500))
            }
        }
        return data;
    }

    override fun getStoreAround(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreAroundResponse> {
        val data = MutableLiveData<GetStoreAroundResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getStoreAround(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getCategories(commonRequestObj: CommonRequestObj): MutableLiveData<GetCategoriesResponse> {
        val data = MutableLiveData<GetCategoriesResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getCategories(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getSubCategories(commonRequestObj: CommonRequestObj): MutableLiveData<GetCategoriesResponse> {
        val data = MutableLiveData<GetCategoriesResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getSubCategories(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getStoreByCategory(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreByCategoryResponse> {
        val data = MutableLiveData<GetStoreByCategoryResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getStoreByCategory(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getDocuments(commonRequestObj: CommonRequestObj): MutableLiveData<GetDocumentResponse> {
        val data = MutableLiveData<GetDocumentResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getDocuments(
                    commonRequestObj.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getCategoryByStore(commonRequestObj: CommonRequestObj): MutableLiveData<GetCategoryByStoreResponse> {
        val data = MutableLiveData<GetCategoryByStoreResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getCategoryByStore(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getProductByCategory(commonRequestObj: CommonRequestObj): MutableLiveData<GetProductByCategoryResponse> {
        val data = MutableLiveData<GetProductByCategoryResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getProductByCategory(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getStoreByProduct(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreByProductResponse> {
        val data = MutableLiveData<GetStoreByProductResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getStoreByProduct(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getStoreDetail(commonRequestObj: CommonRequestObj): MutableLiveData<GetStoreDetailResponse> {
        val data = MutableLiveData<GetStoreDetailResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getStoreDetail(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getProductDetail(commonRequestObj: CommonRequestObj): MutableLiveData<GetProductDetailResponse> {
        val data = MutableLiveData<GetProductDetailResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getProductDetail(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    //Login
    override fun login(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginOtpResponse> {
        val data = MutableLiveData<LoginOtpResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.login(userAuthRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun verifyVendorCode(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginOtpResponse> {
        val data = MutableLiveData<LoginOtpResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.verifyVendorCode(userAuthRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun register(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<SignupResponse> {
        val data = MutableLiveData<SignupResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.signup(userAuthRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun sendOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.sendOtp(userAuthRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun verifyOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginResponse> {
        val data = MutableLiveData<LoginResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.verifyMobile(userAuthRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun verifyLoginOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginResponse> {
        val data = MutableLiveData<LoginResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.verifyLoginOtp(userAuthRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    //Listing
    override fun getOrderHistory(commonRequestObj: CommonRequestObj): MutableLiveData<OrderHistoryResponse> {
        val data = MutableLiveData<OrderHistoryResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getOrderHistory(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getNewOrders(commonRequestObj: CommonRequestObj): MutableLiveData<OrderHistoryResponse> {
        val data = MutableLiveData<OrderHistoryResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    apiService?.getNewOrders(commonRequestObj?.headerInfo?.Authorization!!)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getNotificationList(commonRequestObj: CommonRequestObj): MutableLiveData<NotificationListResponse> {
        val data = MutableLiveData<NotificationListResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getNotificationList(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getChatList(commonRequestObj: CommonRequestObj): MutableLiveData<GetChatListResponse> {
        val data = MutableLiveData<GetChatListResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getChatList(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getUpdatedChatList(commonRequestObj: CommonRequestObj): MutableLiveData<GetChatListResponse> {
        val data = MutableLiveData<GetChatListResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getUpdatedChatList(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun saveChat(
        token: String,
        file: MultipartBody.Part?,
        orderId: RequestBody,
        vendorId: RequestBody,
        msg: RequestBody,
        docName: RequestBody,
        docType: RequestBody
    ): MutableLiveData<GetChatListResponse> {
        val data = MutableLiveData<GetChatListResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    apiService?.saveChat(token, file, orderId, vendorId, msg, docName, docType)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun applyCoupon(commonRequestObj: CommonRequestObj): MutableLiveData<ApplyCouponResponse> {
        val data = MutableLiveData<ApplyCouponResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.applyCoupon(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getCouponList(commonRequestObj: CommonRequestObj): MutableLiveData<GetCouponListResponse> {
        val data = MutableLiveData<GetCouponListResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getCouponList(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getOfferCouponList(commonRequestObj: CommonRequestObj): MutableLiveData<GetCouponListResponse> {
        val data = MutableLiveData<GetCouponListResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response =
                    apiService?.getOfferCouponList(commonRequestObj?.headerInfo?.Authorization!!)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun placeOrder(
        token: String,
        postOrderRequest: PlaceOrderRequest
    ): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.placeOrder(token, postOrderRequest)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun uploadFile(
        token: String,
        file: MultipartBody.Part?,
        requestDocs: RequestBody,
        requestDocName: RequestBody,
        requestDocType: RequestBody
    ): MutableLiveData<UploadFileResponse> {
        val data = MutableLiveData<UploadFileResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.callUploadDocuments(
                    token,
                    file,
                    requestDocs,
                    requestDocName,
                    requestDocType
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun saveRating(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.saveRating(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun changePwd(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.changePwd(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun updateProfile(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.updateProfile(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun deleteDocument(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.deleteDocument(
                    commonRequestObj.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun addDocument(
        token: String,
        file: MultipartBody.Part?,
        docNameBody: RequestBody
    ): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.addDocument(
                    token,
                    file!!, docNameBody
                )
                if (response.isSuccessful) {
                    data.postValue(response.body())
                } else {
                    errorOnAPI.postValue(response.message())
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getProdByStore(commonRequestObj: CommonRequestObj): MutableLiveData<ProductByStoreResponse> {
        val data = MutableLiveData<ProductByStoreResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getProdByStore(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun homeSearch(commonRequestObj: CommonRequestObj): MutableLiveData<HomeSearchResponse> {
        val data = MutableLiveData<HomeSearchResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.homeSearch(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun cashfreeToken(
        token: String,
        cashfreeObj: CashfreeObj
    ): MutableLiveData<CashfreeTokenResponse> {
        val data = MutableLiveData<CashfreeTokenResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.cashfreeToken(token, cashfreeObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun savePayment(
        token: String,
        savePaymentRequest: SavePaymentRequest
    ): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.savePayment(token, savePaymentRequest)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getInvoice(commonRequestObj: CommonRequestObj): MutableLiveData<GetInvoiceResponse> {
        val data = MutableLiveData<GetInvoiceResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getInvoice(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun forgotPwd(commonRequestObj: UserAuthRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.forgotPwd(commonRequestObj)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun removeCoupon(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.removeCoupon(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun getPaytmChecksum(
        mobile: String,
        email: String,
        userid: String,
        orderid: String,
        amount: String
    ): MutableLiveData<PaytmChecksumResponse> {
        val data = MutableLiveData<PaytmChecksumResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getPaytmChecksum(mobile, email, userid, orderid, amount)
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    errorOnAPI.postValue("${response.message()}")
                }

            } catch (e: Exception) {
                errorOnAPI.postValue("Something went wrong::${e.localizedMessage}")
            }
        }
        return data;
    }

    override fun updatePwd(commonRequestObj: CommonRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.updatePwd(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    data.postValue(
                        CommonResponse(
                            message = "Something went wrong, please try again later",
                            status = false
                        )
                    )
                }

            } catch (e: Exception) {
                data.postValue(
                    CommonResponse(
                        message = "Something went wrong, please try again later",
                        status = false
                    )
                )
            }
        }
        return data;
    }

    override fun getSetting(commonRequestObj: CommonRequestObj): MutableLiveData<GetSettingResponse> {
        val data = MutableLiveData<GetSettingResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getSetting(
                    commonRequestObj?.headerInfo?.Authorization!!,
                    commonRequestObj
                )
                if (response?.isSuccessful!!) {
                    data.postValue(response?.body())
                } else {
                    data.postValue(
                        GetSettingResponse(
                            message = "Something went wrong, please try again later",
                            status = false
                        )
                    )
                }
            } catch (e: Exception) {
                data.postValue(
                    GetSettingResponse(
                        message = "Something went wrong, please try again later",
                        status = false
                    )
                )
            }
        }
        return data;
    }

    override fun getHelpSupportWebview(token: String): MutableLiveData<String> {
        val data = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getHelpSupportWebview(token)
//                if (response?.isSuccessful!!) {
                data.postValue(response?.body())
//                } else {
//                    data.postValue("Something went wrong, please try again later!")
//                }

            } catch (e: Exception) {
                data.postValue("Something went wrong, please try again later!")
            }
        }
        return data;
    }

    override fun getEnquiryWebview(token: String): MutableLiveData<String> {
        val data = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getEnquiryWebview(token)
//                if (response?.isSuccessful!!) {
                data.postValue(response)
//                } else {
//                    data.postValue("Something went wrong, please try again later!")
//                }

            } catch (e: Exception) {
                data.postValue("Something went wrong, please try again later!")
            }
        }
        return data;
    }


}