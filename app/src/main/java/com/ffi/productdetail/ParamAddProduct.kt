package com.ffi.productdetail

import com.google.gson.annotations.SerializedName

class ParamAddProduct {

    @field:SerializedName("productId")
    var productId: String = ""

    @field:SerializedName("variationId")
    var variationId: String = ""

    @field:SerializedName("quantity")
    var quantity: String = "1"

    @field:SerializedName("status")
    var status: String = "0"

    @field:SerializedName("addFromWishlist")
    var addFromWishlist: String = "0"

    override fun toString(): String {
        return "ParamAddProduct(productId='$productId', variationId='$variationId', quantity='$quantity', status='$status')"
    }
    /*IMORTANT NOTE:
    status = 1 ==> update
    status = 0 ==> add*/


}
