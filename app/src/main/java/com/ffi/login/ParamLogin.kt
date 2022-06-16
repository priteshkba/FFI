package com.ffi.login

import com.google.gson.annotations.SerializedName

class ParamLogin {
    @field:SerializedName("MobileNumber")
    var MobileNumber: String = ""

    @field:SerializedName("PhoneCode")
    var PhoneCode: String = ""

    @field:SerializedName("DeviceToken")
    var DeviceToken: String = ""

    override fun toString(): String {
        return "ParamLogin(MobileNumber='$MobileNumber', PhoneCode='$PhoneCode', DeviceToken='$DeviceToken')"
    }


}
