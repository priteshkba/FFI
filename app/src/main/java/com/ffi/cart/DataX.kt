package com.ffi.cart


import com.google.gson.annotations.SerializedName

data class DataX(
    @SerializedName("GST")
    var gST: String = "",
    @SerializedName("GrandTotal")
    var grandTotal: String = "",
    @SerializedName("Items")
    var items: List<ItemX> = listOf(),
    @SerializedName("SelectedAddress")
    var selectedAddress: SelectedAddress = SelectedAddress(),
    @SerializedName("ShippingCharges")
    var shippingCharges: String = "",
    @SerializedName("SubTotal")
    var subTotal: String = "",
    @SerializedName("enoughBalanceInWallet")
    var makePayment: Boolean = false
)