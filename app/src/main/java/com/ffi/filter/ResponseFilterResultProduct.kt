package com.ffi.filter


import com.ffi.productlist.Data
import com.google.gson.annotations.SerializedName

data class ResponseFilterResultProduct(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)