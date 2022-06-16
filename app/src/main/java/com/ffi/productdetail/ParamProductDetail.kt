package com.ffi.productdetail

import com.google.gson.annotations.SerializedName

class ParamProductDetail {

    @field:SerializedName("productId")
    var productId: String = ""

    override fun toString(): String {
        return "ParamProductDetail(productId='$productId')"
    }
}
