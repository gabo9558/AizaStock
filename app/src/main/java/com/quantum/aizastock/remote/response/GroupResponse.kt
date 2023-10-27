package com.quantum.aizastock.remote.response

import com.quantum.aizastock.models.Group

data class GroupResponse(
    val status: String,
    val data: Group
)