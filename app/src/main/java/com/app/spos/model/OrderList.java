package com.app.spos.model;

import com.google.gson.annotations.SerializedName;

public class OrderList {



    @SerializedName("order_id")
    private String orderId;

    @SerializedName("invoice_id")
    private String invoiceId;

    @SerializedName("order_date")
    private String orderDate;
    @SerializedName("order_time")
    private String orderTime;

    @SerializedName("order_type")
    private String orderType;


    @SerializedName("order_price")
    private String orderPrice;


    @SerializedName("order_payment_method")
    private String orderPaymentMethod;

    @SerializedName("discount")
    private String discount;

    @SerializedName("tax")
    private String tax;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("order_note")
    private String orderNote;


    @SerializedName("served_by")
    private String servedBy;

    @SerializedName("value")
    private String value;

    @SerializedName("paid_amount")
    private String paidAmount;
    @SerializedName("due_amount")
    private String dueAmount;
    public String getInvoiceId() {
        return invoiceId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getOrderPaymentMethod() {
        return orderPaymentMethod;
    }


    public String getValue() {
        return value;
    }

    public String getServedBy() {
        return servedBy;
    }

    public String getTax() {
        return tax;
    }

    public String getDiscount() {
        return discount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public String getOrderPrice() {
        return orderPrice;
    }


    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setOrderPaymentMethod(String orderPaymentMethod) {
        this.orderPaymentMethod = orderPaymentMethod;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public void setServedBy(String servedBy) {
        this.servedBy = servedBy;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(String dueAmount) {
        this.dueAmount = dueAmount;
    }
}