package com.ffi.add_address


import com.google.gson.annotations.SerializedName

data class ResponseCountryList(
    @SerializedName("data")
    var `data`: ArrayList<Data> = ArrayList(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)