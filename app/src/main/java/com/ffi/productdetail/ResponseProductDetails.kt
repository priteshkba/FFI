package com.ffi.productdetail

import com.google.gson.annotations.SerializedName

data class ResponseProductDetails(
    @field:SerializedName("data")
    var data: Data? = Data(),

    @field:SerializedName("message")
    var message: String = "",

    @field:SerializedName("status")
    var status: Int = 0
)