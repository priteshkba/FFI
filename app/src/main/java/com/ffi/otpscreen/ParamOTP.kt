package com.ffi.otpscreen

import com.google.gson.annotations.SerializedName

class ParamOTP {
    @field:SerializedName("MobileNumber")
    var MobileNumber: String = ""

    @field:SerializedName("PhoneCode")
    var PhoneCode: String = ""

    @field:SerializedName("OTP")
    var OTP: String = ""

    @field:SerializedName("EmailAddress")
    var EmailAddress: String = ""

    @field:SerializedName("FirstName")
    var FirstName: String = ""

    @field:SerializedName("LastName")
    var LastName: String = ""

    @field:SerializedName("FacebookId")
    var FacebookId: String = ""

    @field:SerializedName("GoogleId")
    var GoogleId: String = ""

    @field:SerializedName("DeviceToken")
    var DeviceToken: String = ""

    override fun toString(): String {
        return "ParamOTP(MobileNumber='$MobileNumber', PhoneCode='$PhoneCode', OTP='$OTP', EmailAddress='$EmailAddress', FirstName='$FirstName', LastName='$LastName', FacebookId='$FacebookId', GoogleId='$GoogleId', DeviceToken='$DeviceToken')"
    }


}
