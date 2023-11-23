package com.mamits.apnaonlines.userv.model.response


import com.google.gson.annotations.SerializedName

data class NotificationDataItem(@SerializedName("is_read")
                    val isRead: Int = 0,
                    @SerializedName("sent_by")
                    val sentBy: String = "",
                    @SerializedName("updated_at")
                    val updatedAt: String = "",
                    @SerializedName("sent_to")
                    val sentTo: Int = 0,
                    @SerializedName("noti_type")
                    val notiType: String = "",
                    @SerializedName("created_at")
                    val createdAt: String = "",
                    @SerializedName("id")
                    val id: Int = 0,
                    @SerializedName("message")
                    val message: String = "",
                    @SerializedName("status")
                    val status: Int = 0)


data class NotificationListResponse(@SerializedName("data")
                                    val data: ArrayList<NotificationDataItem>?,
                                    @SerializedName("status")
                                    val status: Boolean = false,
                                    @SerializedName("next")
                                    val next: Boolean = false,
                                    @SerializedName("messageId")
                                    val messageId: Int = 0,
                                    @SerializedName("message")
                                    val message: String = "")


