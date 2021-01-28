package com.recyclemybin.utils


/**
 * Created by Shashank
 */
class Constants {
    companion object {
        const val REQUEST_PERMISSIONS = 200
        //        const val BASE_URL = "http://103.26.97.22/SP_WS/SP_S_Webservice.svc/"
//        const val BASE_URL = "https://recyclemybin.000webhostapp.com/"
//        const val BASE_URL = "http://rmb.wealthresources.in/Api/" //Live
//        const val BASE_URL = "http://rmb.wealthresources.in/Api/" //Live
        const val BASE_URL = "http://test.rmb.wealthresources.in/Api/"  //Test
        //        const val BASE_URL = "http://192.168.0.105/scrap/api/"
        const val USERTYPE_INDIVIDUAL = "0"
        const val USERTYPE_APARTMENT = "1"
        const val USERTYPE_CORPORATE = "2"
        const val USERTYPE_VENDOR = "3"
        const val USERTYPE_ADMIN = "4"

        const val PROCESSING_FEES_PERCENT = 5

        const val FB_SUCCESS = "FB_SUCCESS"
        const val FB_CANCEL = "FB_CANCEL"
        const val FB_ERROR = "FB_ERROR"
        const val LINKEDIN_SUCCESS = "LINKEDIN_SUCCESS"
        const val LINKEDIN_CANCEL = "LINKEDIN_CANCEL"

        //LinkedIn
        const val API_KEY_linkedin_CONST = "815wbplzyjdkp4"
        const val SECRET_KEY_linkedin_CONST = "i975Vipr4jd0HW34"
        const val STATE_linkedin_CONST = "8Y2V7RJ7Y18Jv"
        const val REDIRECT_URI_linedin_CONST = "https://www.sundaymobility.com/auth/linkedin"

        //Instagram
        const val CLIENT_ID_INSTA_CONST = "39513cbf3df14d7a84482cf3414621ae"
        const val CLIENT_SECRET_INSTA_CONST = "d4b6f59d88f9455e8e1ec51a5fcf0bca"
        const val CALLBACK_URL_INSTA_CONST = "https://www.sundaymobility.com/"

        //Register
        const val STARTUP_VIEWED = "STARTUP_VIEWED"
        const val USER_INFO = "USER_INFO"
        const val IS_LOGIN = "IS_LOGIN"
        const val USER_LOGIN_VIA = "USER_LOGIN_VIA"
        const val USER_FNAME = "USER_FNAME"
        const val USER_LNAME = "USER_LNAME"
        const val USER_NAME = "USER_NAME"
        const val USER_EMAIL = "USER_EMAIL"
        const val USER_MOBILE = "USER_MOBILE"
        const val SOCIAL_USER_ID = "SOCIAL_USER_ID"
        const val USER_ID = "USER_ID"
        const val USER_TOKEN = "USER_TOKEN"
        const val SOCIAL_USER_TOKEN = "SOCIAL_USER_TOKEN"
        const val INDIVIDUAL = "individual"
        const val APARTMENT = "apartment"
        const val APARTMENT_ASSIGNEE = "apartment_assignee"
        const val CORPORATE = "corporate"
        const val CORPORATE_ASSIGNEE = "corporate_assignee"

        const val FCM_TOKEN = "FCM_TOKEN"
        const val SUBSCRIBE_FCM = "SUBSCRIBE_FCM"
        const val SUBSCRIBE_PRELOGIN = "PRELOGIN"
        const val SUBSCRIBE_POSTLOGIN = "POSTLOGIN"


        const val ENCRYPT_KEY_VALUE = "encryptKey:h3cRb6fJuh/Wb6cqL2jRry/xWWUk1uxb8MTRPfn8FCg="
        var IMAGE_URL_APPEND = "http://test.sundaymobility.com/drapp/"
        var DEEPLINK_URL_PREFIX = "https://recyclemybin.page.link/link"
        var DEEPLINK_URL_SUFFIX_STR = "&apn=com.bidsapp"
        var DEEPLINK_BLOGS_URL = "https://www.recyclemybin.com/Dashboard"
        var TOTAL_FILE_SIZE = 45

        const val TOKEN = "dGhpc19pc19kcl9hcHBfc2VjcmVhdGVfa2V5"
        const val DEVICE_TOKEN = "DEVICE_TOKEN"


        const val CUSTOMERNO = "CUSTOMERNO"
        const val MOBILE_NO = "MOBILE_NO"
        const val USER_TYPE = "USER_TYPE"
        const val LOGIN_TYPE = "LOGIN_TYPE"
        const val EMAIL_ID = "EMAIL_ID"
        const val QUALIFICATION = "QUALIFICATION"
        const val FROM = "FROM"
        const val POSITION_KEY = "POSITION_KEY"
        const val IMAGELIST_KEY = "IMAGELIST_KEY"
        const val NETWORK_ERROR = "NETWORK_ERROR"
        const val SETTINGS = "SETTINGS"
        const val REDIRECTION_URL = "REDIRECTION_URL"
        const val NO_RECORDS = "NO_RECORDS"
        const val RESULT_ACTIVITY_CALLBACK = 12
        const val RESULT_UPLOAD_CALLBACK = 14
        const val ADD_FLAG = "ADD_FLAG"
        const val UPDATE_FLAG = "UPDATE_FLAG"
        const val ACTIVITY_FLAG = "ACTIVITY_FLAG"
        const val PATIENT_ITEM = "PATIENT_ITEM"
        const val PATIENT_ID = "PATIENT_ID"
        const val MAX_DOC_TO_UPLOAD = 5
        val BIDS_FILEPROVIDER = "com.bidsapp.fileprovider"
        const val REQUEST_CAMERA = 600
        const val REQUEST_GALLERY = 700
        const val RC_PHOTO_PICKER_PERM = 123
        const val RC_FILE_PICKER_PERM = 321
        val CUSTOM_REQUEST_CODE = 532
        val STATUS_CANCELLED = "Cancelled"
        val STATUS_INPROGRESS = "In Progress"
        val STATUS_COMPLETED = "Completed"
        val DR_ID = "DR_ID"
        val DEEPLINK_URL_SUFFIX = "deeplink_suffix_url"
        val BLOG_ID = "BLOG_ID"

        var LOADING_VIEW = 0
        var HEADER_VIEW = 1
        var PREDICTION_MORE = "PREDICTION_MORE"
        var EXCEPTION_500 = "Something went wrong, please try again later"
        const val REGEX_GPS_NAME = "^[#.0-9a-zA-Z\\s,-]+\$"

        //            0=processig1=completed,2=cancled3=reshudle,4=dispatched
        var ORDER_PROCESSING = "0"
        var ORDER_COMPLETED = "1"
        var ORDER_CANCELLED = "5"
        var ORDER_RESCHEDULE = "3"
        var ORDER_DISPATCH = "4"

        var ORDER_STATUS_PROCESSING = "Processing"
        var ORDER_STATUS_COMPLETED = "Completed"
        var ORDER_STATUS_CANCELLED = "Cancelled"
        var ORDER_STATUS_DISPATCH = "Dispatched"
        var ORDER_STATUS_RESCHEDULE = "Rescheduled"

        var ORDER_PICKUP_SCREEN = "ORDER_PICKUP_SCREEN"
        var FROM_SCREEN = "FROM_SCREEN"
        var PICKUP_USER_DATA = "PICKUP_USER_DATA"
        var PICKUP_LOGIN_TYPE = "PICKUP_LOGIN_TYPE"
        var PICKUP_ITEMLIST = "PICKUP_ITEMLIST"
        var PICKUP_RAISE_ORDER = "PICKUP_RAISE_ORDER"
        var PICKUP_ITEM_ENTRY = "PICKUP_ITEM_ENTRY"
        var PICKUP_DISPATCH_ORDER = "PICKUP_DISPATCH_ORDER"
        var LOCATION_LAT = "LOCATION_LAT"
        var LOCATION_LNG = "LOCATION_LNG"

        //Fragment TAGS
        var HOME_PAGE = "HOME_PAGE"


    }
}