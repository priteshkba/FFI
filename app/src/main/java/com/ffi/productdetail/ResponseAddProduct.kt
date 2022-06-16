package com.ffi.productdetail


import com.ffi.cart.Data
import com.google.gson.annotations.SerializedName


data class ResponseAddProduct(
    @field:SerializedName("message")
    var message: String = "",
    @field:SerializedName("status")
    var status: Int = 0,
    @SerializedName("data")
    var `data`: Data = Data()
)