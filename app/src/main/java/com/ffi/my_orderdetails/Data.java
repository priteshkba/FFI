package com.ffi.my_orderdetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data{
    @SerializedName("OrderId")
    public String orderId;
    @SerializedName("CustomerName")
    public String customerName;
    @SerializedName("ResellerName")
    public String resellerName;
    @SerializedName("FromName")
    public String fromName;
    @SerializedName("FromContact")
    public String fromContact;
    @SerializedName("ResellerContact")
    public String resellerContact;
    @SerializedName("Building")
    public String building;
    @SerializedName("Street")
    public String street;
    @SerializedName("Landmark")
    public String landmark;
    @SerializedName("City")
    public String city;
    @SerializedName("Pincode")
    public String pincode;
    @SerializedName("State")
    public String state;
    @SerializedName("Country")
    public String country;
    @SerializedName("CompleteAddress")
    public String completeAddress;
    @SerializedName("Items")
    public List<Item> items;
    @SerializedName("TotalQuantity")
    public int totalQuantity;
    @SerializedName("PaymentStatus")
    public String paymentStatus;
    @SerializedName("PaymentStatusName")
    public String paymentStatusName;
    @SerializedName("OrderStatus")
    public String orderStatus;
    @SerializedName("OrderStatusName")
    public String orderStatusName;
    @SerializedName("SubTotal")
    public String subTotal;
    @SerializedName("GST")
    public String gST;
    @SerializedName("ShippingCharge")
    public String shippingCharge;
    @SerializedName("GrandTotal")
    public String grandTotal;
    @SerializedName("MembershipDiscount")
    public String membershipDiscount;
    @SerializedName("CreatedDateTime")
    public String createdDateTime;
    @SerializedName("Notes")
    public List<Note> notes;
}
