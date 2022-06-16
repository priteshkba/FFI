package com.ffi.cart


import com.google.gson.annotations.SerializedName

data class SelectedAddress(
    @SerializedName("AddressLine1")
    var addressLine1: String = "",
    @SerializedName("AddressLine2")
    var addressLine2: String = "",
    @SerializedName("City")
    var city: String = "",
    @SerializedName("CountryId")
    var countryId: String = "",
    @SerializedName("CountryName")
    var countryName: String = "",
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("Pincode")
    var pincode: String = "",
    @SerializedName("StateId")
    var stateId: String = "",
    @SerializedName("StateName")
    var stateName: String = ""
)