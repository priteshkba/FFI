package com.ffi.productlist.topproduct


import com.ffi.productlist.Media
import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("Description")
    var description: String = "",
    @SerializedName("ID")
    var iD: String = "",
    /*  @SerializedName("IsWishlist")
      var isWishlist: String = "",*/
    @SerializedName("Media")
    var media: List<Media> = listOf(),
    @SerializedName("Price")
    var price: String = "",
    @SerializedName("SKU")
    var sKU: String = "",
    /*  @SerializedName("TagName")
      var tagName: String = "",*/
    @SerializedName("Title")
    var title: String = ""
)