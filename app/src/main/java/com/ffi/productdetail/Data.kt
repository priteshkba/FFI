package com.ffi.productdetail

import com.ffi.productlist.Media
import com.google.gson.annotations.SerializedName

data class Data(
    @field:SerializedName("CategoryName")
    var CategoryName: String = "",

    @field:SerializedName("Description")
    var Description: String = "",

    @field:SerializedName("ProductType")
    var ProductType: String = "",

    @field:SerializedName("VariationId")
    var VariationId: String = "",

    @field:SerializedName("WishlistVariationIds")
    var WishlistVariationIds: ArrayList<String>? = ArrayList(),

    @field:SerializedName("IsWishlist")
    var IsWishlist: String = "",

    @field:SerializedName("Media")
    var Media: List<Media> = listOf(),

    @field:SerializedName("Price")
    var Price: String = "",

    @field:SerializedName("Title")
    var Title: String = "",

    @field:SerializedName("MembershipDiscount")
    var MembershipDiscount: String = "",

    @field:SerializedName("variation")
    var variation: List<Variation>? = listOf(),

    @field:SerializedName("VariationList")
    var VariationList: ArrayList<VariationList>? = ArrayList(),

    @field:SerializedName("ReviewRating")
    var ReviewRating: ReviewRating = ReviewRating(),

    @field:SerializedName("DispatchTime")
    var DispatchTime: String = ""


) {
    override fun toString(): String {
        return "Data(CategoryName='$CategoryName', Description='$Description', ProductType='$ProductType', VariationId='$VariationId', WishlistVariationIds=$WishlistVariationIds, IsWishlist='$IsWishlist', Media=$Media, Price='$Price', Title='$Title', MembershipDiscount='$MembershipDiscount', variation=$variation, VariationList=$VariationList, ReviewRating=$ReviewRating)"
    }
}