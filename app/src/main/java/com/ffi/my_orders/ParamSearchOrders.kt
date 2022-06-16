package com.ffi.my_orders


import com.google.gson.annotations.SerializedName

data class ParamSearchOrders(
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("orderId")
    var orderId: String = "",
    @SerializedName("page")
    var page: String = ""


) {
    override fun toString(): String {
        return "ParamSearchOrders(limit='$limit', orderId='$orderId', page='$page')"
    }
}