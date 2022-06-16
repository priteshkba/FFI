package com.ffi.membership


import com.google.gson.annotations.SerializedName

data class ParamAddMeberShip(
    @SerializedName("membershipId")
    var membershipId: Int? = 0,
    @SerializedName("userId")
    var userId: Int? = 0
)