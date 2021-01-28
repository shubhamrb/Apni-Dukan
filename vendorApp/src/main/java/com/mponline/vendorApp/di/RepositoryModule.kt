package com.mponline.vendorApp.di

import com.mponline.vendorApp.api.NetworkAPIService
import com.mponline.vendorApp.db.dao.UserDao
import com.mponline.vendorApp.listener.UserRepository
import com.mponline.vendorApp.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class RepositoryModule {

    @Provides
    fun provideDataRepository(apiService: NetworkAPIService, userDao: UserDao): UserRepository {
        return UserRepositoryImpl(
            apiService,
            userDao
        ) as UserRepository
    }

}