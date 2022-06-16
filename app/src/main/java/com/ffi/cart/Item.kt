package com.ffi.cart

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("AvailableQuantity")
    var availableQuantity: String = "",
    @SerializedName("Seller")
    var Seller: String = "",
    @SerializedName("CategoryName")
    var categoryName: String = "",
    @SerializedName("ID")
    var iD: String = "",
    @SerializedName("ItemPrice")
    var itemPrice: String = "",
    @SerializedName("Media")
    var media: List<String> = listOf(),
    @SerializedName("ProductId")
    var productId: String = "",
    @SerializedName("Quantity")
    var quantity: String = "",
    @SerializedName("SKU")
    var sKU: String = "",
    @SerializedName("OutOfStock")
    var OutOfStock: Boolean = false,
    @SerializedName("Title")
    var title: String = "",
    @SerializedName("TotalPrice")
    var totalPrice: String = "",
    @SerializedName("Type")
    var type: List<Type> = listOf(),
    @SerializedName("Variant")
    var variant: String = "",
    @SerializedName("VariationId")
    var variationId: String = ""
)