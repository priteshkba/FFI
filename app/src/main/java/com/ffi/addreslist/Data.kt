package com.ffi.addreslist


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("CustomerName")
    var CustomerName: String = "",
    @SerializedName("PhoneNumber")
    var PhoneNumber: String = "",
    @SerializedName("City")
    var city: String = "",
    @SerializedName("CountryName")
    var countryName: String = "",
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("IsSelected")
    var isSelected: String = "",
    @SerializedName("Pincode")
    var pincode: String = "",
    @SerializedName("StateName")
    var stateName: String = "",
    @SerializedName("Building")
    var Building: String = "",
    @SerializedName("Street")
    var Street: String = "",
    @SerializedName("Landmark")
    var Landmark: String = "",
    @SerializedName("CountryId")
    var CountryId: String = "",
    @SerializedName("StateId")
    var StateId: String = "",
    @SerializedName("CompleteAddress")
    var CompleteAddress: String = ""


) {
    override fun toString(): String {
        return "Data(CustomerName='$CustomerName', PhoneNumber='$PhoneNumber', city='$city', countryName='$countryName', iD='$iD', isSelected='$isSelected', pincode='$pincode', stateName='$stateName', Building='$Building', Street='$Street', Landmark='$Landmark', CountryId='$CountryId', StateId='$StateId', CompleteAddress='$CompleteAddress')"
    }
}