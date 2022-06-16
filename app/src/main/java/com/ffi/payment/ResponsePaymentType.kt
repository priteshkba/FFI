package com.ffi.payment


import com.google.gson.annotations.SerializedName

data class ResponsePaymentType(
    @SerializedName("data")
    var `data`: ArrayList<DataX>? = ArrayList(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("status")
    var status: Int? = 0
)