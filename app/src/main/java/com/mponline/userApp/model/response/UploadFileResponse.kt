package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class UploadImgData(@SerializedName("ext")
                val ext: String = "",
                @SerializedName("url")
                val url: String = "")


data class UploadFileResponse(@SerializedName("data")
                              val data: UploadImgData,
                              @SerializedName("messageId")
                              val messageId: Int = 0,
                              @SerializedName("message")
                              val message: String = "",
                              @SerializedName("status")
                              val status: Boolean = false)


