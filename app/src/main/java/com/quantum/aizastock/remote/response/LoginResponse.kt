package com.quantum.aizastock.remote.response

import com.quantum.aizastock.models.User

data class LoginResponse(
    val status: String,
    val user: User
)