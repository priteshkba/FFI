package com.ffi.productdetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Comments(

	@SerializedName("FirstName")
	@Expose
	var firstName: String? = "",

	@SerializedName("LastName")
	@Expose
	var lastName: String? = "",

	@SerializedName("ProfileImage")
	@Expose
	var profileImage: String? = "",

	@SerializedName("Rating")
	@Expose
	var rating: String? = "",
	@SerializedName("Comment")
	@Expose
	var comment: String? = "",

	@SerializedName("CreatedDateTime")
	@Expose
	var createdDateTime: String? = ""
)