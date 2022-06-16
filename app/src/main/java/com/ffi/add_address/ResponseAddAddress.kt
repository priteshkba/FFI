package com.ffi.add_address


import com.google.gson.annotations.SerializedName

data class ResponseAddAddress(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("addressId")
    var addressId: String = ""
)