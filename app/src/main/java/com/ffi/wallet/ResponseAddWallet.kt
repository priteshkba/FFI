package com.ffi.wallet


import com.google.gson.annotations.SerializedName

data class ResponseAddWallet(
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("paymentCancelUrl")
    var paymentCancelUrl: String? = "",
    @SerializedName("paymentErrorUrl")
    var paymentErrorUrl: String? = "",
    @SerializedName("paymentProcessUrl")
    var paymentProcessUrl: String? = "",
    @SerializedName("paymentSuccessUrl")
    var paymentSuccessUrl: String? = "",
    @SerializedName("status")
    var status: Int? = 0
)