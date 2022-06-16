package com.ffi.my_orderdetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item{

    @SerializedName("OtherReturnReason")
    public String OtherReturnReason;

    @SerializedName("ReasonText")
    public String ReasonText;
    @SerializedName("ReturnReasonId")
    public int ReturnReasonId;

    @SerializedName("ProductId")
    public String productId;
    @SerializedName("VariationId")
    public String variationId;
    @SerializedName("Title")
    public String title;
    @SerializedName("SKU")
    public String sKU;
    @SerializedName("CategoryName")
    public String categoryName;
    @SerializedName("TrackingCode")
    public String trackingCode;
    @SerializedName("TrackingUrl")
    public String trackingUrl;
    @SerializedName("TrackingPartner")
    public Object trackingPartner;
    @SerializedName("OrderStatus")
    public String orderStatus;
    @SerializedName("OrderItemStatusName")
    public String orderItemStatusName;
    @SerializedName("PaymentMethod")
    public String paymentMethod;
    @SerializedName("ItemPrice")
    public String itemPrice;
    @SerializedName("OrderDetailId")
    public String orderDetailId;
    @SerializedName("ReturnValidUpto")
    public String returnValidUpto;
    @SerializedName("IsReturnRequested")
    public int isReturnRequested;
    @SerializedName("IsActive")
    public String isActive;
    @SerializedName("ReviewRating")
    public boolean reviewRating;
    @SerializedName("Variant")
    public String variant;
    @SerializedName("Quantity")
    public String quantity;
    @SerializedName("TotalPrice")
    public String totalPrice;
    @SerializedName("Type")
    public List<Type> type;
    @SerializedName("Media")
    public List<String> media;
}
