package com.ffi.my_orders


import com.google.gson.annotations.SerializedName

data class ResponseSearchOrder(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)