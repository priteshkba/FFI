package com.ffi.model


import com.google.gson.annotations.SerializedName

data class ResponseGetSharedData(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)