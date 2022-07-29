package com.app.spos.model;

import com.google.gson.annotations.SerializedName;

public class OrderDetails {



    @SerializedName("order_details_id")
    private String orderDetailsId;

    @SerializedName("invoice_id")
    private String invoiceId;

    @SerializedName("product_order_date")
    private String productOrderDate;
    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_quantity")
    private String productQuantity;

    @SerializedName("product_weight")
    private String productWeight;

    @SerializedName("product_price")
    private String productPrice;

    @SerializedName("value")
    private String value;

    @SerializedName("product_image")
    private String productImage;
    @SerializedName("paid_amount")
    private String paidAmount;
    @SerializedName("due_amount")
    private String dueAmount;


    public String getInvoiceId() {
        return invoiceId;
    }


    public String getProductName() {
        return productName;
    }

    public String getProductOrderDate() {
        return productOrderDate;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getProductPrice() {
        return productPrice;
    }


    public String getValue() {
        return value;
    }

    public String getProductImage() {
        return productImage;
    }


    public String getProductWeight() {
        return productWeight;
    }


    public String getOrderDetailsId() {
        return orderDetailsId;
    }


    public void setOrderDetailsId(String orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public void setProductOrderDate(String productOrderDate) {
        this.productOrderDate = productOrderDate;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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