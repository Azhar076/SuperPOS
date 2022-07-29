package com.app.spos.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class getAllCustomersReceiveModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("pay_date")
    @Expose
    private String payDate;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("created_by")
    @Expose
    private String created_by;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
}
