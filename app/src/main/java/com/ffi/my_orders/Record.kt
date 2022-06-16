package com.ffi.my_orders


import com.google.gson.annotations.SerializedName

data class Record(
    @SerializedName("DateTime")
    var dateTime: String? = "",
    @SerializedName("GrandTotal")
    var grandTotal: String? = "",
    @SerializedName("TotalAmount")
    var TotalAmount: String? = "",
    @SerializedName("CustomerName")
    var CustomerName: String? = "",
    @SerializedName("ResellerName")
    var ResellerName: String? = "",
    @SerializedName("OrderId")
    var orderId: String? = "",
    @SerializedName("OrderStatus")
    var orderStatus: String? = "",
    @SerializedName("OrderStatusName")
    var orderStatusName: String? = "",
    @SerializedName("PaymentStatus")
    var paymentStatus: String? = "",
    @SerializedName("PaymentStatusName")
    var paymentStatusName: String? = "",
    @SerializedName("TotalItems")
    var totalItems: String? = "",
    @SerializedName("MembershipDiscount")
    var MembershipDiscount: String? = "",

    @SerializedName("TrackingCode")
    var TrackingCode: String? = "",
    @SerializedName("TrackingUrl")
    var TrackingUrl: String? = "",
    @SerializedName("TrackingPartner")
    var TrackingPartner: String? = ""

)