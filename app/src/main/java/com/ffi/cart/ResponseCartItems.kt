package com.ffi.cart

import com.google.gson.annotations.SerializedName

data class ResponseCartItems(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)