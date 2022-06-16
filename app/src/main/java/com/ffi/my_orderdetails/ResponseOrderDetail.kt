package com.ffi.my_orderdetails


import com.google.gson.annotations.SerializedName

data class ResponseOrderDetail(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)