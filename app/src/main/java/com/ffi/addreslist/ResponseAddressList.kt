package com.ffi.addreslist


import com.google.gson.annotations.SerializedName

data class ResponseAddressList(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)