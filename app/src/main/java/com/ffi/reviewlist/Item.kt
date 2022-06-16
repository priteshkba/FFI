package com.ffi.reviewlist


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("Comment")
    var comment: String = "",
    @SerializedName("CreatedDateTime")
    var createdDateTime: String = "",
    @SerializedName("FirstName")
    var firstName: String = "",
    @SerializedName("LastName")
    var lastName: String = "",
    @SerializedName("ProfileImage")
    var profileImage: String = "",
    @SerializedName("Rating")
    var rating: String = "",
    @SerializedName("Title")
    var title: String = ""
)