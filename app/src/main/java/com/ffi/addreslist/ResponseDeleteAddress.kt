package com.ffi.addreslist


import com.google.gson.annotations.SerializedName

data class ResponseDeleteAddress(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)