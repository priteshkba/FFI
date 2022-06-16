package com.ffi.productdetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Varient {

    @SerializedName("ID")
    @Expose
    private Integer iD = 0;

    @SerializedName("Name")
    @Expose
    private String value = "";

    @SerializedName("Code")
    @Expose
    private String Code = "";

    public Integer quantity = 0;
    public Boolean isQuantityFound = false;

    @SerializedName("isSelected")
    @Expose
    private Boolean isSelected = false;

    Boolean _isEnable = true;

    Boolean shouldDisplay = false;

    public Integer getiD() {
        return iD;
    }

    public void setiD(Integer iD) {
        this.iD = iD;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean get_isEnable() {
        return _isEnable;
    }

    public void set_isEnable(Boolean _isEnable) {
        this._isEnable = _isEnable;
    }


    public Boolean getShouldDisplay() {
        return shouldDisplay;
    }

    public void setShouldDisplay(Boolean shouldDisplay) {
        this.shouldDisplay = shouldDisplay;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getQuantityFound() {
        return isQuantityFound;
    }

    public void setQuantityFound(Boolean quantityFound) {
        isQuantityFound = quantityFound;
    }

    @Override
    public String toString() {
        return "Varient{" +
                "iD=" + iD +
                ", value='" + value + '\'' +
                ", Code='" + Code + '\'' +
                ", isSelected=" + isSelected +
                ", _isEnable=" + _isEnable +
                ", shouldDisplay=" + shouldDisplay +
                '}';
    }
}
