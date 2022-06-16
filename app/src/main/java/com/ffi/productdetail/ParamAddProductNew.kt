package com.ffi.productdetail

import com.google.gson.annotations.SerializedName

/*IMORTANT NOTE:
   status = 1 ==> update
   status = 0 ==> add*/

data class ParamAddProductNew (

    @SerializedName("productId")
    var productId: String = "",

    @SerializedName("variationId")
    var variationId: String = "",

    @SerializedName("quantity")
    var quantity: String = "1",

    @SerializedName("status")
    var status: String = "0",

    @SerializedName("addFromWishlist")
    var addFromWishlist: String = "0"

)