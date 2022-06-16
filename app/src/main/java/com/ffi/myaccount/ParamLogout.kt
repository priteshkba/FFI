package com.ffi.myaccount

import com.google.gson.annotations.SerializedName

class ParamLogout {
    @field:SerializedName("DeviceToken")
    var DeviceToken: String = ""

    override fun toString(): String {
        return "ParamLogout(DeviceToken='$DeviceToken')"
    }


}
