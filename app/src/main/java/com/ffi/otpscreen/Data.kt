package com.ffi.otpscreen

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Data(
    @Expose
    @SerializedName("BasicUrls")
    var BasicUrls: BasicUrls = BasicUrls(),

    @Expose
    @SerializedName("CreatedDateTime")
    var CreatedDateTime: String = "",

    @Expose
    @SerializedName("DefaultAddressId")
    var DefaultAddressId: String = "",

    @Expose
    @SerializedName("EmailAddress")
    var EmailAddress: String = "",

    @Expose
    @SerializedName("FirstName")
    var FirstName: String = "",

    @Expose
    @SerializedName("LastName")
    var LastName: String = "",

    @Expose
    @SerializedName("MobileNumber")
    var MobileNumber: String = "",

    @Expose
    @SerializedName("PhoneCode")
    var PhoneCode: String = "",

    @Expose
    @SerializedName("UserId")
    var UserId: String = "",

    @Expose
    @SerializedName("UserToken")
    var UserToken: String = "",

    @Expose
    @SerializedName("Media")
    var Media: String = ""

)