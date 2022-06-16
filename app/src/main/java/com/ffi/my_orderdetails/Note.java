package com.ffi.my_orderdetails;

import com.google.gson.annotations.SerializedName;

public class Note{
    @SerializedName("Note")
    public String note;
    public String image;
    @SerializedName("CreatedBy")
    public String createdBy;
    @SerializedName("IsPublic")
    public int isPublic;
    @SerializedName("CreatedDateTime")
    public String createdDateTime;
}
