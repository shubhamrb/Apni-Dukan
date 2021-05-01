package com.mponline.userApp.api

import com.mponline.userApp.model.UserListResponse
import com.mponline.userApp.model.request.CommonRequestObj
import com.mponline.userApp.model.request.UserAuthRequestObj
import com.mponline.userApp.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface NetworkAPIService {

    @GET(ApiName.USER_LIST)
    suspend fun fetchUsers(@Query("results") results: Int): Response<UserListResponse>

    //MpOnline
    @POST(ApiName.GET_HOME)
    suspend fun getHomeData(@Body commonRequestObj: CommonRequestObj): Response<GetHomeDataResponse>

    @POST(ApiName.STORE_AROUND)
    suspend fun getStoreAround(@Body commonRequestObj: CommonRequestObj): Response<GetStoreAroundResponse>

    @POST(ApiName.GET_CATEGORIES)
    suspend fun getCategories(@Body commonRequestObj: CommonRequestObj): Response<GetCategoriesResponse>

    @POST(ApiName.GET_SUBCATEGORIES)
    suspend fun getSubCategories(@Body commonRequestObj: CommonRequestObj): Response<GetCategoriesResponse>

    @POST(ApiName.GET_STORE_BY_CATEGORY)
    suspend fun getStoreByCategory(@Body commonRequestObj: CommonRequestObj): Response<GetStoreByCategoryResponse>

    @POST(ApiName.GET_CATEGORY_BY_STORE)
    suspend fun getCategoryByStore(@Body commonRequestObj: CommonRequestObj): Response<GetCategoryByStoreResponse>

    @POST(ApiName.GET_PRODUCT_BY_CATEGORY)
    suspend fun getProductByCategory(@Body commonRequestObj: CommonRequestObj): Response<GetProductByCategoryResponse>

    @POST(ApiName.GET_STORE_BY_PRODUCT)
    suspend fun getStoreByProduct(@Body commonRequestObj: CommonRequestObj): Response<GetStoreByProductResponse>

    @POST(ApiName.GET_STORE_DETAIL)
    suspend fun getStoreDetail(@Body commonRequestObj: CommonRequestObj): Response<GetStoreDetailResponse>

    @POST(ApiName.GET_PRODUCT_DETAIL)
    suspend fun getProductDetail(@Body commonRequestObj: CommonRequestObj): Response<GetProductDetailResponse>

    //Login
    @POST(ApiName.SIGNUP)
    suspend fun signup(@Body userAuthRequestObj: UserAuthRequestObj): Response<SignupResponse>

    @POST(ApiName.SIGNIN)
    suspend fun login(@Body userAuthRequestObj: UserAuthRequestObj): Response<LoginResponse>

    @POST(ApiName.SEND_OTP)
    suspend fun sendOtp(@Body userAuthRequestObj: UserAuthRequestObj): Response<CommonResponse>

    @POST(ApiName.VERIFY_MOBILE_OTP)
    suspend fun verifyMobile(@Body userAuthRequestObj: UserAuthRequestObj): Response<CommonResponse>


}