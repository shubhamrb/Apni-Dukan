package com.mamits.apnaonlines.userv.di

import com.mamits.apnaonlines.userv.api.NetworkAPIService
import com.mamits.apnaonlines.userv.db.dao.UserDao
import com.mamits.apnaonlines.userv.listener.UserRepository
import com.mamits.apnaonlines.userv.repository.UserRepositoryImpl
import com.mamits.apnaonlines.userv.viewmodel.UserListViewModel
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