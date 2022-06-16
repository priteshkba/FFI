package com.ffi.wallet


import com.google.gson.annotations.SerializedName

data class ParamAddWallet(
    @SerializedName("amount")
    var amount: String? = "",
    @SerializedName("userId")
    var userId: String? = ""
)