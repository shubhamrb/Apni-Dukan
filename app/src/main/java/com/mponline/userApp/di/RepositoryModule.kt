package com.mponline.userApp.di

import com.mponline.userApp.api.NetworkAPIService
import com.mponline.userApp.db.dao.UserDao
import com.mponline.userApp.listener.UserRepository
import com.mponline.userApp.repository.UserRepositoryImpl
import com.mponline.userApp.viewmodel.UserListViewModel
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
    @Provides
    fun provideViewModel(userRepository: UserRepository): UserListViewModel {
        return UserListViewModel(userRepository) as UserListViewModel
    }

}