package com.ffi.my_orderdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class AddressDetailResponse {
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
        @SerializedName("PhoneNumber")
        private String phonenumber;
        @Expose
        @SerializedName("FromContact")
        private String fromcontact;
        @Expose
        @SerializedName("FromName")
        private String fromname;
        @Expose
        @SerializedName("ResellerContact")
        private String resellercontact;
        @Expose
        @SerializedName("ResellerName")
        private String resellername;
        @Expose
        @SerializedName("CustomerName")
        private String customername;
        @Expose
        @SerializedName("UserId")
        private String userid;
        @Expose
        @SerializedName("ID")
        private String id;

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

        public String getPhonenumber() {
            return phonenumber;
        }

        public String getFromcontact() {
            return fromcontact;
        }

        public String getFromname() {
            return fromname;
        }

        public String getResellercontact() {
            return resellercontact;
        }

        public String getResellername() {
            return resellername;
        }

        public String getCustomername() {
            return customername;
        }

        public String getUserid() {
            return userid;
        }

        public String getId() {
            return id;
        }
    }
}
