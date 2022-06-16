package com.ffi.wishlist


import com.ffi.cart.Type
import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("CategoryName")
    var categoryName: String = "",
    @SerializedName("Seller")
    var Seller: String = "",
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("ItemPrice")
    var itemPrice: String = "",
    @SerializedName("Media")
    var media: List<String> = listOf(),
    @SerializedName("ProductId")
    var productId: String = "",
    @SerializedName("SKU")
    var sKU: String = "",
    @SerializedName("Title")
    var title: String = "",
    @SerializedName("Variant")
    var Variant: String = "",
    @SerializedName("VariationId")
    var VariationId: String = "",
    @SerializedName("Type")
    var Type: List<Type> = listOf()

)