package com.ffi.add_address


import com.google.gson.annotations.SerializedName

data class ParamAddAddress(
    @SerializedName("addressId")
    var addressId: String = "0",
    @SerializedName("customerName")
    var customerName: String = "0",
    @SerializedName("mobileNumber")
    var mobileNumber: String = "0",
    @SerializedName("building")
    var building: String = "",
    @SerializedName("street")
    var street: String = "",
    @SerializedName("landmark")
    var landmark: String = "",
    @SerializedName("city")
    var city: String = "",
    @SerializedName("countryId")
    var countryId: String = "",
    @SerializedName("pincode")
    var pincode: String = "",
    @SerializedName("stateId")
    var stateId: String = "",

    @SerializedName("completeAddress")
    var completeAddress: String = "",
    @SerializedName("CustomerContact")
    var CustomerContact: String = "",

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
        return "ParamAddAddress(addressId='$addressId', customerName='$customerName', mobileNumber='$mobileNumber', building='$building', street='$street', landmark='$landmark', city='$city', countryId='$countryId', pincode='$pincode', stateId='$stateId', completeAddress='$completeAddress', CustomerContact='$CustomerContact', ResellerName='$ResellerName', ResellerContact='$ResellerContact', fromname='$fromname', fromcontact='$fromcontact')"
    }
}