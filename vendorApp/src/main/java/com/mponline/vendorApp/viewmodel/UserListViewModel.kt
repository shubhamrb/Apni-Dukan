package com.mponline.vendorApp.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mponline.vendorApp.listener.UserRepository
import com.mponline.vendorApp.model.ResultUserItem
import com.mponline.vendorApp.model.UserListResponse

class UserListViewModel  @ViewModelInject constructor(
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

}