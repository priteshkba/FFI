package com.ffi.wishlist


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("Items")
    var items: ArrayList<Item> = ArrayList()
)