package com.ffi.my_orderdetails


import com.google.gson.annotations.SerializedName

data class ParamOrderDeatils(
    @SerializedName("orderId")
    var orderId: String = ""
) {
    override fun toString(): String {
        return "ParamOrderDeatils(orderId='$orderId')"
    }
}