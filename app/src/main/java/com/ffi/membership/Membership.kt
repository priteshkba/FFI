package com.ffi.membership


import com.google.gson.annotations.SerializedName

data class Membership(
    @SerializedName("ID")
    var iD: String? = "",
    @SerializedName("MembershipCost")
    var membershipCost: String? = "",
    @SerializedName("MembershipDescription")
    var membershipDescription: String? = "",
    @SerializedName("MembershipName")
    var membershipName: String? = "",
    @SerializedName("CanPurchase")
    var CanPurchase: String? = "",
    @SerializedName("UserId")
    var UserId: String? = "",
    @SerializedName("isActive")
    var isActive: String? = "",
    @SerializedName("IsWholeseller")
    var IsWholeseller: String? = "",
    @SerializedName("IsRenew")
    var isRenew: String? = "",
    @SerializedName("MembershipEndDate")
    var MembershipEndDate: String? = "",
   @SerializedName("RenewableCost")
    var RenewableCost: String? = "",

    @SerializedName("FirstTimeMembershipCost")
    var FirstTimeMembershipCost: String? = "",

   @SerializedName("TotalUserId")
   var TotalUserId: String? = "")






