package com.mponline.vendorApp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mponline.vendorApp.api.NetworkAPIService
import com.mponline.vendorApp.db.dao.UserDao
import com.mponline.vendorApp.listener.UserRepository
import com.mponline.vendorApp.model.ResultUserItem
import com.mponline.vendorApp.model.UserListResponse
import com.mponline.vendorApp.util.CommonUtils
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
}