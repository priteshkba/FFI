package com.ffi.cart


import com.google.gson.annotations.SerializedName

data class ResponseCheckout(
    @SerializedName("data")
    var `data`: DataX = DataX(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)