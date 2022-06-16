package com.ffi.home


import com.google.gson.annotations.SerializedName

data class ResponseProductSearch(
    @SerializedName("data")
    var `data`: DataX = DataX(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)