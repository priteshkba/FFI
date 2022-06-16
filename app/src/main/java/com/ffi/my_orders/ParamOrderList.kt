package com.ffi.my_orders


import com.google.gson.annotations.SerializedName

data class ParamOrderList(
    @SerializedName("limit")
    var limit: String = "",
    @SerializedName("orderStatus")
    var orderStatus: String = "",
    @SerializedName("page")
    var page: String = ""


) {
    override fun toString(): String {
        return "ParamOrderList(limit='$limit', orderStatus='$orderStatus', page='$page')"
    }
}