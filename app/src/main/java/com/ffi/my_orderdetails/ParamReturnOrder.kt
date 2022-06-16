package com.ffi.my_orderdetails

import com.google.gson.annotations.SerializedName

class ParamReturnOrder (
        @SerializedName("OrderId")
        var OrderId: String = "",

        @SerializedName("OrderDetailId")
        var OrderDetailId: String = ""
) {
    override fun toString(): String {
        return "ParamReturnOrder(OrderId='$OrderId')"
    }
}