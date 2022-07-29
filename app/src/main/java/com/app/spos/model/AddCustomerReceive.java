package com.app.spos.model;

public class AddCustomerReceive {

    String customer_id;
    String pay_date;
    String note;
    String amount;
    String pay_type;
    String shop_id;
    String owner_id;
    String staff_id;

    public AddCustomerReceive(String customer_id, String pay_date, String note, String amount, String pay_type, String shop_id, String owner_id, String staff_id) {
        this.customer_id = customer_id;
        this.pay_date = pay_date;
        this.note = note;
        this.amount = amount;
        this.pay_type = pay_type;
        this.shop_id = shop_id;
        this.owner_id = owner_id;
        this.staff_id = staff_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }
}
