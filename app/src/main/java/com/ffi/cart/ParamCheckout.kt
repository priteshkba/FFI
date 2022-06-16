package com.ffi.cart


import com.google.gson.annotations.SerializedName

data class ParamCheckout(
    @SerializedName("addressId")
    var addressId: String = "",
    @SerializedName("payWithWallet")
    var payWithWallet: Boolean? = false


) {
    override fun toString(): String {
        return "ParamCheckout(addressId='$addressId', payWithWallet=$payWithWallet)"
    }
}