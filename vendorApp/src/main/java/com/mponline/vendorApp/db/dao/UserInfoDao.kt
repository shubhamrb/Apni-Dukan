package com.mponline.vendorApp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mponline.vendorApp.model.ResultUserItem

@Dao
interface UserDao {

    @Query("SELECT * FROM ResultUserItem")
    fun loadUserList(): LiveData<List<ResultUserItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserList(userListResponse: MutableList<ResultUserItem>)

    @Update
    fun updateUser(user: ResultUserItem)
}
