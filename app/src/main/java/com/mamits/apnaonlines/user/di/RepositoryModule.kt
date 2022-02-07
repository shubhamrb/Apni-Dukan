package com.mamits.apnaonlines.user.di

import com.mamits.apnaonlines.user.api.NetworkAPIService
import com.mamits.apnaonlines.user.db.dao.UserDao
import com.mamits.apnaonlines.user.listener.UserRepository
import com.mamits.apnaonlines.user.repository.UserRepositoryImpl
import com.mamits.apnaonlines.user.viewmodel.UserListViewModel
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