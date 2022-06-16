package com.ffi.cart


import com.google.gson.annotations.SerializedName

data class ParamCartItemremove(
    @SerializedName("itemId")
    var itemId: String = ""
) {
    override fun toString(): String {
        return "ParamCartItemremove(itemId='$itemId')"
    }
}