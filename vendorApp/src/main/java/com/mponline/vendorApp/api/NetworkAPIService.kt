package com.mponline.vendorApp.api

import com.mponline.vendorApp.model.UserListResponse
import retrofit2.Response
import retrofit2.http.*

interface NetworkAPIService {

    @GET(ApiName.USER_LIST)
    suspend fun fetchUsers(@Query("results") results :Int): Response<UserListResponse>

}