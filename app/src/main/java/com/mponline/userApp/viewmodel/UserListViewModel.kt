package com.mponline.userApp.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mponline.userApp.listener.UserRepository
import com.mponline.userApp.model.ResultUserItem
import com.mponline.userApp.model.UserListResponse
import com.mponline.userApp.model.request.CommonRequestObj
import com.mponline.userApp.model.response.*

public class UserListViewModel  @ViewModelInject constructor(
    val userRepositoryImpl: UserRepository
) : ViewModel() {

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


}