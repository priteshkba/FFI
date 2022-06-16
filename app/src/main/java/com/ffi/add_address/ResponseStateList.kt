package com.ffi.add_address

import com.google.gson.annotations.SerializedName

data class ResponseStateList(
    @SerializedName("data")
    var `data`: ArrayList<DataX> = ArrayList(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)