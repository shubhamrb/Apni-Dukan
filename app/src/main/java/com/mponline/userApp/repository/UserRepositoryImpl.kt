package com.mponline.userApp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mponline.userApp.api.NetworkAPIService
import com.mponline.userApp.db.dao.UserDao
import com.mponline.userApp.listener.UserRepository
import com.mponline.userApp.model.ResultUserItem
import com.mponline.userApp.model.UserListResponse
import com.mponline.userApp.model.request.CommonRequestObj
import com.mponline.userApp.model.request.UserAuthRequestObj
import com.mponline.userApp.model.response.*
import com.mponline.userApp.util.CommonUtils
import kotlinx.coroutines.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(val apiService: NetworkAPIService, val userDao: UserDao):
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
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getHomeData(commonRequestObj)
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
    override fun login(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<LoginResponse> {
        val data = MutableLiveData<LoginResponse>()
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

    override fun verifyOtp(userAuthRequestObj: UserAuthRequestObj): MutableLiveData<CommonResponse> {
        val data = MutableLiveData<CommonResponse>()
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

    //Listing
    override fun getOrderHistory(commonRequestObj: CommonRequestObj): MutableLiveData<OrderHistoryResponse> {
        val data = MutableLiveData<OrderHistoryResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.getOrderHistory(commonRequestObj?.headerInfo?.Authorization!!)
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
                val response = apiService?.getNotificationList(commonRequestObj?.headerInfo?.Authorization!!)
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
                val response = apiService?.getChatList(commonRequestObj?.headerInfo?.Authorization!!, commonRequestObj)
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
                val response = apiService?.getUpdatedChatList(commonRequestObj?.headerInfo?.Authorization!!, commonRequestObj)
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

    override fun saveChat(commonRequestObj: CommonRequestObj): MutableLiveData<GetChatListResponse> {
        val data = MutableLiveData<GetChatListResponse>()
        val errorOnAPI = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService?.saveChat(commonRequestObj?.headerInfo?.Authorization!!, commonRequestObj)
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
                val response = apiService?.applyCoupon(commonRequestObj?.headerInfo?.Authorization!!, commonRequestObj)
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
                val response = apiService?.getCouponList(commonRequestObj?.headerInfo?.Authorization!!, commonRequestObj)
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



}