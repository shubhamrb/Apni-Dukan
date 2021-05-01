package com.mponline.userApp.api

class ApiName {
    companion object {
        const val USER_LIST:String = "api/"

        const val GET_HOME:String = "api/getHomeData"
        const val STORE_AROUND:String = "api/getStoreAroundYou"
        const val GET_CATEGORIES:String = "api/getCategories"
        const val GET_SUBCATEGORIES:String = "api/getSubCategories"
        const val GET_STORE_BY_CATEGORY:String = "api/getStoreByCategory"
        const val GET_CATEGORY_BY_STORE:String = "api/getCategoryByStore"
        const val GET_PRODUCT_BY_CATEGORY:String = "api/getProductByCategory"
        const val GET_STORE_BY_PRODUCT:String = "api/getStoreByProduct"
        const val GET_STORE_DETAIL:String = "api/getStoreDetail"
        const val GET_PRODUCT_DETAIL:String = "api/getProductDetail"

        //2
        const val SIGNUP:String = "api/signup"
        const val SIGNIN:String = "api/signin"
        const val SEND_OTP:String = "api/sendOtp"
        const val VERIFY_MOBILE_OTP:String = "api/verifyMobileOtp"
        const val CHANGE_PWD:String = "api/auth/changePassword"
        const val UPDATE_PROFILE:String = "api/auth/update_profile"

    }
}