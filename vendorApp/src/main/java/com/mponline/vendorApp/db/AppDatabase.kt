package com.mponline.vendorApp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mponline.vendorApp.db.dao.UserDao
import com.mponline.vendorApp.model.ResultUserItem

@Database(entities = [ ResultUserItem::class],version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
}