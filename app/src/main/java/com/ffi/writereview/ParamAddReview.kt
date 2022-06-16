package com.ffi.writereview


import com.google.gson.annotations.SerializedName

data class ParamAddReview(
    @SerializedName("Comment")
    var comment: String = "",
    @SerializedName("ProductId")
    var productId: String = "",
    @SerializedName("RatingId")
    var ratingId: String = "",
    @SerializedName("Title")
    var title: String = "",
    @SerializedName("OrderId")
    var OrderId: String = ""
) {
    override fun toString(): String {
        return "ParamAddReview(comment='$comment', productId='$productId', ratingId='$ratingId', title='$title', OrderId='$OrderId')"
    }
}