package com.ffi.wallet


import com.google.gson.annotations.SerializedName

data class ParamWalletHistory(
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("Page")
    var page: String = "",
    @SerializedName("userId")
    var userId: String = ""
) {
    override fun toString(): String {
        return "ParamWalletHistory(limit='$limit', page='$page', userId='$userId')"
    }
}