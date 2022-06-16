package com.ffi.model


import com.google.gson.annotations.SerializedName

data class ParamAddShareddata(
    @SerializedName("referenceId")
    var referenceId: String = "",
    @SerializedName("typeId")
    var typeId: String = ""


) {
    override fun toString(): String {
        return "ParamAddShareddata(referenceId='$referenceId', typeId='$typeId')"
    }
}