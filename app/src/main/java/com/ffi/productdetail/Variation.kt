package com.ffi.productdetail

import com.ffi.productlist.Media
import com.google.gson.annotations.SerializedName

data class Variation(
    @field:SerializedName("Media")
    var Media: Media = Media(),

    @field:SerializedName("Name")
    var Name: String = "",

    @field:SerializedName("Code")
    var Code: String = "",

    @field:SerializedName("Price")
    var Price: String = "",

    @field:SerializedName("Quantity")
    var Quantity: String = "",

    @field:SerializedName("IsWishlist")
    var IsWishlist: String = "",

    @field:SerializedName("Type")
    var Type: List<Type> = listOf(),

    @field:SerializedName("VariationId")
    var VariationId: String = "",

    @field:SerializedName("DispatchTime")
var DispatchTime: String = ""
)