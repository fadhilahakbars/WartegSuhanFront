package com.netlab.suhan.data

data class Transaction(
    val orderId: Int,
    val name: String,
    val date: String,
    val qty: Int,
    val total: Double
)
