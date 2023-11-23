package com.mamits.apnaonlines.userv.model.response


import com.google.gson.annotations.SerializedName

data class DocumentData(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("file")
    val file: String = "",
    @SerializedName("file_name")
    val file_name: String = "",
)


data class GetDocumentResponse(
    @SerializedName("data")
    val data: List<DocumentData>,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("next")
    val next: Boolean = false,
    @SerializedName("messageId")
    val messageId: Int = 0,
    @SerializedName("message")
    val message: String = ""
)


