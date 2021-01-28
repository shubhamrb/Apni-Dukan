package com.mponline.vendorApp.di

import android.app.Application
import androidx.room.Room
import com.mponline.vendorApp.db.dao.UserDao
import com.mponline.vendorApp.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesAppDatabase(app:Application): AppDatabase {
        return Room.databaseBuilder(app,
            AppDatabase::class.java,"demoapp_db").build()
    }

    @Singleton
    @Provides
    fun providesUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

}