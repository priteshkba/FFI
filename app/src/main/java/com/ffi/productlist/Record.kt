package com.ffi.productlist

import com.google.gson.annotations.SerializedName

data class Record(
    @field:SerializedName("Price")
    var Price: String = "",

    @field:SerializedName("ID")
    var ID: String = "",

    @field:SerializedName("SKU")
    var SKU: String = "",

    @field:SerializedName("Title")
    var Title: String = "",

    @field:SerializedName("Description")
    var Description: String = "",

    @field:SerializedName("Media")
    var Media: List<Media> = listOf()


) {
    override fun toString(): String {
        return "Record(Price='$Price', ID='$ID', SKU='$SKU', Title='$Title', Description='$Description', Media=$Media)"
    }
}