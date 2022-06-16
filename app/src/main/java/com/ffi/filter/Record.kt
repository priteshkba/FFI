package com.ffi.filter


import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("CategoryName")
    var categoryName: String = "",
    @SerializedName("Description")
    var description: String = "",
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("IsWishlist")
    var isWishlist: String = "",
    @SerializedName("Media")
    var media: List<Media> = listOf(),
    @SerializedName("MediaFolder")
    var mediaFolder: String = "",
    @SerializedName("Price")
    var price: String = "",
    @SerializedName("Quantity")
    var quantity: String = "",
    @SerializedName("Title")
    var title: String = "",
    @SerializedName("VariantionPrice")
    var variantionPrice: String = "",
    @SerializedName("VariationId")
    var variationId: String = ""
)