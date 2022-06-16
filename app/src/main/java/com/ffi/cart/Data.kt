package com.ffi.cart


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("GrandTotal")
    var grandTotal: String = "",
    @SerializedName("GST")
    var GST: String = "",
    @SerializedName("ShippingCharges")
    var ShippingCharges: String = "",
    @SerializedName("Items")
    var items: ArrayList<Item> = ArrayList(),
    @SerializedName("SubTotal")
    var subTotal: String = "",
    @SerializedName("walletBalance")
    var walletBalance: String = "",
    @SerializedName("MembershipDiscount")
    var MembershipDiscount: String = ""
) {
    override fun toString(): String {
        return "Data(grandTotal='$grandTotal', GST='$GST', ShippingCharges='$ShippingCharges', items=$items, subTotal='$subTotal', walletBalance='$walletBalance')"
    }
}