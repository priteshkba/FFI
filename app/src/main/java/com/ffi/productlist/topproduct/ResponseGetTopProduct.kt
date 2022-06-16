package com.ffi.productlist.topproduct


import com.google.gson.annotations.SerializedName

data class ResponseGetTopProduct(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)