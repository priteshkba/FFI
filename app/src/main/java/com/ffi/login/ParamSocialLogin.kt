package com.ffi.login

import com.google.gson.annotations.SerializedName

class ParamSocialLogin {
    @field:SerializedName("emailaddress")
    var EmailAddress: String = ""

    @field:SerializedName("FacebookId")
    var FacebookId: String = ""

    @field:SerializedName("GoogleId")
    var GoogleId: String = ""

    @field:SerializedName("FirstName")
    var FirstName: String = ""

    @field:SerializedName("LastName")
    var LastName: String = ""
    override fun toString(): String {
        return "ParamSocialLogin(EmailAddress='$EmailAddress', FacebookId='$FacebookId', GoogleId='$GoogleId', FirstName='$FirstName', LastName='$LastName')"
    }


}
