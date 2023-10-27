package com.quantum.aizastock.models

data class Item(
    val id: Int,
    val id_user: Int,
    val IMEI: String,
    val description: String,
    val status: String
)