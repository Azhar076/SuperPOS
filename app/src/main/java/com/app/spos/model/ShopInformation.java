package com.app.spos.model;

import com.google.gson.annotations.SerializedName;

public class ShopInformation {


    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("shop_name")
    private String shopName;
    @SerializedName("shop_email")
    private String shopEmail;

    @SerializedName("shop_contact")
    private String shopContact;

    @SerializedName("shop_address")
    private String shopAddress;

    @SerializedName("value")
    private String value;

    @SerializedName("tax")
    private String tax;
    @SerializedName("shop_vat_no")
    private String shop_vat_no;
    @SerializedName("logo")
    private String logo;
    @SerializedName("tax_type")
    private String tax_type;


    public String getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopEmail() {
        return shopEmail;
    }

    public String getShopContact() {
        return shopContact;
    }

    public String getShopAddress() {
        return shopAddress;
    }


    public String getValue() {
        return value;
    }

    public String getTax() {
        return tax;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopEmail(String shopEmail) {
        this.shopEmail = shopEmail;
    }

    public void setShopContact(String shopContact) {
        this.shopContact = shopContact;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getShop_vat_no() {
        return shop_vat_no;
    }

    public void setShop_vat_no(String shop_vat_no) {
        this.shop_vat_no = shop_vat_no;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTax_type() {
        return tax_type;
    }

    public void setTax_type(String tax_type) {
        this.tax_type = tax_type;
    }
}