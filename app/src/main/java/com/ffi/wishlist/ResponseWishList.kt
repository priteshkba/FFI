package com.ffi.wishlist


import com.google.gson.annotations.SerializedName

data class ResponseWishList(
    @SerializedName("data")
    var `data`: Data = Data(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: Int = 0
)