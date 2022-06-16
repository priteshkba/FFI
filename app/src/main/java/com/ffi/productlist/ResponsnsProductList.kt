package com.ffi.productlist

import com.google.gson.annotations.SerializedName

data class ResponsnsProductList(
    @field:SerializedName("data")
    var `data`: Data = Data(),

    @field:SerializedName("message")
    var message: String = "",

    @field:SerializedName("status")
    var status: Int = 0
)