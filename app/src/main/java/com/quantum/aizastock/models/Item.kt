package com.quantum.aizastock.models

data class Item(
    val id: Int,
    val id_user: Int,
    val serial_number: String,
    val description: String,
    val status: String
)