package com.mponline.vendorApp.listener

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mponline.vendorApp.model.ResultUserItem
import com.mponline.vendorApp.model.UserListResponse

interface UserRepository {

    fun fetchAllUsers(result:Int): MutableLiveData<UserListResponse>

    fun insertUserList(userRes: MutableList<ResultUserItem>)

    fun updateUser(userRes: ResultUserItem)

    fun getAllUsersFromDb(): LiveData<List<ResultUserItem>>
}