package com.ffi.add_address


import com.google.gson.annotations.SerializedName

data class ParamCopyAddress(
    @SerializedName("addressId")
    var addressId: String = "",
    @SerializedName("completeAddress")
    var completeAddress: String = "",
    @SerializedName("customerName")
    var CustomerName: String = "",

    @SerializedName("resellerName")
    var ResellerName: String = "",
    @SerializedName("resellerContact")
    var ResellerContact: String = "",

    @SerializedName("fromName")
    var fromname: String = "",
    @SerializedName("fromContact")
    var fromcontact: String = ""
) {
    override fun toString(): String {
        return "ParamCopyAddress(addressId='$addressId', completeAddress='$completeAddress', CustomerName='$CustomerName', ResellerName='$ResellerName', ResellerContact='$ResellerContact', fromname='$fromname', fromcontact='$fromcontact')"
    }
}