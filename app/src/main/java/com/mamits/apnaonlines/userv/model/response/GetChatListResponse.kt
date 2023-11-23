package com.mamits.apnaonlines.userv.model.response


import com.google.gson.annotations.SerializedName

data class GetChatListResponse(@SerializedName("data")
                               val data: ArrayList<ChatListDataItem>?,
                               @SerializedName("status")
                               val status: Boolean = false,
                               @SerializedName("next")
                               val next: Boolean = false,
                               @SerializedName("messageId")
                               val messageId: Int = 0,
                               @SerializedName("message")
                               val message: String = "")


data class ChatListDataItem(@SerializedName("to_user")
                            val toUser: String = "",
                            @SerializedName("attachment")
                            val attachment: String = "",
                            @SerializedName("updated_at")
                            val updatedAt: String = "",
                            @SerializedName("file_type")
                            val fileType: String = "",
                            @SerializedName("created_at")
                            val createdAt: String = "",
                            @SerializedName("id")
                            val id: String = "",
                            @SerializedName("message")
                            val message: String = "",
                            @SerializedName("order_id")
                            val orderId: String = "",
                            @SerializedName("deleted_at")
                            val deletedAt: String = "",
                            @SerializedName("from_user")
                            val fromUser: String = "")


