package com.ffi.wallet


import com.google.gson.annotations.SerializedName

data class ParamWallet(
    @SerializedName("userId")
    var userId: String = ""
) {
    override fun toString(): String {
        return "ParamWallet(userId='$userId')"
    }
}