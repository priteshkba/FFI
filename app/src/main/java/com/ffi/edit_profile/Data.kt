package com.ffi.edit_profile


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("CreatedDateTime")
    var createdDateTime: String = "",
    @SerializedName("DefaultAddressId")
    var defaultAddressId: String = "",
    @SerializedName("email")
    var emailAddress: String = "",
    @SerializedName("FirstName")
    var firstName: String = "",
    @SerializedName("Gender")
    var gender: String = "",
    @SerializedName("LastName")
    var lastName: String = "",
    @SerializedName("Media")
    var media: String = "",
    @SerializedName("MobileNumber")
    var mobileNumber: String = "",
    @SerializedName("PhoneCode")
    var phoneCode: String = "",
    @SerializedName("UserId")
    var userId: String = ""
)