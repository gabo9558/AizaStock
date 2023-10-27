package com.quantum.aizastock.remote.response

import com.quantum.aizastock.models.DataStock

data class StockResponse(
    val status: String,
    val data: DataStock
)