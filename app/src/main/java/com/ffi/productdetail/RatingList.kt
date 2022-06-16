package com.ffi.productdetail

import com.google.gson.annotations.SerializedName

data class RatingList(
    @SerializedName("Rating") var rating: Float = 0f,
    @SerializedName("Percentage") var Percentage: Int = 0
)