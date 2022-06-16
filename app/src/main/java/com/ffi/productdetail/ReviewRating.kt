package com.ffi.productdetail

import com.google.gson.annotations.SerializedName

data class ReviewRating (

	@SerializedName("OverAllRating")
	var overAllRating : String="",

	@SerializedName("TotalRating")
	var TotalRating : Int=0,

	@SerializedName("RatingList")
	var ratingList : List<RatingList> = listOf(),

	@SerializedName("Comments")
	var comments : List<Comments> = listOf()
)