package com.ffi.membership


import com.google.gson.annotations.SerializedName

data class Data(
     @SerializedName("membershipList")
     var membershipList: List<Membership>? = listOf(),

        @SerializedName("ActiveMembershipId")
        var ActiveMembershipId: String? = ""


)