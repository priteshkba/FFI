package com.ffi.my_orderdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class OrderDetailResponse {

    @Expose
    @SerializedName("data")
    private DataEntity data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("status")
    private int status;

    public DataEntity getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public static class DataEntity {
        @Expose
        @SerializedName("Notes")
        private List<NotesEntity> notes;
        @Expose
        @SerializedName("CreatedDateTime")
        private String createddatetime;
        @Expose
        @SerializedName("MembershipDiscount")
        private String membershipdiscount;
        @Expose
        @SerializedName("GrandTotal")
        private String grandtotal;
        @Expose
        @SerializedName("ShippingCharge")
        private String shippingcharge;
        @Expose
        @SerializedName("GST")
        private String gst;
        @Expose
        @SerializedName("SubTotal")
        private String subtotal;
        @Expose
        @SerializedName("OrderStatusName")
        private String orderstatusname;
        @Expose
        @SerializedName("OrderStatus")
        private String orderstatus;
        @Expose
        @SerializedName("PaymentStatusName")
        private String paymentstatusname;
        @Expose
        @SerializedName("PaymentStatus")
        private String paymentstatus;
        @Expose
        @SerializedName("TotalQuantity")
        private int totalquantity;
        @Expose
        @SerializedName("Items")
        private List<ItemsEntity> items;
        @Expose
        @SerializedName("CompleteAddress")
        private String completeaddress;
        @Expose
        @SerializedName("Country")
        private String country;
        @Expose
        @SerializedName("State")
        private String state;
        @Expose
        @SerializedName("Pincode")
        private String pincode;
        @Expose
        @SerializedName("City")
        private String city;
        @Expose
        @SerializedName("Landmark")
        private String landmark;
        @Expose
        @SerializedName("Street")
        private String street;
        @Expose
        @SerializedName("Building")
        private String building;
        @Expose
        @SerializedName("ResellerContact")
        private String resellercontact;
        @Expose
        @SerializedName("FromContact")
        private String fromcontact;
        @Expose
        @SerializedName("FromName")
        private String fromname;
        @Expose
        @SerializedName("ResellerName")
        private String resellername;
        @Expose
        @SerializedName("CustomerName")
        private String customername;
        @Expose
        @SerializedName("OrderId")
        private String orderid;

        public List<NotesEntity> getNotes() {
            return notes;
        }

        public String getCreateddatetime() {
            return createddatetime;
        }

        public String getMembershipdiscount() {
            return membershipdiscount;
        }

        public String getGrandtotal() {
            return grandtotal;
        }

        public String getShippingcharge() {
            return shippingcharge;
        }

        public String getGst() {
            return gst;
        }

        public String getSubtotal() {
            return subtotal;
        }

        public String getOrderstatusname() {
            return orderstatusname;
        }

        public String getOrderstatus() {
            return orderstatus;
        }

        public String getPaymentstatusname() {
            return paymentstatusname;
        }

        public String getPaymentstatus() {
            return paymentstatus;
        }

        public int getTotalquantity() {
            return totalquantity;
        }

        public List<ItemsEntity> getItems() {
            return items;
        }

        public String getCompleteaddress() {
            return completeaddress;
        }

        public String getCountry() {
            return country;
        }

        public String getState() {
            return state;
        }

        public String getPincode() {
            return pincode;
        }

        public String getCity() {
            return city;
        }

        public String getLandmark() {
            return landmark;
        }

        public String getStreet() {
            return street;
        }

        public String getBuilding() {
            return building;
        }

        public String getResellercontact() {
            return resellercontact;
        }

        public String getFromcontact() {
            return fromcontact;
        }

        public String getFromname() {
            return fromname;
        }

        public String getResellername() {
            return resellername;
        }

        public String getCustomername() {
            return customername;
        }

        public String getOrderid() {
            return orderid;
        }
    }

    public static class NotesEntity {
        @Expose
        @SerializedName("CreatedDateTime")
        private String createddatetime;
        @Expose
        @SerializedName("IsPublic")
        private int ispublic;
        @Expose
        @SerializedName("CreatedBy")
        private String createdby;
        @Expose
        @SerializedName("image")
        private String image;
        @Expose
        @SerializedName("Note")
        private String note;

        public String getCreateddatetime() {
            return createddatetime;
        }

        public int getIspublic() {
            return ispublic;
        }

        public String getCreatedby() {
            return createdby;
        }

        public String getImage() {
            return image;
        }

        public String getNote() {
            return note;
        }
    }

    public static class ItemsEntity {
        @Expose
        @SerializedName("Media")
        private List<String> media;
        @Expose
        @SerializedName("Type")
        private List<TypeEntity> type;
        @Expose
        @SerializedName("TotalPrice")
        private String totalprice;
        @Expose
        @SerializedName("Quantity")
        private String quantity;
        @Expose
        @SerializedName("Variant")
        private String variant;
        @Expose
        @SerializedName("ReviewRating")
        private boolean reviewrating;
        @Expose
        @SerializedName("IsActive")
        private String isactive;
        @Expose
        @SerializedName("IsReturnRequested")
        private int isreturnrequested;
        @Expose
        @SerializedName("ReturnValidUpto")
        private String returnvalidupto;
        @Expose
        @SerializedName("OrderDetailId")
        private String orderdetailid;
        @Expose
        @SerializedName("ItemPrice")
        private String itemprice;
        @Expose
        @SerializedName("PaymentMethod")
        private String paymentmethod;
        @Expose
        @SerializedName("OrderItemStatusName")
        private String orderitemstatusname;
        @Expose
        @SerializedName("OrderStatus")
        private String orderstatus;
        @Expose
        @SerializedName("TrackingUrl")
        private String trackingurl;
        @Expose
        @SerializedName("TrackingCode")
        private String trackingcode;
        @Expose
        @SerializedName("CategoryName")
        private String categoryname;
        @Expose
        @SerializedName("SKU")
        private String sku;
        @Expose
        @SerializedName("Title")
        private String title;
        @Expose
        @SerializedName("VariationId")
        private String variationid;
        @Expose
        @SerializedName("ProductId")
        private String productid;

        public List<String> getMedia() {
            return media;
        }

        public List<TypeEntity> getType() {
            return type;
        }

        public String getTotalprice() {
            return totalprice;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getVariant() {
            return variant;
        }

        public boolean getReviewrating() {
            return reviewrating;
        }

        public String getIsactive() {
            return isactive;
        }

        public int getIsreturnrequested() {
            return isreturnrequested;
        }

        public String getReturnvalidupto() {
            return returnvalidupto;
        }

        public String getOrderdetailid() {
            return orderdetailid;
        }

        public String getItemprice() {
            return itemprice;
        }

        public String getPaymentmethod() {
            return paymentmethod;
        }

        public String getOrderitemstatusname() {
            return orderitemstatusname;
        }

        public String getOrderstatus() {
            return orderstatus;
        }

        public String getTrackingurl() {
            return trackingurl;
        }

        public String getTrackingcode() {
            return trackingcode;
        }

        public String getCategoryname() {
            return categoryname;
        }

        public String getSku() {
            return sku;
        }

        public String getTitle() {
            return title;
        }

        public String getVariationid() {
            return variationid;
        }

        public String getProductid() {
            return productid;
        }
    }

    public static class TypeEntity {
        @Expose
        @SerializedName("VariantValue")
        private String variantvalue;
        @Expose
        @SerializedName("VariantName")
        private String variantname;

        public String getVariantvalue() {
            return variantvalue;
        }

        public String getVariantname() {
            return variantname;
        }
    }
}
