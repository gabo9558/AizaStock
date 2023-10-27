package com.quantum.aizastock.remote.response

import com.quantum.aizastock.models.Item

data class DataConsultResponse(
    val status: String,
    val data: Item
)