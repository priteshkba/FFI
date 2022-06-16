package com.ffi.home

import com.google.gson.annotations.SerializedName

data class RecordXX(
    @field:SerializedName("Description")
    var Description: String = "",

    @field:SerializedName("ID")
    var ID: String = "",

    @field:SerializedName("Media")
    var Media: String = "",

    @field:SerializedName("Price")
    var Price: String = "",

    @field:SerializedName("SKU")
    var SKU: String = "",

    @field:SerializedName("TagName")
    var TagName: String = "",

    @field:SerializedName("Title")
    var Title: String = "",

    @field:SerializedName("IsWishlist")
    var IsWishlist: String = ""
)