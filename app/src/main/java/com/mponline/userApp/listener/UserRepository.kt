package com.mponline.userApp.listener

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mponline.userApp.model.ResultUserItem
import com.mponline.userApp.model.UserListResponse
import com.mponline.userApp.model.request.CommonRequestObj
import com.mponline.userApp.model.response.*

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


}