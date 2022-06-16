package com.ffi.reviewlist


import com.ffi.productdetail.RatingList
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("OverAllRating")
    var overAllRating: Double = 0.0,

    @SerializedName("TotalRating")
    var TotalRating: Int = 0,

    @SerializedName("RatingList")
    var ratingList: List<RatingList> = listOf(),

    @SerializedName("Items")
    var items: List<Item> = listOf(),

    @SerializedName("TotalPage")
    var totalPage: Int = 0
)