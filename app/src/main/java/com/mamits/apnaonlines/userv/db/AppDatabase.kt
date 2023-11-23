package com.mamits.apnaonlines.userv.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mamits.apnaonlines.userv.db.dao.UserDao
import com.mamits.apnaonlines.userv.model.ResultUserItem

@Database(entities = [ ResultUserItem::class],version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object{
        val DATABASE_NAME = "mponline_user_db"
        var INSTANCE: AppDatabase? = null
        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE =
                        Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
//                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)  //Only update the schema
                            .build()
                }
            }
            return INSTANCE
        }
    }

}