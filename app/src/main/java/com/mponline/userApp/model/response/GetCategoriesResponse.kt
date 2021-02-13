package com.mponline.userApp.model.response


import com.google.gson.annotations.SerializedName

data class CategoryDataItem(@SerializedName("image")
                    val image: String = "",
                    @SerializedName("name")
                    val name: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("banner_image")
                    val bannerImage: String = "")


data class GetCategoriesResponse(@SerializedName("data")
                                 val data: ArrayList<CategorylistItem>?,
                                 @SerializedName("success")
                                 val success: Boolean = false,
                                 @SerializedName("messageId")
                                 val messageId: Int = 0,
                                 @SerializedName("message")
                                 val message: String = "")


