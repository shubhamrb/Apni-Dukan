package com.mamits.apnaonlines.userv.model


import com.google.gson.annotations.SerializedName

data class Bundle(@SerializedName("mMap")
                  val mMap: MMap
)


data class Data(@SerializedName("id")
                val id: String = "",
                @SerializedName("type")
                val type: String = "")


data class MMap(@SerializedName("google.original_priority")
                val googleOriginalPriority: String = "",
                @SerializedName("google.sent_time")
                val googleSentTime: Long = 0,
                @SerializedName("google.delivered_priority")
                val googleDeliveredPriority: String = "",
                @SerializedName("gcm.notification.title")
                val gcmNotificationTitle: String = "",
                @SerializedName("google.c.sender.id")
                val googleCSenderId: String = "",
                @SerializedName("type")
                val type: String = "",
                @SerializedName("google.message_id")
                val googleMessageId: String = "",
                @SerializedName("collapse_key")
                val collapseKey: String = "",
                @SerializedName("gcm.notification.e")
                val gcmNotificationE: String = "",
                @SerializedName("google.c.a.e")
                val googleCAE: String = "",
                @SerializedName("google.ttl")
                val googleTtl: Int = 0,
                @SerializedName("from")
                val from: String = "",
                @SerializedName("id")
                val id: String = "",
                @SerializedName("gcm.notification.body")
                val gcmNotificationBody: String = "")


data class FcmNotificationObj(@SerializedName("data")
                              val data: Data,
                              @SerializedName("bundle")
                              val bundle: Bundle
)


