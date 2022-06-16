package com.ffi.model


import com.google.gson.annotations.SerializedName

class ParamAddREmoveWishItem(
    @field:SerializedName("productId")
    var productId: String = "",

    @field:SerializedName("variationId")
    var variationId: String = "",

    @field:SerializedName("status")
    var status: String = ""
) {
    override fun toString(): String {
        return "ParamAddREmoveWishItem(productId='$productId', variationId='$variationId', status='$status')"
    }
}