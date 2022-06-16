package com.ffi.cart


import com.google.gson.annotations.SerializedName

data class ResponseCartItemRemove(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("data")
    var `data`: Data = Data()
)