package com.ffi.membership


import com.google.gson.annotations.SerializedName

data class ResponseMeberShip(
    @SerializedName("data")
    var `data`: Data? = Data(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("status")
    var status: Int? = 0
)