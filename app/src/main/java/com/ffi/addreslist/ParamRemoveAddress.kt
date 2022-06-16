package com.ffi.addreslist


import com.google.gson.annotations.SerializedName

data class ParamRemoveAddress(
    @SerializedName("addressId")
    var addressId: String = ""


) {
    override fun toString(): String {
        return "ParamCheckout(addressId='$addressId')"
    }
}