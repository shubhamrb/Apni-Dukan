package com.mponline.userApp.model.response

data class RegisterResponse(val data: RegisterData,
                            val messageId: Int = 0,
                            val message: String = "",
                            val status: Boolean = false)


data class User(val phone: String = "",
                val name: String = "",
                val id: Int = 0)


data class RegisterData(val user: User,
                val token: String = "")


