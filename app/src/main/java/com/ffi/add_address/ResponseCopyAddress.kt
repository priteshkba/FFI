package com.ffi.add_address


import com.google.gson.annotations.SerializedName

data class ResponseCopyAddress(
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("data")
    var data: DataId = DataId()
)