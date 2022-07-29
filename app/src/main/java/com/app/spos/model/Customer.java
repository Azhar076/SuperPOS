package com.app.spos.model;

import com.google.gson.annotations.SerializedName;

public class Customer {


    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("customer_email")
    private String customerEmail;

    @SerializedName("customer_cell")
    private String customerCell;

    @SerializedName("customer_address")
    private String customerAddress;

    @SerializedName("value")
    private String value;
    @SerializedName("message")
    private String massage;
    @SerializedName("vat_no")
    private String vat_no;

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerCell() {
        return customerCell;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }


    public String getValue() {
        return value;
    }

    public String getMassage() {
        return massage;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerCell(String customerCell) {
        this.customerCell = customerCell;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getVat_no() {
        return vat_no;
    }

    public void setVat_no(String vat_no) {
        this.vat_no = vat_no;
    }
}