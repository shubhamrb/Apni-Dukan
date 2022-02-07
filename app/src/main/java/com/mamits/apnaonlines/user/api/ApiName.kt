package com.mamits.apnaonlines.user.api

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
        const val VERIFY_LOGIN_OTP:String = "api/verifyLoginOtp"

        const val GET_ORDER_HISTORY:String = "api/auth/getOrderHistory"
        const val GET_LATEST_ORDER:String = "api/auth/getLatestOrder"
        const val NOTIFICATION_LIST:String = "api/auth/getNotificationList"
        const val GET_CHAT_LIST:String = "api/auth/getChatList"
        const val GET_UPDATED_CHAT_LIST:String = "api/auth/getUpdatedMessage"
        const val SAVE_CHAT:String = "api/auth/saveChat"
        const val GET_COUPON_LIST:String = "api/auth/couponList"
        const val APPLY_COUPON:String = "api/auth/applycoupon"
        const val REMOVE_COUPON:String = "api/auth/removecoupon"
        const val PLACE_ORDER:String = "api/auth/placeOrder"
        const val UPLOAD_FILE:String = "api/auth/uploadFile"
        const val SAVE_RATINGS:String = "api/auth/saveRatting"

        const val CHANGE_PWD:String = "api/auth/changePassword"
        const val UPDATE_PROFILE:String = "api/auth/update_profile"
        const val GET_PRODUCT_BY_STORE:String = "api/getProductByStore"
        const val HOME_SEARCH:String = "api/search"

        const val CASHFREE_TOKEN:String = "api/auth/cashfreetoken"
        const val SAVE_PAYMENT:String = "api/auth/paymentdone"
        const val GET_INVOICE:String = "api/auth/getInvoice"
        const val FORGOT_PWD:String = "api/forgotpassword"
        const val PAYTM_CHECKSUM:String = "paytmchecksum/index.php"
        const val UPDATE_PWD:String = "api/updatepassword"
        const val GET_SETTING:String = "api/getSetting"
        const val ENQUIRY_WEBVIEW:String = "api/auth/getenquirylist"
        const val HELP_SUPPORT_WEBVIEW:String = "api/auth/gethelpsupport"

    }
}