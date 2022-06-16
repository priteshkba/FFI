package com.ffi.wallet


import com.google.gson.annotations.SerializedName

data class ResponseWallet(
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("walletAmount")
    var walletAmount: String? = ""
)