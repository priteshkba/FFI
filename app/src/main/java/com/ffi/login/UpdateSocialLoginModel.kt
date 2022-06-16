package com.ffi.login

import com.google.gson.annotations.SerializedName

class UpdateSocialLoginModel {
    @field:SerializedName("Gender")
    var Gender: String = ""

    @field:SerializedName("LastName")
    var LastName: String = ""

    @field:SerializedName("FirstName")
    var FirstName: String = ""

}
