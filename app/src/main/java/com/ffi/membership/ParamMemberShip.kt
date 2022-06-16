package com.ffi.membership


import com.google.gson.annotations.SerializedName

data class ParamMemberShip(
    @SerializedName("userId")
    var userId: Int? = 0
)